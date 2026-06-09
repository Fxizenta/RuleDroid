"""
Semgrep rule syntax validation and auto-fixing (original version).

Validates YAML rules with ``semgrep --validate``, sends errors to an LLM for
fixing, and iterates until rules pass or all attempts are exhausted.
"""

import json
import os
import re

import requests

import llmevolucore.common as common
from llmevolucore.rule_utils import (
    extract_rule_ids_from_folder,
    find_debug_files,
    get_rule_source_code,
    load_error_report,
    run_semgrep_validate,
)
from llog import log_and_print


# ---------------------------------------------------------------------------
# API helpers (thin wrappers that use the global config in common)
# ---------------------------------------------------------------------------


def _debug_code_with_local_api(source_code: str, error: dict) -> str | None:
    """Send source + error to the API and return the fixed code."""
    error_message = json.dumps(error, indent=2)
    prompt = (
        f"source code:\n{source_code}\n\n"
        f"error message:\n{error_message}\n\n"
        f"Please fix the code based on the above error information."
    )
    data = {
        "chatId": "abcd2",
        "stream": False,
        "detail": False,
        "messages": [{"role": "user", "content": prompt}],
    }
    headers = {
        "Authorization": f"Bearer {common.api_key}",
        "Content-Type": "application/json",
        "Connection": "close",
    }
    try:
        response = requests.post(
            common.api_base_url,
            headers=headers,
            data=json.dumps(data),
            timeout=300,
        )
        if response.status_code == 200:
            print("Ask API Success\n")
            return response.json()["choices"][0]["message"]["content"]
        print(f"Error: {response.status_code}", response.text)
    except requests.RequestException as e:
        print(f"API request failed: {e}")
    return None


def _auto_debug_errors(json_file: str, rule_id_path: dict[str, str]) -> None:
    """Iterate over validation errors and attempt to auto-fix each one."""
    errors = load_error_report(json_file)
    for error in errors:
        try:
            file_path, source_code = get_rule_source_code(error, rule_id_path)
            if not file_path:
                continue

            debugged_code = _debug_code_with_local_api(source_code, error)
            yaml_code = common.extract_yaml_from_response(debugged_code or "")
            if debugged_code and yaml_code:
                _save_debugged_code(file_path, yaml_code[0])
                log_and_print(
                    f"Successfully debugged and saved code for: {file_path}"
                )
        except Exception as e:
            print(
                f"Failed to debug error in file "
                f"{error.get('path', 'unknown')}: {e}"
            )


def _save_debugged_code(file_path: str, debugged_code: str) -> None:
    """Overwrite *file_path* with the debugged YAML code."""
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(debugged_code)


# ---------------------------------------------------------------------------
# Text-block / remake helpers
# ---------------------------------------------------------------------------


def _read_textblock_file(filepath: str, textblock_path: str) -> str:
    """
    Given a ``.yaml.debug`` file path, locate and read the corresponding
    ``.md`` text-block file.
    """
    try:
        yaml_path = filepath.replace("_0.yaml.debug", "")
        relative_path = yaml_path.split("/LLMrule/")[-1]
        relative_path = os.path.dirname(relative_path)
        md_file_path = os.path.join(
            textblock_path, f"{relative_path}_textres.md"
        )
        print(f"md_file_path: {md_file_path}")
        if os.path.exists(md_file_path):
            with open(md_file_path, "r", encoding="utf-8") as f:
                return f.read()
        return f"Error: File not found at path {md_file_path}"
    except Exception as e:
        return f"Error occurred: {e}"


def remake_debug_rule(rules_folder: str, textblock_path: str) -> None:
    """Re-generate rules for ``.debug`` files by re-sending text blocks to the API."""
    prompt2 = (
        "Please refer to the semgrep_doc in the knowledge base for Semgrep "
        "syntax rules, and recheck the above for any syntax issues. If any "
        "issues are found, please correct them accordingly."
    )

    debug_files = find_debug_files(rules_folder)
    print(f"Total .debug files: {len(debug_files)}")
    print(debug_files)

    for file in debug_files:
        print(f"Processing file: {file}")
        textblock = _read_textblock_file(file, textblock_path)
        standards = common.parse_text(textblock)

        for standard in standards:
            name = standard["name"].replace("** ", "")
            filerulename = re.sub(
                r"_[\d_]+\.yaml\.debug$", "", os.path.basename(file)
            )
            if name != filerulename:
                print(
                    f"File {filerulename} does not match standard {name}, "
                    f"skipping"
                )
                continue

            prompt = f"name:{name}\ninfo:\n{standard['info']}"
            chat_id = str(os.time() if hasattr(os, 'time') else __import__('time').time())

            response = common.send_request(prompt, chat_id=chat_id)
            if not response:
                print("First request failed, retrying...")
                import time
                time.sleep(3)
                response = common.send_request(prompt, chat_id=chat_id)
                if not response:
                    print(
                        f"Second request failed, skipping file "
                        f"{textblock}:{name}"
                    )
                    continue

            followup_response = common.send_request(prompt2, chat_id=chat_id)
            yaml_blocks = common.extract_yaml_from_response(
                followup_response or ""
            )

            for yaml_block in yaml_blocks:
                yaml_content = yaml_block.strip()
                save_file_path = file.replace(".debug", "")
                print(f"save_file_path: {save_file_path}")
                common.save_yaml_file(yaml_content, save_file_path)
                print(f"Saved YAML file to: {save_file_path}")

            os.remove(file)
            print(f"Deleted debug file: {file}")

    print(
        "\n\n\n----------All debug files have been remade successfully----------\n"
    )


# ---------------------------------------------------------------------------
# Main loop
# ---------------------------------------------------------------------------


def check_and_fix_errors(
    rules_folder: str, output_file: str, max_attempts: int = 5
) -> None:
    """
    Loop: validate → fix → repeat until no errors or *max_attempts* exhausted.

    If errors persist after *max_attempts*, rules are renamed and a further
    shortened loop runs.
    """
    outer_attempts = max_attempts
    while True:
        attempt_count = 0
        while attempt_count < outer_attempts:
            run_semgrep_validate(rules_folder, output_file)
            errors = load_error_report(output_file)

            if not errors:
                print("Validation completed successfully. No errors found.")
                return

            print(
                f"Errors found in validation. Attempting to auto-debug "
                f"(Attempt {attempt_count + 1}/{outer_attempts})."
            )
            rule_id_path = extract_rule_ids_from_folder(rules_folder)
            _auto_debug_errors(output_file, rule_id_path)
            attempt_count += 1

        # Final validation
        run_semgrep_validate(rules_folder, output_file)
        errors = load_error_report(output_file)

        if errors:
            print(
                f"Errors persist after {outer_attempts} attempts. "
                f"Renaming files with errors and restarting count."
            )
            for error in errors:
                file_path, _ = get_rule_source_code(error, rule_id_path)
                if file_path:
                    print(f"error file: {file_path}")
            outer_attempts = 3
        else:
            print("Final validation completed. No errors found.")
            break


def fix_debug(
    rules_folder: str, output_file: str, textblock_path: str = "", round_num: int = 8
) -> None:
    """Public entry point: validate and fix all rules under *rules_folder*."""
    check_and_fix_errors(rules_folder, output_file, round_num)


if __name__ == "__main__":
    rules_folder = ""
    output_file = ""
    fix_debug(rules_folder, output_file)
