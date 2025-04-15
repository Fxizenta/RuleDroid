import os
import yaml

def process_yaml_files(directory):
    """
    Traverse all YAML files in the given directory,
    modify duplicate rule IDs, and save the files back.

    :param directory: The path to the directory to process
    """
    rule_id_counts = {}  # Tracks rule IDs and their counts

    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".yaml") or file.endswith(".yml"):
                file_path = os.path.join(root, file)
                process_file(file_path, rule_id_counts)

def process_file(file_path, rule_id_counts):
    """
    Process a single YAML file, modify duplicate rule IDs, and save it.

    :param file_path: Path to the YAML file
    :param rule_id_counts: Global dictionary to track rule ID counts
    """
    with open(file_path, "r", encoding="utf-8") as f:
        try:
            data = yaml.safe_load(f)
        except yaml.YAMLError as e:
            print(f"Error parsing YAML file {file_path}: {e}")
            return

    if not data or "rules" not in data:
        return

    for rule in data["rules"]:
        if "id" in rule:
            original_id = rule["id"]
            if original_id in rule_id_counts:
                rule_id_counts[original_id] += 1
                rule["id"] = f"{original_id}_{rule_id_counts[original_id]}"
                print(f"Modified rule ID: {original_id} -> {rule['id']}")
            else:
                rule_id_counts[original_id] = 0

    # Write the modified content back to the file
    with open(file_path, "w", encoding="utf-8") as f:
        yaml.dump(data, f, allow_unicode=True)

def rename_dup(directory_path):
    """
    Entry point to check and process a given directory.
    """
    if os.path.exists(directory_path) and os.path.isdir(directory_path):
        process_yaml_files(directory_path)
        print("Processing completed!")
    else:
        print("Invalid directory path. Please check and try again.")

if __name__ == "__main__":
    # Set your target directory path here
    directory_path = ""
    rename_dup(directory_path)
