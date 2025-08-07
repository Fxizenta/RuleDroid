import os
from llog import log_and_print

def count_md_files(folder_path, output_file):
    """
    Count the number of .md files in a specified folder and save the file names to the specified output file.

    Parameters:
    folder_path (str): The folder path to scan.
    output_file (str): The output file path where the .md file names will be saved.

    Returns:
    tuple: A tuple containing the number of .md files and the list of file paths.
    """
    md_files = []  # List to store all .md file paths

    # Recursively walk through the folder
    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith('.md'):  # Check if the file is a .md file
                md_files.append(os.path.join(root, file))  # Save the file path

    # Log the total number of .md files and their names
    log_and_print(f"Total number of .md files: {len(md_files)}")
    log_and_print("List of file names:")
    for file in md_files:
        log_and_print(file.replace(folder_path, ""))

    # Save the file names to the specified output file
    try:
        with open(output_file, "w") as f:
            for file in md_files:
                f.write(file.replace(folder_path, "") + "\n")
        log_and_print(f"File names have been saved to {output_file}")
    except IOError as e:
        log_and_print(f"Error writing to file: {e}")

    return len(md_files), md_files

# Example usage
if __name__ == "__main__":
    folder = ""
    output = ""

    if os.path.isdir(folder):
        count_md_files(folder, output)
    else:
        print("Please provide a valid folder path.")
