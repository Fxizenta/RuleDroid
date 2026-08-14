"""Static quality checks for the bundled Semgrep rule collection."""

from __future__ import annotations

import argparse
import os
import subprocess
from collections import defaultdict
from pathlib import Path
from typing import Any, Iterable, List, Sequence

import yaml


REQUIRED_FIELDS = ("id", "languages", "message", "severity")
MATCH_FIELDS = (
    "pattern",
    "patterns",
    "pattern-either",
    "pattern-regex",
    "mode",
)
ALLOWED_SEVERITIES = {"INFO", "WARNING", "ERROR"}
PLACEHOLDERS = (
    "Your Name",
    "callOtherMethodThatCreatesPackageContext",
    "collectSensitiveData",
    "com.somepackage",
)


class UniqueKeyLoader(yaml.SafeLoader):
    """Safe YAML loader that rejects keys hidden by last-value-wins parsing."""


def _construct_unique_mapping(loader: UniqueKeyLoader, node: yaml.MappingNode, deep=False):
    loader.flatten_mapping(node)
    mapping = {}
    for key_node, value_node in node.value:
        key = loader.construct_object(key_node, deep=deep)
        if key in mapping:
            raise yaml.constructor.ConstructorError(
                "while constructing a mapping",
                node.start_mark,
                f"found duplicate key {key!r}",
                key_node.start_mark,
            )
        mapping[key] = loader.construct_object(value_node, deep=deep)
    return mapping


UniqueKeyLoader.add_constructor(
    yaml.resolver.BaseResolver.DEFAULT_MAPPING_TAG,
    _construct_unique_mapping,
)

# These fields appeared in the original LLM-generated rules but are not Semgrep
# rule operators.  Semgrep can ignore unknown fields while still returning a
# successful validation result, which makes the rule look active when part of
# its intended filtering or data-flow logic is actually absent.
IGNORED_RULE_FIELDS = {
    "condition",
    "metavariable-types",
    "metavariables",
    "multiline",
    "path",
    "path-ignore",
    "path-propagation",
    "path-regex",
    "path-suffix",
    "pattern-not-either",
    "patterns-not",
    "sanitization",
    "taint-tracking",
}

SCALAR_PATTERN_OPERATORS = {
    "pattern",
    "pattern-inside",
    "pattern-not",
    "pattern-not-inside",
    "pattern-not-regex",
    "pattern-regex",
}
LIST_PATTERN_OPERATORS = {
    "pattern-either",
    "pattern-propagators",
    "pattern-sanitizers",
    "pattern-sinks",
    "pattern-sources",
    "patterns",
}
METAVARIABLE_OPERATORS = {
    "metavariable-analysis",
    "metavariable-comparison",
    "metavariable-name",
    "metavariable-pattern",
    "metavariable-regex",
    "metavariable-type",
}


def _operator_shape_problems(node: Any, location: str) -> List[str]:
    """Find operator values whose YAML shape cannot express Semgrep semantics."""
    problems: List[str] = []
    if isinstance(node, list):
        for index, value in enumerate(node, start=1):
            problems.extend(_operator_shape_problems(value, f"{location}[{index}]"))
        return problems
    if not isinstance(node, dict):
        return problems

    for operator, value in node.items():
        operator_location = f"{location}.{operator}"
        if operator in SCALAR_PATTERN_OPERATORS and not isinstance(value, str):
            problems.append(
                f"{operator_location}: {operator} must be a string, not "
                f"{type(value).__name__}"
            )
        elif operator in LIST_PATTERN_OPERATORS and not isinstance(value, list):
            problems.append(
                f"{operator_location}: {operator} must be a list, not "
                f"{type(value).__name__}"
            )
        elif operator in METAVARIABLE_OPERATORS:
            if not isinstance(value, dict):
                problems.append(
                    f"{operator_location}: {operator} must be a mapping, not "
                    f"{type(value).__name__}"
                )
            elif not isinstance(value.get("metavariable"), str):
                problems.append(
                    f"{operator_location}: {operator} requires a string "
                    "metavariable field"
                )
        problems.extend(_operator_shape_problems(value, operator_location))
    return problems


def rule_files(root: Path) -> Iterable[Path]:
    """Yield all YAML rule files in deterministic order."""
    yield from sorted(root.rglob("*.yaml"))
    yield from sorted(root.rglob("*.yml"))


def audit_rules(root: Path) -> List[str]:
    """Return structural and maintainability problems in *root*."""
    problems: List[str] = []
    ids = defaultdict(list)

    for path in rule_files(root):
        relative = path.relative_to(root)
        try:
            document = yaml.load(
                path.read_text(encoding="utf-8"), Loader=UniqueKeyLoader
            )
        except (OSError, UnicodeError, yaml.YAMLError) as error:
            problems.append(f"{relative}: invalid YAML: {error}")
            continue

        if not isinstance(document, dict) or not isinstance(document.get("rules"), list):
            problems.append(f"{relative}: top level must contain a rules list")
            continue

        for index, rule in enumerate(document["rules"], start=1):
            location = f"{relative}:rule[{index}]"
            if not isinstance(rule, dict):
                problems.append(f"{location}: rule must be a mapping")
                continue

            for field in REQUIRED_FIELDS:
                if field not in rule or rule[field] in (None, "", []):
                    problems.append(f"{location}: missing non-empty {field}")

            rule_id = rule.get("id")
            if isinstance(rule_id, str) and rule_id:
                ids[rule_id].append(location)

            languages = rule.get("languages")
            if not isinstance(languages, list) or not all(
                isinstance(language, str) and language.strip() == language
                for language in languages
            ):
                problems.append(f"{location}: languages must be a list of clean strings")

            severity = rule.get("severity")
            if severity not in ALLOWED_SEVERITIES:
                problems.append(
                    f"{location}: unsupported severity {severity!r}; "
                    f"expected one of {sorted(ALLOWED_SEVERITIES)}"
                )

            if not any(field in rule for field in MATCH_FIELDS):
                problems.append(f"{location}: no matching operator")

            ignored_fields = sorted(IGNORED_RULE_FIELDS.intersection(rule))
            for field in ignored_fields:
                problems.append(
                    f"{location}: unsupported field {field!r} would be ignored "
                    "by Semgrep"
                )

            problems.extend(_operator_shape_problems(rule, location))

            patterns = rule.get("patterns")
            if isinstance(patterns, list):
                primary_positives = sum(
                    1
                    for item in patterns
                    if isinstance(item, dict)
                    and "pattern" in item
                )
                if primary_positives > 1:
                    problems.append(
                        f"{location}: {primary_positives} positive patterns are ANDed; "
                        "use pattern-either or an enclosing pattern when alternatives or "
                        "cross-statement sequencing are intended"
                    )

            serialized = path.read_text(encoding="utf-8")
            for placeholder in PLACEHOLDERS:
                if placeholder in serialized:
                    problems.append(f"{location}: placeholder text {placeholder!r}")

    for rule_id, locations in sorted(ids.items()):
        if len(locations) > 1:
            problems.append(
                f"duplicate rule id {rule_id!r}: " + ", ".join(locations)
            )

    return problems


def validate_with_semgrep(root: Path, semgrep: str = "semgrep") -> List[str]:
    """Compile every rule with Semgrep and return validation failures."""
    environment = os.environ.copy()
    environment["SEMGREP_SEND_METRICS"] = "off"
    completed = subprocess.run(
        [semgrep, "--validate", "--config", str(root), "--json"],
        capture_output=True,
        text=True,
        env=environment,
    )
    if completed.returncode == 0:
        return []
    output = completed.stdout.strip() or completed.stderr.strip()
    return [f"Semgrep validation failed (exit {completed.returncode}): {output}"]


def run(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "rules",
        nargs="?",
        type=Path,
        default=Path(__file__).resolve().parents[1] / "RuleDroid_1_Rule",
    )
    parser.add_argument(
        "--semgrep-bin",
        help="also compile all rules with this Semgrep executable",
    )
    arguments = parser.parse_args(argv)

    problems = audit_rules(arguments.rules)
    if arguments.semgrep_bin:
        problems.extend(validate_with_semgrep(arguments.rules, arguments.semgrep_bin))

    if problems:
        for problem in problems:
            print(problem)
        return 1

    print(f"Rule quality checks passed for {arguments.rules}")
    return 0


if __name__ == "__main__":
    raise SystemExit(run())
