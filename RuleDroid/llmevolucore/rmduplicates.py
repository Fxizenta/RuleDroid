"""
Used to detect and remove duplicate Semgrep rules.
"""

import os
import re
import time
from collections import defaultdict
from typing import Optional

import requests
import yaml

import llmevolucore.common as common
from llog import log_and_print


def find_all_files(root_dir: str, extensions: tuple[str, ...] = (".yml", ".yaml")):
    """Yield every file under *root_dir* whose suffix is in *extensions*."""
    for root, _, files in os.walk(root_dir):
        for file in files:
            if file.endswith(extensions):
                yield os.path.join(root, file)


def normalize_api(api: str) -> str:
    """Normalize ``$Var.methodName`` → ``$Var`` so variables collapse."""
    if api.startswith("$"):
        parts = api.split(".", 1)
        if len(parts) == 2:
            return f"$Var.{parts[1]}"
        return "$Var"
    return api


def extract_apis_from_patterns(patterns) -> set[str]:
    """Extract and normalize Java/Android API references from Semgrep patterns."""
    api_pattern = re.compile(
        r'(\$[a-zA-Z_][\w]*(?:\.[a-zA-Z_][\w]*)*|[a-zA-Z_][\w]*\.[a-zA-Z_][\w]*)\b'
    )
    apis: set[str] = set()

    def _collect(value: str) -> None:
        for api in api_pattern.findall(value):
            apis.add(normalize_api(api))

    if isinstance(patterns, list):
        for pattern in patterns:
            if isinstance(pattern, dict):
                for key, value in pattern.items():
                    if isinstance(value, str):
                        _collect(value)
                    elif isinstance(value, list):
                        apis.update(extract_apis_from_patterns(value))
    elif isinstance(patterns, str):
        _collect(patterns)

    return apis


def parse_semgrep_rule(file_path: str):
    """Parse a Semgrep YAML file and yield (apis, rule_id, rule_dict) tuples."""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rule_content = yaml.safe_load(f)
            if isinstance(rule_content, dict) and "rules" in rule_content:
                for rule in rule_content["rules"]:
                    rule_id = rule.get("id", "unknown")
                    file_apis: set[str] = set()
                    for key in (
                        "patterns",
                        "pattern-either",
                        "pattern-not",
                        "pattern-inside",
                        "pattern-not-inside",
                        "pattern",
                    ):
                        if key in rule:
                            file_apis.update(extract_apis_from_patterns(rule[key]))
                    if file_apis:
                        yield file_apis, rule_id, rule
    except Exception as e:
        print(f"Error parsing file {file_path}: {e}")


def group_rules_by_api(root_dir: str) -> dict[str, list]:
    """Group Semgrep rules by the Java/Android APIs they reference."""
    api_groups: dict[str, list] = defaultdict(list)
    for file_path in find_all_files(root_dir):
        for apis, rule_id, rule_content in parse_semgrep_rule(file_path):
            for api in apis:
                api_groups[api].append((rule_id, file_path, rule_content))
    return api_groups


def _parse_duplicate_response(response: str) -> dict:
    """Parse the LLM response about duplicate rules."""
    result: dict = {}
    response = response.replace("*", "")

    dup_match = re.search(
        r"\*?\*?\s*Duplicates\*?\*?\s*\*?\*?:\*?\*?\s*(True|False|Unknown)",
        response,
        re.IGNORECASE,
    )
    result["Duplicates"] = (
        dup_match.group(1).capitalize() if dup_match else "Unknown"
    )

    if result["Duplicates"] in ("False", "Unknown"):
        return result

    sections = response.split("---")
    repeats: dict = {}
    for section in sections:
        repeat_match = re.search(
            r"Repeat\s+(\d+)((?:\s+rule\d+\s*:\s*\S+)+)\s+The Best\s*:\s*(\S+)",
            section,
            re.IGNORECASE,
        )
        if repeat_match:
            repeat_id = repeat_match.group(1)
            rules_block = repeat_match.group(2)
            best = repeat_match.group(3)
            rules = re.findall(r"rule\d+\s*:\s*(\S+)", rules_block, re.IGNORECASE)
            repeat_key = f"Repeat {repeat_id}"
            repeats[repeat_key] = {
                f"rule{i + 1}": rule for i, rule in enumerate(rules)
            }
            repeats[repeat_key]["The Best"] = best

    result.update(repeats)
    return result


def _validate_response_format(response: str) -> bool:
    """Check that the API response has the expected structure."""
    if "---" not in response or not re.search(r"(?m)^---$", response):
        log_and_print("Invalid response format: missing delimiter ---")
        return False

    repeat_blocks = re.findall(r"(?<=---).*?Repeat", response, re.S)
    if len(repeat_blocks) != response.count("Repeat"):
        log_and_print(
            "Invalid response format: missing delimiter between Repeat blocks"
        )
        return False

    return True


def check_duplicate_rules(api: str, entries: list) -> list[str]:
    """Query the LLM to identify which rules are duplicates for a given API."""
    prompt = ""
    for rule_id, file_path, rule_content in entries:
        prompt += f"ruleid: {rule_id}\n```yaml\n"
        prompt += yaml.dump(rule_content, allow_unicode=True)
        prompt += "\n```\n---"

    while True:
        response = common.send_request(prompt, chat_id="o3mini", timeout=560)
        if response is None:
            log_and_print("Request failed, retrying...")
            continue
        log_and_print(f"response:\n{response}")
        result = _parse_duplicate_response(response)
        if result["Duplicates"] in ("False", "Unknown"):
            log_and_print(f"\nfile_path: {file_path}\nAPI: {api}")
            log_and_print("duplicate result: " + result["Duplicates"])
            return []
        if not _validate_response_format(response):
            log_and_print("Invalid format, resending request...")
            continue
        break

    del_list: list[str] = []
    best_list: list[str] = []
    all_rule: list[str] = []

    log_and_print(f"\nfile_path: {file_path}\nAPI: {api}")
    log_and_print("duplicate result: " + result["Duplicates"])
    log_and_print("duplicate rules:")
    for repeat_key, rules in result.items():
        if repeat_key.startswith("Repeat"):
            best_list.append(rules.pop("The Best"))
            log_and_print(f"\n{repeat_key}:")
            log_and_print(f"\n{rules}:")
            all_rule.extend(rules.values())

    for rule in all_rule:
        if rule not in best_list:
            del_list.append(rule)
            log_and_print(f"Deleting rule: {rule}")

    log_and_print(f"del_list: {del_list}")
    log_and_print(f"best_list: {best_list}")
    return del_list


def delete_duplicate_rules(
    api: str, del_list: list[str], entries: list
) -> None:
    """Remove rules whose IDs appear in *del_list* from their YAML files."""
    for rule_id, file_path, rule_content in entries:
        if rule_id not in del_list:
            continue
        try:
            with open(file_path, "r", encoding="utf-8") as f:
                data = yaml.safe_load(f)

            if "rules" not in data or not isinstance(data["rules"], list):
                raise ValueError(
                    "Invalid YAML structure: 'rules' section missing or not a list."
                )

            if len(data["rules"]) == 1:
                log_and_print(
                    f"Only one rule found in {file_path}. Deleting the entire file."
                )
                os.remove(file_path)
                continue

            original_length = len(data["rules"])
            data["rules"] = [
                r for r in data["rules"] if r.get("id") != rule_id
            ]

            if len(data["rules"]) == original_length:
                log_and_print(
                    f"Rule with ID '{rule_id}' not found in {file_path}."
                )
                continue

            with open(file_path, "w", encoding="utf-8") as f:
                yaml.safe_dump(data, f, sort_keys=False)

            log_and_print(
                f"Rule with ID '{rule_id}' successfully deleted from {file_path}."
            )
        except Exception as e:
            log_and_print(f"Error processing file {file_path}: {e}")

    log_and_print(f"{api} completed.")
    log_and_print("=" * 50)


def rule_exists_in_file(rule_id: str, file_path: str) -> bool:
    """Check whether *rule_id* still exists in the YAML file."""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rule_content = yaml.safe_load(f)
            if isinstance(rule_content, dict) and "rules" in rule_content:
                for rule in rule_content["rules"]:
                    if rule.get("id") == rule_id:
                        return True
    except Exception as e:
        print(f"Error checking file {file_path}: {e}")
    log_and_print(f"Rule {rule_id} no longer exists in file {file_path}.")
    return False


def rm_duplicates(root_dir: str) -> None:
    """Main entry point: detect and remove duplicate Semgrep rules."""
    if not os.path.isdir(root_dir):
        print("Invalid directory path.")
        return

    api_groups = group_rules_by_api(root_dir)
    black_list = ["e.g", "$Var"]
    print("\nAPI Groups (with more than 2 rules):")
    num = 0

    for api, entries in api_groups.items():
        valid_entries = [
            e for e in entries if rule_exists_in_file(e[0], e[1])
        ]
        if len(valid_entries) >= 2 and api not in black_list:
            num += 1
            log_and_print("=" * 50)
            log_and_print(f"\nAPI: {api}")
            print("Rules:")
            for rule_id, file_path, rule_content in entries:
                log_and_print(f"- Rule ID: {rule_id}")
                log_and_print(f"    File: {file_path}:{rule_id}")
            dup_list = check_duplicate_rules(api, entries)
            print(f"Rules to delete: {dup_list}")
            delete_duplicate_rules(api, dup_list, entries)

    print(f"\nFound {num} API groups.")


if __name__ == "__main__":
    root_dir = ""
    rm_duplicates(root_dir)
