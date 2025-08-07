import requests
import os
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
import time
from llog import log_and_print

'''
This script processes text blocks to filter out those that are deemed unsuitable by the LLM for semgrep detection.
It identifies blocks that produce a "False" result.
The script may need to be run multiple times with a voting mechanism.
'''

# API configuration
api_base_url = ''
api_key = ''

def read_files_recursive(dir_path):
    """Recursively read all file paths in the given directory."""
    files = []
    for root, _, filenames in os.walk(dir_path):
        for filename in filenames:
            files.append(os.path.join(root, filename))
    return files

def parse_text(content):
    """Parse the text into a list of dictionaries with keys: name, type, and info."""
    sections = re.split(r'---\n', content)
    results = []
    for section in sections:
        if not section.strip():
            continue

        name_match = re.search(r'\*{0,2}name\*{0,2}:\s*(.+?)\n', section, re.IGNORECASE)
        name = name_match.group(1).strip() if name_match else None

        type_match = re.search(r'\*{0,2}type\*{0,2}:\s*(.+?)\n', section, re.IGNORECASE)
        type_ = type_match.group(1).strip() if type_match else None

        info_match = re.search(r'\*{0,2}info\*{0,2}:\s*(.+)', section, re.DOTALL | re.IGNORECASE)
        info = info_match.group(1).strip() if info_match else None

        if name and type_ and info:
            results.append({
                'name': name,
                'type': type_,
                'info': info
            })
    return results

def send_request(prompt, retries=3):
    """Send a request to the custom API and return the response, with retry logic."""
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
            response = requests.post(api_base_url, json=payload, headers=headers, timeout=560)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except requests.exceptions.Timeout:
            print(f"Request timed out, retrying {attempt + 1}/{retries}...")
            attempt += 1
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed after {retries} retries.")
    return None

def save_file(content, savepath):
    """Save markdown content to the specified path."""
    os.makedirs(os.path.dirname(savepath), exist_ok=True)
    with open(savepath, 'w', encoding='utf-8') as f:
        f.write(content)

def process_file(filepath, readpath, savepath_base, savepath_true):
    log_and_print(f"Processing file: {filepath}")
    """Process a single file: parse, send to API, extract and save markdown result."""
    with open(filepath, 'r', encoding='utf-8') as file:
        content = file.read()
    filename = os.path.basename(filepath)
    standards = parse_text(content)
    log_and_print(f"File {filepath} contains {len(standards)} standards")

    for i, standard in enumerate(standards):
        if 'Safety' not in standard['type']:
            log_and_print(f"{filepath} skipping non-Safety Requirements standard: {standard['name']}:{standard['type']}")
            continue
        name = standard['name'].replace("* ", "").replace("*", "")
        prompt = f"name:{standard['name']}\ninfo:\n{standard['info']}"
        
        relative_path = Path(filepath).relative_to(readpath)
        save_false_path = os.path.join(savepath_base, relative_path.parent, filename[:-11], f"{name}.md")
        save_true_path = os.path.join(savepath_true, relative_path.parent, filename[:-11], f"{name}.md")
        
        if os.path.exists(save_false_path) or os.path.exists(save_true_path):
            log_and_print(f"{filepath}/{name} already exists, skipping this standard: {name}")
            continue

        response = send_request(prompt)
        if not response:
            log_and_print(f"{filepath}/{name} first request failed, retrying...")
            time.sleep(3)
            response = send_request(prompt)
            if not response:
                log_and_print(f"{filepath}/{name} second request failed, skipping this standard: {name}")
                continue

        res = re.compile(r"false\n", re.IGNORECASE | re.MULTILINE)

        if res.search(response):
            log_and_print(f"False: {filepath} {name}")
            content = "**BlockText**\n\n" + prompt + "\n\n**Response**\n\n" + response
            save_file(content, save_false_path)
            log_and_print(f"Saved YAML to: {save_false_path}")
        else:
            log_and_print(f"True: {filepath} {name}")
            content = "**BlockText**\n\n" + prompt + "\n\n**Response**\n\n" + response
            save_file(content, save_true_path)
            log_and_print(f"Saved YAML to: {save_true_path}")

def process_directory(readpath, savepath, savepath_true, max_workers=30):
    """Recursively process all files in a directory using multithreading."""
    all_files = read_files_recursive(readpath)

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [executor.submit(process_file, file, readpath, savepath, savepath_true) for file in all_files]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error occurred while processing a file: {exc}")

if __name__ == "__main__":
    # Input and output paths
    readpath = ""  # Replace with actual input path
    savepath = ""  # Replace with actual output path for False results
    savepath_true = ""  # Output path for True results

    # Start processing the directory
    process_directory(readpath, savepath, savepath_true)
