"""
Rule severity classification via LLM API.

Reads Semgrep YAML rules and asks an LLM to classify each as ERROR,
WARNING, or INFO severity.
"""

import os
import re
import logging
from concurrent.futures import ThreadPoolExecutor

import llmevolucore.common as common

logging.basicConfig(
    filename="makerule.log",
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
)


def log_and_print(message: str) -> None:
    """Print a message to stdout and also write it to the log."""
    print(message)
    logging.info(message)


def _find_and_replace_severity(file_path: str, new_severity: str) -> None:
    """Replace the ``severity:`` field value in a YAML file."""
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = re.sub(
        r"severity:\s*(ERROR|WARNING|INFO)",
        f"severity: {new_severity}",
        content,
    )
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)


def _process_file(file_path: str, chat_id: int) -> None:
    """Classify the severity of rules in a single YAML file."""
    print(f"Processing file: {file_path}")
    with open(file_path, "r", encoding="utf-8") as f:
        file_content = f.read()

    result = common.send_request(file_content, chat_id=str(chat_id), timeout=360)
    if result:
        if "ERROR" in result:
            new_severity = "ERROR"
        elif "WARNING" in result:
            new_severity = "WARNING"
        elif "INFO" in result:
            new_severity = "INFO"
        else:
            new_severity = None

        if new_severity:
            _find_and_replace_severity(file_path, new_severity)
            log_and_print(f"Updated severity of {file_path} to {new_severity}")
        else:
            log_and_print(f"Unknown response: {result}")
    else:
        log_and_print(f"Failed to update severity for {file_path}")


def process_files_in_directory(directory: str, max_workers: int = 5) -> None:
    """Walk *directory* for YAML files and classify severity of each."""
    chat_id = 1000
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = []
        for root, _, files in os.walk(directory):
            for file in files:
                if file.endswith((".yaml", ".yml")):
                    file_path = os.path.join(root, file)
                    futures.append(
                        executor.submit(_process_file, file_path, chat_id)
                    )
                    chat_id += 1
        for future in futures:
            future.result()


if __name__ == "__main__":
    directory_path = ""
    process_files_in_directory(directory_path)
