import json
import os
import time
import requests
import re
import subprocess
from pathlib import Path
import yaml
from llog import log_and_print

# API URL and headers setup
url = ""
makerule_apikey = " "
headers = {
    "Authorization": f"Bearer {makerule_apikey}",
    "Content-Type": "application/json",
    "Connection": "close"
}
api_key=""

def save_yaml_file(content, savepath):
    """Remove '**' from the path"""
    savepath = savepath.replace("** ", "")
    """Save YAML content to the specified path"""
    os.makedirs(os.path.dirname(savepath), exist_ok=True)
    with open(savepath, 'w', encoding='utf-8') as f:
        f.write(content)

def load_error_report(file_path):
    # First check if the file exists, if not return an empty list
    if not os.path.exists(file_path):
        return []
    """Reads JSON file and returns 'errors' field."""
    with open(file_path, "r") as file:
        data = json.load(file)
    return data.get("errors", [])


def get_rule_source_code(error,rule_id_path):
    file_path = error.get("path")

    if not file_path and "spans" in error and error["spans"]:
        file_path = error["spans"][0]["file"]

    if not file_path and "message" in error:
        #todo modif to match the path all can use
        match = re.search(r"/data/fxizenta/.*.yaml", error["message"])
        if match:
            file_path = match.group(0)
    if not file_path and "rule_id" in error:
        rule_id=error["rule_id"]
        file_path=rule_id_path.get(rule_id)
        print(f"find file_path by ruleid:{file_path}")

    if not file_path:
        print(f"Warning: No valid file path found for error with message: {error.get('message', 'No message provided')}")
        return None, None

    if os.path.exists(file_path):
        with open(file_path, "r") as file:
            source_code = file.read()
        return file_path, source_code
    elif os.path.exists(file_path.replace(".yaml",".yaml.debug")):
        with open(file_path.replace(".yaml",".yaml.debug"), "r") as file:
            source_code = file.read()
        return file_path, source_code
    else:
        raise FileNotFoundError(f"File not found: {file_path}")

def debug_code_with_local_api(source_code, error):
    error_message = json.dumps(error, indent=2)
    prompt = f"""
    source code:
    {source_code}
    
    error message:
    {error_message}
    
    Please fix the code based on the above error information.
    """
    data = {
        "chatId": "abcd",
        "stream": False,
        "detail": False,
        "messages": [{"role": "user", "content": prompt}]
    }
    response = requests.post(url, headers=headers, data=json.dumps(data), timeout=300)

    if response.status_code == 200:
        response_json = response.json()
        print("Ask API Success\n")
        return response_json["choices"][0]["message"]["content"]
    else:
        print(f"Error: {response.status_code}", response.text)
        return None

def save_debugged_code(file_path, debugged_code):
    with open(file_path, "w") as file:
        file.write(debugged_code)

def extract_yaml_from_response(response):
    """Extract YAML code blocks from the response"""
    code_blocks = re.findall(r'```yaml(.*?)```', response, re.DOTALL)
    return code_blocks


def auto_debug_errors(json_file,rule_id_path):
    errors = load_error_report(json_file)
    for error in errors:
        try:
            file_path, source_code = get_rule_source_code(error,rule_id_path)
            if not file_path:
                continue

            debugged_code = debug_code_with_local_api(source_code, error)
            yaml_code=extract_yaml_from_response(debugged_code)
            if debugged_code:
                save_debugged_code(file_path, yaml_code[0])
                log_and_print(f"Successfully debugged and saved code for: {file_path}")

        except Exception as e:
            print(f"Failed to debug error in file {error.get('path', 'unknown')}: {e}")

def run_semgrep_validate(rules_folder, output_file):
    # First check if the file exists, if it does, delete it
    if os.path.exists(output_file):
        os.remove(output_file)
    command = [
        "sudo", "semgrep",
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

def extract_rule_ids_from_folder(folder_path):
    """
    Recursively traverses a folder to find all YAML files, parses them using regex to extract Semgrep rule IDs,
    and returns a dictionary where keys are rule IDs and values are the file paths.

    :param folder_path: Path to the folder to traverse.
    :return: Dictionary of rule_id: file_path
    """
    rules_dict = {}

    # Regex pattern to match rule IDs
    rule_id_pattern = re.compile(r"-\s+id:\s+(\S+)")

    # Traverse the folder recursively
    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith(".yaml"):
                file_path = os.path.join(root, file)
                try:
                    # Open the file and search for rule IDs using regex
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                        matches = rule_id_pattern.findall(content)

                        # Add each found rule ID to the dictionary
                        for rule_id in matches:
                            rules_dict[rule_id] = file_path
                except Exception as e:
                    log_and_print(f"Error processing file {file_path}: {e}")

    return rules_dict

def rename_debug_files(directory):
    debug_files = find_debug_files(directory)
    for file in debug_files:
        new_file = file.replace(".debug", "")
        os.rename(file, new_file)
        log_and_print(f"Renamed file: {file} -> {new_file}")


def check_and_fix_errors(rules_folder, output_file, max_attempts=5):
    rename_debug_files(rules_folder)
    """Loop validation and fix process until no errors are found or files are renamed after max_attempts."""
    max_attempts = max_attempts
    while True:
        attempt_count = 0
        while attempt_count < max_attempts:
            run_semgrep_validate(rules_folder, output_file)

            errors = load_error_report(output_file)
            if not errors:
                log_and_print("Validation completed successfully. No errors found.")
                return  # End function if no errors

            log_and_print(f"Errors found in validation. Attempting to auto-debug (Attempt {attempt_count + 1}/{max_attempts}).")
            rule_id_path=extract_rule_ids_from_folder(rules_folder)
            auto_debug_errors(output_file,rule_id_path)

            attempt_count += 1

        # Final validation after max_attempts
        run_semgrep_validate(rules_folder, output_file)
        errors = load_error_report(output_file)

        if errors:
            log_and_print(f"Errors persist after {max_attempts} attempts. Renaming files with errors and restarting count.")
            for error in errors:
                file_path, _ = get_rule_source_code(error,rule_id_path)
                new_file_path = f"{file_path}.debug"
                # Check if debug file already exists
                if os.path.exists(new_file_path):
                    log_and_print(f"Debug file already exists: {new_file_path}")
                    continue
                if file_path:
                    os.rename(file_path, new_file_path)
                    log_and_print(f"Renamed file: {file_path} -> {new_file_path}")
            max_attempts=3
        else:
            log_and_print("Final validation completed. No errors found.")
            break  # End the outer while loop if no errors are found


def find_debug_files(directory):
    debug_files = []
    # Traverse all files and subdirectories in the directory
    for root, _, files in os.walk(directory):
        for file in files:
            # Check if the file ends with .debug
            if file.endswith('.debug'):
                debug_files.append(os.path.join(root, file))
    return debug_files


def read_textblock_file(filepath,textblock_path):
    """
    Read the corresponding .md file content based on the input .yaml.debug file path.

    Parameters:
    - filepath (str): Input .yaml.debug file path

    Returns:
    - str: If the file exists, returns the file content; otherwise, returns an error message.
    """
    try:
        # Get the relative path after removing the .debug suffix
        relative_path = yaml_path.split("/LLMrule/")[-1]
        # Remove the basename
        relative_path = os.path.dirname(relative_path)
        # Construct the .md file path
        md_file_path = os.path.join(textblock_path, f"{relative_path}_textres.md")
        print(f"md_file_path: {md_file_path}")
        # Read the .md file content
        if os.path.exists(md_file_path):
            with open(md_file_path, "r") as file:
                content = file.read()
            return content
        else:
            return f"Error: File not found at path {md_file_path}"
    
    except Exception as e:
        return f"Error occurred: {e}"


def parse_text(content):
    """Parse the text into a list of dictionaries, each containing name, type, and info keys"""
    sections = re.split(r'---\n', content)
    results = []
    for section in sections:
        if not section.strip():
            continue
        
        # Match the name field
        name_match = re.search(r'\*{0,2}name\*{0,2}:\s*(.+?)\n', section, re.IGNORECASE)
        name = name_match.group(1).strip() if name_match else None
        
        # Match the type field
        type_match = re.search(r'\*{0,2}type\*{0,2}:\s*(.+?)\n', section, re.IGNORECASE)
        type = type_match.group(1).strip() if type_match else None
        
        # Match the info field
        info_match = re.search(r'\*{0,2}info\*{0,2}:\s*(.+)', section, re.DOTALL | re.IGNORECASE)
        info = info_match.group(1).strip() if info_match else None

        # Only add to the results if all fields are found
        if name and type and info:
            results.append({
                'name': name,
                'type': type,
                'info': info
            })
    return results

def send_request(prompt, chat_id="v3remake", retries=3):
    """Send a request to the custom API with retry mechanism"""
    payload = {
        "chatId": chat_id,
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
            response = requests.post(url, json=payload, headers=headers, timeout=300)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except requests.exceptions.Timeout:
            print(f"Request timeout, retrying {attempt + 1}/{retries}...")
            attempt += 1
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed, exceeded maximum retry count ({retries})")
    return None


def remake_debug_rule(rules_folder, textblock_path):
    prompt2 = "Please refer to the semgrep_doc in the knowledge base for Semgrep syntax rules, and recheck the above for any syntax issues. If any issues are found, please correct them accordingly."
    
    # Find files ending with .debug
    debug_files = find_debug_files(rules_folder)
    print(f"Total .debug files: {len(debug_files)}")
    print(debug_files)
    
    for file in debug_files:
        print(f"Processing file: {file}")
        textblock = read_textblock_file(file,textblock_path)
        standards = parse_text(textblock)
        
        for standard in standards:
            # Ensure that the result is saved in standard['name']
            name = standard['name'].replace('** ', '')
            filerulename=re.sub(r"_[\d_]+\.yaml\.debug$", "", os.path.basename(file))
            if name != filerulename:
                print(f"File {filerulename} does not match standard {name}, skipping")
                continue
            
            prompt = f"name:{name}\ninfo:\n{standard['info']}"
            chat_id = str(time.time())
            
            # First API request
            response = send_request(prompt, chat_id)
            if not response:
                log_and_print("First request failed, retrying...")
                time.sleep(3)
                response = send_request(prompt, chat_id)
                if not response:
                    log_and_print(f"Second request failed, skipping this file {textblock}:{name}")
                    continue

            # Second API request
            followup_response = send_request(prompt2, chat_id)
            if not followup_response:
                log_and_print("Second request failed, skipping this file")
                continue
            print("Ask API Success\n")
            yaml_blocks = extract_yaml_from_response(followup_response)
            
            for i, yaml_block in enumerate(yaml_blocks):
                yaml_content = yaml_block.strip()
                
                # Generate save path
                save_file_path = file.replace('.debug', '')
                print(f"save_file_path: {save_file_path}")
            
                # Save YAML file
                save_yaml_file(yaml_content, save_file_path)
                log_and_print(f"Saved YAML file to: {save_file_path}")
            
            # Remove the original .debug file
            os.remove(file)
            print(f"Removed debug file: {file}")
    
    log_and_print("\n\n\n----------All debug files have been remade successfully----------\n")

def fix_debug(rules_folder, output_file,textblock_path,round_num=5):
    check_and_fix_errors(rules_folder, output_file, round_num)



if __name__ == "__main__":
    # Execute with specified JSON error report file
    rules_folder = ""
    output_file = ""
    textblock_path=""
    fix_debug(rules_folder, output_file,textblock_path,round_num=5)
