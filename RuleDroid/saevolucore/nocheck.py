"""
Utilities for marking text blocks as "no-check" by renaming their files.

Used after voting to exclude files that were flagged by the LLM as unsuitable
for Semgrep rule generation.
"""

import os
import re

from llog import log_and_print


def remove_leading_spaces(filename: str) -> str:
    """Remove leading whitespace from *filename*."""
    return re.sub(r"^\s+", "", filename)


def read_txt(file_path: str) -> list[str]:
    """Read lines from a text file, stripping whitespace and skipping blanks."""
    with open(file_path, "r", encoding="utf-8") as f:
        return [line.strip() for line in f if line.strip()]


def extract_filenames(paths: list[str]) -> list[str]:
    """Extract bare filenames (without extension) from a list of paths."""
    filenames: list[str] = []
    for path in paths:
        filename = path.split("/")[-1]
        filename = filename.rsplit(".", 1)[0]
        filenames.append(filename.lstrip())
    return filenames


def rename_files(txt_lines: list[str], base_dir: str) -> None:
    """
    For each line in *txt_lines*, locate the corresponding file under
    *base_dir* and rename it by appending ``.nocheck``.
    """
    num = 0
    for line in txt_lines:
        directory, file_name = os.path.split(line)
        file_base_name, _ = os.path.splitext(file_name)
        file_base_name = remove_leading_spaces(file_base_name)
        target_dir = os.path.join(base_dir, directory.strip("/"))

        if not os.path.exists(target_dir):
            log_and_print(f"Directory not found: {target_dir}, skipping...")
            continue

        flag = True
        for file in os.listdir(target_dir):
            if file.startswith(file_base_name):
                original_path = os.path.join(target_dir, file)
                new_path = original_path + ".nocheck"
                os.rename(original_path, new_path)
                log_and_print(f"Renamed: {original_path} -> {new_path}")
                flag = False
                num += 1
        if flag:
            print(f"File not changed: {line}")

    log_and_print(f"A total of {num} files were renamed.")


def remove_nocheck_suffix(base_directory: str) -> None:
    """Recursively strip ``.nocheck`` suffix from all files under *base_directory*."""
    for root, _, files in os.walk(base_directory):
        for file in files:
            if file.endswith(".nocheck"):
                old_path = os.path.join(root, file)
                new_path = old_path[:-8]  # Remove ".nocheck" (8 chars)
                os.rename(old_path, new_path)
                log_and_print(f"Removed suffix: {old_path} -> {new_path}")


def make_textblock_nocheck(txt_file: str, base_directory: str) -> None:
    """Rename files listed in *txt_file* to add ``.nocheck`` suffix."""
    remove_nocheck_suffix(base_directory)
    txt_lines = read_txt(txt_file)
    rename_files(txt_lines, base_directory)


if __name__ == "__main__":
    txt_file = ""
    base_directory = ""

    read_txt(txt_file)
    print(extract_filenames(read_txt(txt_file)))
