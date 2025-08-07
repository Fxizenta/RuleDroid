import requests
import os
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
import time

'''
This script processes blocks of text (standards) and generates corresponding detection rules (YAML)
by sending the content to an LLM API. It includes validation and optimization of the rules.
'''

# Set the API URL and authentication token
api_base_url = ''
api_key = ''

# Second prompt used for rule optimization and syntax checking
prompt2 = (
    "Continue to optimize the above YAML detection rules to improve accuracy, reduce false positives, "
    "and achieve more comprehensive detection. However, please note that you should not add new types of "
    "detection rules, but simply improve the above detections, and at the same time ask you to perform a syntax check "
    "on the existing rules, which can be referred to the semgrep_doc knowledge base for specific grammar specifications, "
    "to ensure that the rules meet the syntax requirements and can run correctly."
)

def read_files_recursive(dir_path):
    """Recursively read all files under the specified directory"""
    files = []
    for root, _, filenames in os.walk(dir_path):
        for filename in filenames:
            print(f"Found file: {filename}")
            path = os.path.join(root, filename)
            files.append(path)
    return files

def parse_text(content):
    """Parse the text content into a list of dictionaries with keys: name, type, info"""
    sections = re.split(r'---\n', content)
    results = []
    for section in sections:
        if not section.strip():
            continue

        name_match = re.search(r'\*{0,2}name\*{0,2}\s?:\s*(.+?)\n', section, re.IGNORECASE)
        name = name_match.group(1).strip() if name_match else None

        type_match = re.search(r'\*{0,2}type\*{0,2}\s?:\s*(.+?)\n', section, re.IGNORECASE)
        type_ = type_match.group(1).strip() if type_match else None

        info_match = re.search(r'\*{0,2}info\*{0,2}\s?:\s*(.+)', section, re.DOTALL | re.IGNORECASE)
        info = info_match.group(1).strip() if info_match else None

        if name and type_ and info:
            results.append({'name': name, 'type': type_, 'info': info})
    return results

def send_request(prompt, chat_id="abcd", retries=3):
    """Send request to the LLM API with retry mechanism"""
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
            response = requests.post(api_base_url, json=payload, headers=headers, timeout=300)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except requests.exceptions.Timeout:
            print(f"Request timed out, retrying {attempt + 1}/{retries}...")
            attempt += 1
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed after maximum retries ({retries})")
    return None

def extract_yaml_from_response(response):
    """Extract YAML blocks from the response"""
    code_blocks = re.findall(r'```yaml(.*?)```', response, re.DOTALL)
    return code_blocks

def save_yaml_file(content, savepath):
    """Save YAML content to the specified path"""
    os.makedirs(os.path.dirname(savepath), exist_ok=True)
    with open(savepath, 'w', encoding='utf-8') as f:
        f.write(content)

def extract_filename(filename):
    """Clean and format the name to be used as a file name"""
    filename = filename.replace("*", "")
    filename = filename.rsplit(".", 1)[0]
    filename = filename.lstrip()
    return filename

def process_file(filepath, readpath, savepath_base, false_name_list):
    print(f"Processing file: {filepath}")
    """Process a single file: parse content, send to API, extract YAML and save"""
    with open(filepath, 'r', encoding='utf-8') as file:
        content = file.read()

    filename = os.path.basename(filepath)
    standards = parse_text(content)
    print(f"{filepath} contains {len(standards)} blocks")

    for i, standard in enumerate(standards):
        name = extract_filename(standard['name'])
        type_ = standard['type']
        info = standard['info']

        if 'Safety' not in type_:
            print(f"Skipping non-Safety Requirement block: {name}:{type_}")
            continue

        if name in false_name_list:
            print(f"Skipping previously flagged block (not suitable for rule generation): {name}")
            continue

        prompt = f"name:{name}\ninfo:\n{info}"
        chat_id = str(time.time()) + f"_{i}"

        response = send_request(prompt, chat_id)
        if not response:
            print("First request failed, retrying...")
            time.sleep(3)
            response = send_request(prompt, chat_id)
            if not response:
                print(f"Second request failed, skipping block: {name}")
                continue

        followup_response = send_request(prompt2, chat_id)
        yaml_blocks = extract_yaml_from_response(followup_response)
        for j, yaml_block in enumerate(yaml_blocks):
            yaml_content = yaml_block.strip()
            relative_path = Path(filepath).relative_to(readpath)
            save_file_path = os.path.join(savepath_base, relative_path.parent, filename[:-11], f"{name}_{i}_{j}.yaml")
            save_yaml_file(yaml_content, save_file_path)
            print(f"Saved YAML to: {save_file_path}")

def process_directory(readpath, savepath, false_name_list, max_workers=20):
    """Recursively process all files in a directory using multithreading"""
    all_files = read_files_recursive(readpath)
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [executor.submit(process_file, file, readpath, savepath, false_name_list) for file in all_files]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error while processing file: {exc}")

if __name__ == "__main__":
    # Input and output directories
    readpath = ""
    savepath = ""
    
    # You can call process_directory here with a false_name_list if ready
    # process_directory(readpath, savepath, false_name_list)
    
    file_list = read_files_recursive(readpath)
    print(file_list)
    print(f"Total files found: {len(file_list)}")
