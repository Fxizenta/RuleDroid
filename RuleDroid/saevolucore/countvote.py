"""
Vote aggregation: reads multiple voting-result files, counts how many times
each item appears, and writes items that meet or exceed a threshold.
"""

from collections import Counter
from typing import List

from llog import log_and_print


def process_files(files: List[str], output_file: str, threshold: int) -> None:
    """
    Count co-occurrences across *files* and write entries that appear in at
    least *threshold* files to *output_file*.

    Parameters
    ----------
    files : list[str]
        Paths to input text files (one entry per line).
    output_file : str
        Path where qualified entries are written, one per line.
    threshold : int
        Minimum number of files an entry must appear in to qualify.
    """
    file_contents: list[set[str]] = []

    for file in files:
        with open(file, "r", encoding="utf-8") as f:
            content = {line.strip() for line in f}
            file_contents.append(content)

    all_strings: Counter[str] = Counter()
    for content in file_contents:
        all_strings.update(content)

    qualified_strings = [
        s for s, count in all_strings.items() if count >= threshold
    ]
    unqualified_strings = {
        s: count for s, count in all_strings.items() if count < threshold
    }

    with open(output_file, "w", encoding="utf-8") as f:
        for string in sorted(qualified_strings):
            f.write(f"{string}\n")

    log_and_print("Unqualified strings and their occurrence counts:")
    log_and_print(f"num: {len(unqualified_strings)}")
    for string, count in unqualified_strings.items():
        log_and_print(f"{string}: {count} times")


if __name__ == "__main__":
    files = [f"/path/rag_low_{i}_files.txt" for i in range(1, 6)]
    output_file = ""
    threshold = 4
    process_files(files, output_file, threshold)
