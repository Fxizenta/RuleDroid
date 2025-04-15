import os
import re
from llog import log_and_print

def remove_leading_spaces(filename):
    """
    Remove leading spaces from the filename.

    Parameters:
        filename (str): The filename string.

    Returns:
        str: The processed filename.
    """
    # Use regex to match and remove leading spaces in the filename
    return re.sub(r'^\s+', '', filename)

# Read the content of a txt file
def read_txt(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        return [line.strip() for line in f if line.strip()]

# Function to extract filenames from paths
def extract_filenames(paths):
    filenames = []
    for path in paths:
        # Extract the last part of the path
        filename = path.split("/")[-1]
        # Remove the extension
        filename = filename.rsplit(".", 1)[0]
        # Remove leading spaces
        filename = filename.lstrip()
        filenames.append(filename)
    return filenames

# Rename files based on txt lines
def rename_files(txt_lines, base_dir):
    num = 0
    for line in txt_lines:
        # Get the directory and filename
        directory, file_name = os.path.split(line)
        file_base_name, _ = os.path.splitext(file_name)
        file_base_name = remove_leading_spaces(file_base_name)
        # Generate the absolute path
        target_dir = os.path.join(base_dir, directory.strip("/"))

        # Skip if the directory does not exist
        if not os.path.exists(target_dir):
            log_and_print(f"Directory not found: {target_dir}, skipping...")
            continue
        flag = True
        # Traverse the target directory to find matching files
        for file in os.listdir(target_dir):
            if file.startswith(file_base_name):
                original_path = os.path.join(target_dir, file)
                new_path = original_path + ".nocheck"
                # Rename the file
                os.rename(original_path, new_path)
                log_and_print(f"Renamed: {original_path} -> {new_path}")
                flag = False
                num += 1
        if flag:
            print(f"File not changed: {line}")
    log_and_print(f"A total of {num} files were renamed.")

def remove_nocheck_suffix():
    """
    Recursively remove the `.nocheck` suffix from all files in the directory
    """
    for root, _, files in os.walk(base_directory):
        for file in files:
            if file.endswith(".nocheck"):
                old_path = os.path.join(root, file)
                new_path = old_path[:-8]  # Remove ".nocheck"
                os.rename(old_path, new_path)
                log_and_print(f"Removed suffix: {old_path} -> {new_path}")

def make_textblock_nocheck(txt_file, base_directory):
    remove_nocheck_suffix()
    txt_lines = read_txt(txt_file)
    rename_files(txt_lines, base_directory)

if __name__ == "__main__":
    # Path configuration
    txt_file = ""  # Replace with your txt file path
    base_directory = ""  # Replace with your target directory
    # make_textblock_nocheck(txt_file, base_directory)
    read_txt(txt_file)
    # Extract filenames
    print(extract_filenames(read_txt(txt_file)))
