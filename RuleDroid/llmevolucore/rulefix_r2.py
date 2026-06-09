"""
Semgrep rule syntax validation and auto-fixing (revised version, r2).

Improves on the original ``rulefix.py`` by adding rename-on-failure for
.debug files, better error recovery, and text-block-based rule re-generation.
"""

import json
import os
import re
import time

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
# API helpers
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
        "chatId": "abcd",
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


def _save_debugged_code(file_path: str, debugged_code: str) -> None:
    """Overwrite *file_path* with the debugged YAML code."""
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(debugged_code)


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


# ---------------------------------------------------------------------------
# .debug file handling
# ---------------------------------------------------------------------------


def _rename_debug_files(directory: str) -> None:
    """Strip the ``.debug`` suffix from all debug files in *directory*."""
    for file in find_debug_files(directory):
        new_file = file.replace(".debug", "")
        os.rename(file, new_file)
        log_and_print(f"Renamed file: {file} -> {new_file}")


def _read_textblock_file(filepath: str, textblock_path: str) -> str:
    """
    Given a ``.yaml.debug`` file path, locate and read the corresponding
    ``.md`` text-block file.
    """
    try:
        yaml_path = filepath.replace(".debug", "")
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


# ---------------------------------------------------------------------------
# Main logic
# ---------------------------------------------------------------------------


def check_and_fix_errors(
    rules_folder: str,
    output_file: str,
    max_attempts: int = 5,
) -> None:
    """
    Loop: validate → fix → repeat.

    On entry all ``.debug`` files are renamed back to ``.yaml`` so validation
    can pick them up.  If errors persist after *max_attempts* rounds the
    offending files are renamed to ``.debug`` and a further shortened loop runs.
    """
    _rename_debug_files(rules_folder)

    outer_attempts = max_attempts
    while True:
        attempt_count = 0
        while attempt_count < outer_attempts:
            run_semgrep_validate(rules_folder, output_file)
            errors = load_error_report(output_file)

            if not errors:
                log_and_print("Validation completed successfully. No errors found.")
                return

            log_and_print(
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
            log_and_print(
                f"Errors persist after {outer_attempts} attempts. "
                f"Renaming files with errors and restarting count."
            )
            for error in errors:
                file_path, _ = get_rule_source_code(error, rule_id_path)
                if file_path:
                    new_file_path = f"{file_path}.debug"
                    if os.path.exists(new_file_path):
                        log_and_print(
                            f"Debug file already exists: {new_file_path}"
                        )
                        continue
                    os.rename(file_path, new_file_path)
                    log_and_print(f"Renamed file: {file_path} -> {new_file_path}")
            outer_attempts = 3
        else:
            log_and_print("Final validation completed. No errors found.")
            break


def remake_debug_rule(rules_folder: str, textblock_path: str) -> None:
    """Re-generate rules for ``.debug`` files using the original text blocks."""
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
            chat_id = str(time.time())

            response = common.send_request(prompt, chat_id=chat_id)
            if not response:
                log_and_print("First request failed, retrying...")
                time.sleep(3)
                response = common.send_request(prompt, chat_id=chat_id)
                if not response:
                    log_and_print(
                        f"Second request failed, skipping this file "
                        f"{textblock}:{name}"
                    )
                    continue

            followup_response = common.send_request(prompt2, chat_id=chat_id)
            if not followup_response:
                log_and_print("Second request failed, skipping this file")
                continue

            print("Ask API Success\n")
            yaml_blocks = common.extract_yaml_from_response(followup_response)

            for yaml_block in yaml_blocks:
                yaml_content = yaml_block.strip()
                save_file_path = file.replace(".debug", "")
                print(f"save_file_path: {save_file_path}")
                common.save_yaml_file(yaml_content, save_file_path)
                log_and_print(f"Saved YAML file to: {save_file_path}")

            os.remove(file)
            print(f"Removed debug file: {file}")

    log_and_print(
        "\n\n\n----------All debug files have been remade successfully----------\n"
    )


def fix_debug(
    rules_folder: str,
    output_file: str,
    textblock_path: str = "",
    round_num: int = 5,
) -> None:
    """Public entry point: validate and fix all rules under *rules_folder*."""
    _ = textblock_path  # reserved for future use
    check_and_fix_errors(rules_folder, output_file, round_num)


if __name__ == "__main__":
    rules_folder = ""
    output_file = ""
    textblock_path = ""
    fix_debug(rules_folder, output_file, textblock_path, round_num=5)
