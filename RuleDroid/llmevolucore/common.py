"""
Shared utilities for the LLM evolution core.

This module consolidates commonly duplicated functions across the package:
API requests, text parsing, YAML extraction, and file I/O helpers.
"""

import os
import re
import time
import requests
from typing import Optional


# ---------------------------------------------------------------------------
# API configuration (set by the caller before invoking send_request)
# ---------------------------------------------------------------------------

api_base_url: str = ""
api_key: str = ""


# ---------------------------------------------------------------------------
# API request
# ---------------------------------------------------------------------------

def send_request(
    prompt: str,
    chat_id: str = "",
    retries: int = 3,
    timeout: int = 300,
    base_url: Optional[str] = None,
    key: Optional[str] = None,
) -> Optional[str]:
    """
    Send a request to the LLM API with a retry mechanism.

    Parameters
    ----------
    prompt : str
        The user message content to send.
    chat_id : str
        Optional chat/session identifier included in the payload.
    retries : int
        Maximum number of retry attempts on timeout.
    timeout : int
        Request timeout in seconds.
    base_url : str or None
        API base URL; falls back to module-level ``api_base_url``.
    key : str or None
        API key; falls back to module-level ``api_key``.

    Returns
    -------
    str or None
        The ``content`` field from the first choice, or *None* on failure.
    """
    url = base_url or api_base_url
    auth_key = key or api_key

    payload: dict = {
        "stream": False,
        "detail": False,
        "messages": [
            {"role": "user", "content": prompt}
        ],
    }
    if chat_id:
        payload["chatId"] = chat_id

    headers = {
        "Authorization": f"Bearer {auth_key}",
        "Content-Type": "application/json",
    }

    for attempt in range(retries):
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=timeout)
            response.raise_for_status()
            return response.json()["choices"][0]["message"]["content"]
        except requests.exceptions.Timeout:
            print(f"Request timeout, retrying {attempt + 1}/{retries}...")
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None

    print(f"Request failed after {retries} retries")
    return None


# ---------------------------------------------------------------------------
# Text / YAML helpers
# ---------------------------------------------------------------------------

def parse_text(content: str) -> list[dict[str, Optional[str]]]:
    """
    Parse markdown-ish text content into a list of *standards*.

    Each standard is a dict with keys ``name``, ``type``, and ``info``.
    Sections are delimited by ``---\\n``.
    """
    sections = re.split(r'---\n', content)
    results: list[dict[str, Optional[str]]] = []

    for section in sections:
        if not section.strip():
            continue

        name_match = re.search(
            r'\*{0,2}name\*{0,2}\s?:\s*(.+?)\n', section, re.IGNORECASE
        )
        name = name_match.group(1).strip() if name_match else None

        type_match = re.search(
            r'\*{0,2}type\*{0,2}\s?:\s*(.+?)\n', section, re.IGNORECASE
        )
        type_ = type_match.group(1).strip() if type_match else None

        info_match = re.search(
            r'\*{0,2}info\*{0,2}\s?:\s*(.+)', section, re.DOTALL | re.IGNORECASE
        )
        info = info_match.group(1).strip() if info_match else None

        if name and type_ and info:
            results.append({"name": name, "type": type_, "info": info})

    return results


def extract_yaml_from_response(response: str) -> list[str]:
    """Extract YAML fenced-code blocks from an LLM response string."""
    return re.findall(r'```yaml(.*?)```', response, re.DOTALL)


def save_yaml_file(content: str, savepath: str) -> None:
    """Save YAML content to the specified path, creating directories as needed."""
    savepath = savepath.replace("** ", "")
    os.makedirs(os.path.dirname(savepath), exist_ok=True)
    with open(savepath, "w", encoding="utf-8") as f:
        f.write(content)


# ---------------------------------------------------------------------------
# File-system helpers
# ---------------------------------------------------------------------------

def read_files_recursive(dir_path: str) -> list[str]:
    """Return a list of absolute paths for every file under *dir_path*."""
    files: list[str] = []
    for root, _, filenames in os.walk(dir_path):
        for filename in filenames:
            files.append(os.path.join(root, filename))
    return files
