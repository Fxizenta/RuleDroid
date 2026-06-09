"""
Shared utilities for Semgrep rule validation, debugging, and source extraction.

Used primarily by ``rulefix.py`` and ``rulefix_r2.py``.
"""

import json
import os
import re
import subprocess
from typing import Optional


def load_error_report(file_path: str) -> list[dict]:
    """
    Read a JSON error report file produced by ``semgrep --validate``.

    Returns the ``"errors"`` list, or an empty list if the file doesn't exist.
    """
    if not os.path.exists(file_path):
        return []
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)
    return data.get("errors", [])


def run_semgrep_validate(rules_folder: str, output_file: str) -> None:
    """
    Run ``semgrep --validate`` on *rules_folder* and write JSON output.

    If *output_file* already exists it is deleted first.
    """
    if os.path.exists(output_file):
        os.remove(output_file)

    command = [
        "semgrep",
        "-c", rules_folder,
        "--validate",
        "--json",
        "--output", output_file,
    ]
    try:
        subprocess.run(command, check=True, capture_output=True, text=True)
        print("Semgrep validation completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error occurred while running semgrep: {e.stderr}")


def find_debug_files(directory: str) -> list[str]:
    """Return a list of all files ending with ``.debug`` under *directory*."""
    debug_files: list[str] = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".debug"):
                debug_files.append(os.path.join(root, file))
    return debug_files


def extract_rule_ids_from_folder(folder_path: str) -> dict[str, str]:
    """
    Walk *folder_path* for ``.yaml`` files and extract Semgrep rule IDs via regex.

    Returns a dict mapping ``rule_id`` → ``file_path``.
    """
    rule_id_pattern = re.compile(r"-\s+id:\s+(\S+)")
    rules_dict: dict[str, str] = {}

    for root, _, files in os.walk(folder_path):
        for file in files:
            if not file.endswith(".yaml"):
                continue
            file_path = os.path.join(root, file)
            try:
                with open(file_path, "r", encoding="utf-8") as f:
                    content = f.read()
                for rule_id in rule_id_pattern.findall(content):
                    rules_dict[rule_id] = file_path
            except Exception as e:
                print(f"Error processing file {file_path}: {e}")

    return rules_dict


def get_rule_source_code(
    error: dict,
    rule_id_path: Optional[dict[str, str]] = None,
) -> tuple[Optional[str], Optional[str]]:
    """
    Extract the file path and source code corresponding to a validation *error*.

    Tries several heuristics in order:
    1. Direct ``path`` field.
    2. ``spans[0]["file"]``.
    3. A ``.yaml`` path embedded in the ``message`` field.
    4. Look up ``rule_id`` in *rule_id_path* dict.
    5. Fall back to a ``.yaml.debug`` variant of the resolved path.

    Returns
    -------
    (file_path, source_code) or (None, None) if unresolvable.
    """
    file_path: Optional[str] = error.get("path")

    if not file_path and "spans" in error and error["spans"]:
        file_path = error["spans"][0].get("file")

    if not file_path and "message" in error:
        match = re.search(r"/data/fxizenta/.*\.yaml", error["message"])
        if match:
            file_path = match.group(0)

    if not file_path and "rule_id" in error and rule_id_path:
        rule_id = error["rule_id"]
        file_path = rule_id_path.get(rule_id)
        if file_path:
            print(f"Found file_path by rule_id: {file_path}")

    if not file_path:
        print(
            f"Warning: No valid file path found for error: "
            f"{error.get('message', 'No message provided')}"
        )
        return None, None

    # Try the exact path first, then the .debug variant
    for candidate in (file_path, file_path + ".debug"):
        if os.path.exists(candidate):
            with open(candidate, "r", encoding="utf-8") as f:
                return file_path, f.read()

    raise FileNotFoundError(f"File not found: {file_path}")
