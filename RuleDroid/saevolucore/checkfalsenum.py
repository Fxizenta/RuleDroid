"""
Count ``.md`` files in a directory and write their relative paths to an output
file.  Used to tally the number of "False" votes after LLM filtering.
"""

import os

from llog import log_and_print


def count_md_files(folder_path: str, output_file: str) -> tuple[int, list[str]]:
    """
    Recursively scan *folder_path* for ``.md`` files.

    Returns a tuple of ``(count, file_paths)`` and writes the relative paths
    (with respect to *folder_path*) to *output_file*.
    """
    md_files: list[str] = []

    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith(".md"):
                md_files.append(os.path.join(root, file))

    log_and_print(f"Total number of .md files: {len(md_files)}")
    log_and_print("List of file names:")
    for file in md_files:
        log_and_print(file.replace(folder_path, ""))

    try:
        with open(output_file, "w", encoding="utf-8") as f:
            for file in md_files:
                f.write(file.replace(folder_path, "") + "\n")
        log_and_print(f"File names have been saved to {output_file}")
    except IOError as e:
        log_and_print(f"Error writing to file: {e}")

    return len(md_files), md_files


if __name__ == "__main__":
    folder = ""
    output = ""

    if os.path.isdir(folder):
        count_md_files(folder, output)
    else:
        print("Please provide a valid folder path.")
