"""
Rule strengthening: submits existing Semgrep rules to an LLM to identify
deficiencies and produce improved versions.

Uses multithreading for concurrent processing of many rule files.
"""

import os
import re
import subprocess
import logging
from concurrent.futures import ThreadPoolExecutor, as_completed

import yaml

import llmevolucore.common as common

logging.basicConfig(
    filename="LLMmakerule2.log",
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
)


def log_and_print(message: str) -> None:
    """Print and log a message."""
    print(message)
    logging.info(message)


# ---------------------------------------------------------------------------
# API response parsing
# ---------------------------------------------------------------------------


def _parse_deficiency(api_response: str) -> dict:
    """
    Determine whether the API reports a deficiency and, if so, extract the
    modified rules YAML block.
    """
    is_deficient = False
    modified_rules = None

    deficiency_match = re.search(
        r"Is there a deficiency\*?\*?\*?\*?:\*?\*?\*?\*?\s(True|False)",
        api_response,
        re.IGNORECASE,
    )
    if deficiency_match:
        is_deficient = deficiency_match.group(1).strip().lower() == "true"

    modified_match = re.search(
        r"Modified rules\*?\*?\*?\*?:\*?\*?\*?\*?\s*```yaml\n(.*?)\n```",
        api_response,
        re.DOTALL | re.IGNORECASE | re.MULTILINE,
    )
    if modified_match:
        modified_rules = modified_match.group(1).strip()

    return {"is_deficient": is_deficient, "modified_rules": modified_rules}


# ---------------------------------------------------------------------------
# File helpers
# ---------------------------------------------------------------------------


def _save_to_file(rule, save_path: str) -> None:
    """Save a rule (dict or string) to *save_path*."""
    os.makedirs(os.path.dirname(save_path), exist_ok=True)
    with open(save_path, "w", encoding="utf-8") as f:
        if isinstance(rule, dict):
            rule = yaml.dump(rule, default_flow_style=False)
            log_and_print("YAML dumped from dict")
        f.write(rule)
    log_and_print(f"Rule saved to: {save_path}")


def _get_relative_path(rule_id: str, file_path: str, input_folder: str) -> str:
    """Build a relative save path mirroring the input folder structure."""
    file_name = os.path.basename(file_path).split(".")[0]
    relative_path = file_path[len(input_folder):].strip(os.sep)
    relative_folder = os.path.dirname(relative_path)
    return os.path.join(relative_folder, file_name, rule_id + ".yaml")


# ---------------------------------------------------------------------------
# Validation
# ---------------------------------------------------------------------------


def _run_semgrep_validate(rules_folder: str, output_file: str) -> None:
    """Run ``semgrep --validate`` on a folder, writing JSON results."""
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
        log_and_print("Semgrep validation completed successfully.")
    except subprocess.CalledProcessError as e:
        log_and_print(f"Error occurred while running semgrep: {e.stderr}")


def _debug_rule(save_rule, thread_id: int) -> bool:
    """Return True if *save_rule* passes ``semgrep --validate``."""
    path = f"/data/fxizenta/LLMmakerule2/debug/debug_{thread_id}.yaml"
    debug_json_path = (
        f"/data/fxizenta/LLMmakerule2/debug/debug_{thread_id}.json"
    )
    _save_to_file(save_rule, path)
    if os.path.exists(debug_json_path):
        os.remove(debug_json_path)
    _run_semgrep_validate(path, debug_json_path)
    if os.path.exists(debug_json_path):
        with open(debug_json_path, "r", encoding="utf-8") as f:
            if f.read():
                log_and_print(f"debug_json: {debug_json_path}")
                return False
    return True


# ---------------------------------------------------------------------------
# Main processing
# ---------------------------------------------------------------------------


def _process_file(
    file_path: str,
    input_folder: str,
    output_folder: str,
    thread_id: int,
) -> None:
    """Read rules from a single YAML file, submit each to the API, and save."""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rules = yaml.safe_load(f)
            if "rules" not in rules:
                log_and_print(f"File {file_path} does not contain rules")
                return

            log_and_print(f"Processing file: {file_path}\nrules: {rules['rules']}")
            for rule in rules["rules"]:
                rule_prompt = yaml.dump(rule, default_flow_style=False)
                rule_id = rule["id"]
                relative_path = _get_relative_path(rule_id, file_path, input_folder)
                save_path = os.path.join(output_folder, relative_path)

                if os.path.exists(save_path):
                    log_and_print(f"Rule already exists: {save_path}")
                    if os.path.getsize(save_path) > 0:
                        log_and_print(
                            f"Rule already exists and is not empty: {save_path}"
                        )
                        continue
                    log_and_print(
                        f"Rule already exists but is empty: {save_path}; "
                        f"re-requesting"
                    )

                log_and_print(f"Sending rule to API: {rule_id}")
                api_response = common.send_request(rule_prompt)

                if not api_response:
                    log_and_print(
                        f"Rule {rule_id} did not get a valid response"
                    )
                    continue

                deficiency = _parse_deficiency(api_response)
                while (
                    deficiency["is_deficient"]
                    and deficiency["modified_rules"] is None
                ):
                    log_and_print(
                        f"Rule {rule_id} has deficiencies but no modified "
                        f"rule found, re-requesting"
                    )
                    api_response = common.send_request(rule_prompt)
                    if not api_response:
                        break
                    deficiency = _parse_deficiency(api_response)

                if not deficiency["is_deficient"]:
                    save_rule = {"rules": [rule]}
                else:
                    save_rule = deficiency["modified_rules"]
                    if not _debug_rule(save_rule, thread_id):
                        log_and_print(
                            f"Rule {rule_id} generated rule is not a "
                            f"syntactically correct YAML file"
                        )
                        save_rule = {"rules": [rule]}

                _save_to_file(save_rule, save_path)

    except yaml.YAMLError as e:
        log_and_print(f"Error parsing YAML file: {e}")


def parse_and_save_rules_with_api_multithreaded(
    input_folder: str,
    output_folder: str,
    max_threads: int = 10,
) -> None:
    """Process all rule files under *input_folder* in parallel."""
    file_paths = []
    for root, _, files in os.walk(input_folder):
        for file in files:
            if file.endswith((".yaml", ".yml", ".noscan")):
                file_paths.append(os.path.join(root, file))

    with ThreadPoolExecutor(max_threads) as executor:
        future_to_file = {
            executor.submit(
                _process_file, fp, input_folder, output_folder, tid
            ): fp
            for tid, fp in enumerate(file_paths)
        }
        for future in as_completed(future_to_file):
            file_path = future_to_file[future]
            try:
                future.result()
            except Exception as e:
                log_and_print(f"Error processing file {file_path}: {e}")


if __name__ == "__main__":
    input_rules_folder = ""
    output_rules_folder = ""
    parse_and_save_rules_with_api_multithreaded(
        input_rules_folder, output_rules_folder, max_threads=10
    )
