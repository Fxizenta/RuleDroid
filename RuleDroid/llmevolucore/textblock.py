"""
Multithreaded generation of code blocks related to security issues.

Reads Markdown files, sends each to an LLM API, validates the response format,
and saves the resulting text blocks.
"""

import os
import re
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

import requests

from llog import log_and_print


# Set API base URL and authentication info (configure before running)
api_base_url = "http://"
api_key = ""


def check_text_format(text: str) -> bool:
    """
    Validate the LLM response format.

    Checks:
    1. Whether ``Has`` is ``True`` or ``False``.
    2. If ``Has`` is ``True``, at least one ``safety standard`` block must exist.
    """
    has_match = re.search(r"Has\s?:\s?(True|False)", text, re.IGNORECASE)
    if not has_match:
        print("The first line does not match the required format.")
        return False

    if has_match.group(1) == "False":
        return True

    safety_standard_pattern = (
        r"(?<=\n---\n)\s*\#*\s*safety standard\s?\d+(\.|:|\n)"
    )
    print(re.search(safety_standard_pattern, text, re.IGNORECASE))
    if not re.search(safety_standard_pattern, text, re.IGNORECASE):
        print("At least one 'safety standard' block is required.")
        return False
    return True


def process_file(
    filepath: str,
    readpath: str,
    savepath_base: str,
    max_retries: int = 5,
    retry_delay: int = 2,
) -> None:
    """
    Process a single file: read, submit to API, validate, and save result.

    Retries on failure up to *max_retries* times.
    """
    if not filepath.endswith(".md"):
        return

    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    payload = {
        "stream": False,
        "detail": False,
        "messages": [{"role": "user", "content": content}],
    }
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }

    for attempt in range(max_retries):
        try:
            response = requests.post(
                api_base_url, json=payload, headers=headers, timeout=600
            )
            response.raise_for_status()
            response_data = response.json()
            response_text = response_data["choices"][0]["message"]["content"]

            if not check_text_format(response_text):
                log_and_print(
                    f"Invalid format returned for {filepath}, retrying..."
                )
                log_and_print(f"Retry attempt {attempt + 1}/{max_retries}")
                continue

            relative_path = os.path.relpath(filepath, start=readpath)
            new_filename = os.path.splitext(relative_path)[0] + "_textres.md"
            save_filepath = os.path.join(savepath_base, new_filename)

            os.makedirs(os.path.dirname(save_filepath), exist_ok=True)

            with open(save_filepath, "w", encoding="utf-8") as save_file:
                save_file.write(response_text)
            log_and_print(f"Saved result to: {save_filepath}")
            break

        except (requests.ConnectionError, requests.Timeout, requests.HTTPError) as e:
            print(
                f"Error processing {filepath}, "
                f"retrying {attempt + 1}/{max_retries}: {e}"
            )
            time.sleep(retry_delay)
        except Exception as e:
            print(f"Unexpected error while processing {filepath}: {e}")
            break


def process_directory(
    readpath: str, savepath: str, max_workers: int = 5
) -> None:
    """
    Recursively read all files in *readpath* and process them concurrently.

    Parameters
    ----------
    readpath : str
        Directory containing input ``.md`` files.
    savepath : str
        Directory where processed files will be saved.
    max_workers : int
        Maximum number of worker threads.
    """
    print(f"Starting to process directory: {readpath}")
    all_files: list[str] = []
    for root, _, files in os.walk(readpath):
        for file in files:
            all_files.append(os.path.join(root, file))

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(process_file, f, readpath, savepath)
            for f in all_files
        ]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error occurred while processing file: {exc}")


if __name__ == "__main__":
    readpath = ""
    savepath = ""
    process_directory(readpath, savepath)
