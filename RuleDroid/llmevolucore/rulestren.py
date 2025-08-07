import os
import subprocess
import yaml
import time
import requests
import re
from concurrent.futures import ThreadPoolExecutor, as_completed

import logging


logging.basicConfig(
    filename="LLMmakerule2.log",  # Log file name
    level=logging.INFO,  # Log level
    format="%(asctime)s - %(levelname)s - %(message)s"  # Log format
)

def log_and_print(message):
    """Function to print and log a message"""
    print(message)
    logging.info(message)

# API request function
def send_request(prompt, retries=3):
    """Send request to custom API and return response, with retry mechanism"""
    api_key = ""
    api_base_url = ""

    payload = {
        "stream": False,
        "detail": False,
        "messages": [
            {"role": "user", "content": prompt}
        ]
    }
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    attempt = 0
    while attempt < retries:
        try:
            response = requests.post(api_base_url, json=payload, headers=headers, timeout=300)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except requests.exceptions.Timeout:
            print(f"Request timeout, retrying {attempt + 1}/{retries}...")
            attempt += 1
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed, exceeded maximum retry attempts ({retries})")
    return None

# Parse API response
def parse_deficiency(api_response):
    """Parse if there are deficiencies and the corresponding rules from API response."""
    is_deficient = False
    modified_rules = None

    # Match the boolean value after "#### Is there a deficiency:"
    deficiency_match = re.search(r"Is there a deficiency\*?\*?\*?\*?:\*?\*?\*?\*?\s(True|False)", api_response, re.IGNORECASE)
    if deficiency_match:
        is_deficient = deficiency_match.group(1).strip().lower() == "true"

    # Match the ```yaml``` block content after "#### Modified rules:"
    modified_rules_match = re.search(r"Modified rules\*?\*?\*?\*?:\*?\*?\*?\*?\s*```yaml\n(.*?)\n```", api_response, re.DOTALL | re.IGNORECASE | re.MULTILINE)
    if modified_rules_match:
        modified_rules = modified_rules_match.group(1).strip()
    # log_and_print(f"is_deficient: {is_deficient}\nmodified_rules_match: \n{modified_rules}\napi_response: \n{api_response}")
    return {"is_deficient": is_deficient, "modified_rules": modified_rules}

# Save rule to the specified path
def save_to_file(rule, save_path):
    """Save the rule to the specified path"""
    os.makedirs(os.path.dirname(save_path), exist_ok=True)

    with open(save_path, "w", encoding="utf-8") as f:
        if isinstance(rule, dict):
            rule = yaml.dump(rule, default_flow_style=False)
            log_and_print(f"YAMLDUIMP")
        f.write(rule)
    
    log_and_print(f"Rule saved to: {save_path}")

# Build save path
def get_relative_path(rule_id, file_path, input_folder):
    """Build relative save path for the rule"""
    file_name = os.path.basename(file_path).split(".")[0]
    relative_path = file_path[len(input_folder):].strip(os.sep)
    relative_folder = os.path.dirname(relative_path)
    return os.path.join(relative_folder, file_name, rule_id + ".yaml")

def run_semgrep_validate(rules_folder, output_file):
    # Check if the file exists, if so delete it
    if os.path.exists(output_file):
        os.remove(output_file)
    command = [
        "semgrep",
        "-c", rules_folder,
        "--validate",
        "--json",
        "--output", output_file
    ]
    try:
        subprocess.run(command, check=True, capture_output=True, text=True)
        log_and_print("Semgrep validation completed successfully.")
    except subprocess.CalledProcessError as e:
        log_and_print(f"Error occurred while running semgrep:{e.stderr}")


# Confirm if the generated rule is a syntactically correct YAML file
def debugrule(save_rule, thread_id):
    path = f"/data/fxizenta/LLMmakerule2/debug/debug_{thread_id}.yaml"
    debug_json_path = f"/data/fxizenta/LLMmakerule2/debug/debug_{thread_id}.json"
    save_to_file(save_rule, path)
    # Check if the json file exists, if so delete it
    if os.path.exists(debug_json_path):
        os.remove(debug_json_path)
    run_semgrep_validate(path, debug_json_path)
    if os.path.exists(debug_json_path):
        with open(debug_json_path, "r", encoding="utf-8") as f:
            debug_json = f.read()
        if debug_json:
            log_and_print(f"debug_json: {debug_json}")
            return False
    return True

# Single file processing function
def process_file(file_path, input_folder, output_folder, thread_id):
    """Process a single file's rules and save them"""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rules = yaml.safe_load(f)
            if "rules" in rules:
                log_and_print(f"Processing file: {file_path}\nrules: {rules['rules']}")
                for rule in rules["rules"]:
                    rule_prompt = yaml.dump(rule, default_flow_style=False)
                     # Build save path
                    rule_id = rule["id"]
                    relative_path = get_relative_path(rule_id, file_path, input_folder)
                    save_path = os.path.join(output_folder, relative_path)
                    if os.path.exists(save_path):
                        log_and_print(f"Rule already exists: {save_path}")
                        # Skip if the file is not empty
                        if os.path.getsize(save_path) > 0:
                            log_and_print(f"Rule already exists and is not empty: {save_path}")
                            continue
                        else:
                            log_and_print(f"Rule already exists but is empty: {save_path}; re-requesting")
                    log_and_print(f"Sending rule to API: {rule['id']}")
                    api_response = send_request(rule_prompt)

                    if api_response:
                        deficiency = parse_deficiency(api_response)
                        while deficiency["is_deficient"] and deficiency["modified_rules"] is None:
                            log_and_print(f"Rule {rule['id']} has deficiencies but no modified rule found, re-requesting")
                            api_response = send_request(rule_prompt)
                            deficiency = parse_deficiency(api_response)
                            
                        # Determine the rule to save
                        if not deficiency["is_deficient"]:
                            save_rule = {"rules": [rule]}
                        else:
                            save_rule = deficiency["modified_rules"]
                            # Check if the generated rule is a syntactically correct YAML file
                            if not debugrule(save_rule, thread_id):
                                log_and_print(f"Rule {rule['id']} generated rule is not a syntactically correct YAML file")
                                save_rule = {"rules": [rule]}
                            
                        # Save the rule
                        save_to_file(save_rule, save_path)
                    else:
                        log_and_print(f"Rule {rule['id']} did not get a valid response")
            else:
                log_and_print(f"File {file_path} does not contain rules")
    except yaml.YAMLError as e:
        log_and_print(f"Error parsing YAML file: {e}")

# Multithreaded processing function
def parse_and_save_rules_with_api_multithreaded(input_folder, output_folder, max_threads=10):
    """Multithreaded processing of rule files"""
    file_paths = []
    for root, _, files in os.walk(input_folder):
        for file in files:
            if file.endswith(".yaml") or file.endswith(".yml") or file.endswith(".noscan"):
                file_paths.append(os.path.join(root, file))

    with ThreadPoolExecutor(max_threads) as executor:
        future_to_file = {executor.submit(process_file, file_path, input_folder, output_folder, thread_id): file_path for thread_id, file_path in enumerate(file_paths)}
        for future in as_completed(future_to_file):
            file_path = future_to_file[future]
            try:
                future.result()
            except Exception as e:
                log_and_print(f"Error processing file {file_path}: {e}")

if __name__ == "__main__":
    input_rules_folder = ""  # Input rules folder path
    output_rules_folder = ""  # Output parsed results folder path
    parse_and_save_rules_with_api_multithreaded(input_rules_folder, output_rules_folder, max_threads=10)
