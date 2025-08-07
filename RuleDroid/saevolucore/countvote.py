from collections import Counter
from typing import List
from llog import log_and_print

def process_files(files: List[str], output_file: str, threshold: int):
    # Store the set of strings from each file
    file_contents = []

    for file in files:
        with open(file, "r") as f:
            content = set(line.strip() for line in f)
            file_contents.append(content)

    # Merge all strings and count occurrences
    all_strings = Counter()
    for content in file_contents:
        all_strings.update(content)

    # Filter strings that appear in at least the specified number of files
    qualified_strings = [string for string, count in all_strings.items() if count >= threshold]
    unqualified_strings = {string: count for string, count in all_strings.items() if count < threshold}

    # Sort and save the qualified strings to the output file
    with open(output_file, "w") as f:
        for string in sorted(qualified_strings):
            f.write(f"{string}\n")

    # Print the unqualified strings and their occurrence counts
    log_and_print("Unqualified strings and their occurrence counts:")
    log_and_print(f"num: {len(unqualified_strings)}")
    for string, count in unqualified_strings.items():
        log_and_print(f"{string}: {count} times")

# Example of calling the main function
if __name__ == "__main__":
    files = [f"/path/rag_low_{i}_files.txt" for i in range(1, 6)]
    output_file = ""
    threshold = 4
    process_files(files, output_file, threshold)
