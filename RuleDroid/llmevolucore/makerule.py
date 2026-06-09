"""
This script processes blocks of text (standards) and generates corresponding
detection rules (YAML) by sending the content to an LLM API.  It includes
validation and optimization of the rules.
"""

import os
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

import llmevolucore.common as common


# Second prompt used for rule optimization and syntax checking
PROMPT_OPTIMIZE = (
    "Continue to optimize the above YAML detection rules to improve accuracy, "
    "reduce false positives, and achieve more comprehensive detection. However, "
    "please note that you should not add new types of detection rules, but simply "
    "improve the above detections, and at the same time ask you to perform a syntax "
    "check on the existing rules, which can be referred to the semgrep_doc knowledge "
    "base for specific grammar specifications, to ensure that the rules meet the "
    "syntax requirements and can run correctly."
)


def _extract_filename(name: str) -> str:
    """Clean and format a standard name for use as a file-name fragment."""
    name = name.replace("*", "")
    name = name.rsplit(".", 1)[0]
    return name.lstrip()


def process_file(
    filepath: str,
    readpath: str,
    savepath_base: str,
    false_name_list: list[str],
) -> None:
    """Parse a file, send each standard to the API, extract YAML, and save."""
    print(f"Processing file: {filepath}")

    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    filename = os.path.basename(filepath)
    standards = common.parse_text(content)
    print(f"{filepath} contains {len(standards)} blocks")

    for i, standard in enumerate(standards):
        name = _extract_filename(standard["name"])
        type_ = standard["type"]
        info = standard["info"]

        if "Safety" not in (type_ or ""):
            print(f"Skipping non-Safety Requirement block: {name}:{type_}")
            continue

        if name in false_name_list:
            print(
                f"Skipping previously flagged block "
                f"(not suitable for rule generation): {name}"
            )
            continue

        prompt = f"name:{name}\ninfo:\n{info}"
        chat_id = str(time.time()) + f"_{i}"

        response = common.send_request(prompt, chat_id=chat_id)
        if not response:
            print("First request failed, retrying...")
            time.sleep(3)
            response = common.send_request(prompt, chat_id=chat_id)
            if not response:
                print(f"Second request failed, skipping block: {name}")
                continue

        followup_response = common.send_request(PROMPT_OPTIMIZE, chat_id=chat_id)
        if not followup_response:
            print(f"Follow-up request failed, skipping block: {name}")
            continue

        yaml_blocks = common.extract_yaml_from_response(followup_response)
        for j, yaml_block in enumerate(yaml_blocks):
            yaml_content = yaml_block.strip()
            relative_path = Path(filepath).relative_to(readpath)
            save_file_path = os.path.join(
                savepath_base,
                relative_path.parent,
                filename[:-11],
                f"{name}_{i}_{j}.yaml",
            )
            common.save_yaml_file(yaml_content, save_file_path)
            print(f"Saved YAML to: {save_file_path}")


def process_directory(
    readpath: str,
    savepath: str,
    false_name_list: list[str],
    max_workers: int = 20,
) -> None:
    """Recursively process all files in a directory using multithreading."""
    all_files = common.read_files_recursive(readpath)

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(process_file, f, readpath, savepath, false_name_list)
            for f in all_files
        ]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error while processing file: {exc}")


if __name__ == "__main__":
    readpath = ""
    savepath = ""

    file_list = common.read_files_recursive(readpath)
    print(file_list)
    print(f"Total files found: {len(file_list)}")
