"""
Rename duplicate Semgrep rule IDs by appending a numeric suffix.

Two rules with the same ID in different files would otherwise cause conflicts
when loaded together by Semgrep.
"""

import os

import yaml


def process_yaml_files(directory: str) -> None:
    """
    Walk *directory* for ``.yaml``/``.yml`` files and de-duplicate rule IDs
    by appending ``_N`` suffixes to duplicates.
    """
    rule_id_counts: dict[str, int] = {}

    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith((".yaml", ".yml")):
                file_path = os.path.join(root, file)
                _process_file(file_path, rule_id_counts)


def _process_file(file_path: str, rule_id_counts: dict[str, int]) -> None:
    """Modify duplicate rule IDs in a single YAML file."""
    with open(file_path, "r", encoding="utf-8") as f:
        try:
            data = yaml.safe_load(f)
        except yaml.YAMLError as e:
            print(f"Error parsing YAML file {file_path}: {e}")
            return

    if not data or "rules" not in data:
        return

    for rule in data["rules"]:
        if "id" not in rule:
            continue
        original_id = rule["id"]
        if original_id in rule_id_counts:
            rule_id_counts[original_id] += 1
            rule["id"] = f"{original_id}_{rule_id_counts[original_id]}"
            print(f"Modified rule ID: {original_id} -> {rule['id']}")
        else:
            rule_id_counts[original_id] = 0

    with open(file_path, "w", encoding="utf-8") as f:
        yaml.dump(data, f, allow_unicode=True)


def rename_dup(directory_path: str) -> None:
    """Entry point: check and de-duplicate rule IDs under *directory_path*."""
    if os.path.exists(directory_path) and os.path.isdir(directory_path):
        process_yaml_files(directory_path)
        print("Processing completed!")
    else:
        print("Invalid directory path. Please check and try again.")


if __name__ == "__main__":
    directory_path = ""
    rename_dup(directory_path)
