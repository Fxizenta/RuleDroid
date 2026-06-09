"""
This script processes text blocks to filter out those that are deemed unsuitable
by the LLM for Semgrep detection.  It identifies blocks that produce a "False"
result.

The script may need to be run multiple times with a voting mechanism.
"""

import os
import re
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

import llmevolucore.common as common
from llog import log_and_print


def save_markdown_file(content: str, savepath: str) -> None:
    """Save markdown content to the specified path, creating directories as needed."""
    os.makedirs(os.path.dirname(savepath), exist_ok=True)
    with open(savepath, "w", encoding="utf-8") as f:
        f.write(content)


def process_file(
    filepath: str,
    readpath: str,
    savepath_base: str,
    savepath_true: str,
) -> None:
    """Parse a file, send each standard to the API, and save the result."""
    log_and_print(f"Processing file: {filepath}")

    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    filename = os.path.basename(filepath)
    standards = common.parse_text(content)
    log_and_print(f"File {filepath} contains {len(standards)} standards")

    for standard in standards:
        if "Safety" not in (standard.get("type") or ""):
            log_and_print(
                f"{filepath} skipping non-Safety Requirements standard: "
                f"{standard['name']}:{standard.get('type')}"
            )
            continue

        name = standard["name"].replace("* ", "").replace("*", "")
        prompt = f"name:{standard['name']}\ninfo:\n{standard['info']}"

        relative_path = Path(filepath).relative_to(readpath)
        save_false_path = os.path.join(
            savepath_base, relative_path.parent, filename[:-11], f"{name}.md"
        )
        save_true_path = os.path.join(
            savepath_true, relative_path.parent, filename[:-11], f"{name}.md"
        )

        if os.path.exists(save_false_path) or os.path.exists(save_true_path):
            log_and_print(
                f"{filepath}/{name} already exists, skipping this standard: {name}"
            )
            continue

        response = common.send_request(prompt, timeout=560)
        if not response:
            log_and_print(f"{filepath}/{name} first request failed, retrying...")
            time.sleep(3)
            response = common.send_request(prompt, timeout=560)
            if not response:
                log_and_print(
                    f"{filepath}/{name} second request failed, "
                    f"skipping this standard: {name}"
                )
                continue

        false_pattern = re.compile(r"false\n", re.IGNORECASE | re.MULTILINE)
        if false_pattern.search(response):
            log_and_print(f"False: {filepath} {name}")
            output = "**BlockText**\n\n" + prompt + "\n\n**Response**\n\n" + response
            save_markdown_file(output, save_false_path)
            log_and_print(f"Saved to: {save_false_path}")
        else:
            log_and_print(f"True: {filepath} {name}")
            output = "**BlockText**\n\n" + prompt + "\n\n**Response**\n\n" + response
            save_markdown_file(output, save_true_path)
            log_and_print(f"Saved to: {save_true_path}")


def process_directory(
    readpath: str,
    savepath: str,
    savepath_true: str,
    max_workers: int = 30,
) -> None:
    """Recursively process all files in a directory using multithreading."""
    all_files = common.read_files_recursive(readpath)

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(process_file, f, readpath, savepath, savepath_true)
            for f in all_files
        ]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error occurred while processing a file: {exc}")


if __name__ == "__main__":
    readpath = ""
    savepath = ""
    savepath_true = ""
    process_directory(readpath, savepath, savepath_true)
