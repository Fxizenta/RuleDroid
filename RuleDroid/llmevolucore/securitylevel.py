import requests
import time
import re
import os
import logging
import threading
from concurrent.futures import ThreadPoolExecutor

logging.basicConfig(
    filename="makerule.log",  # Log file name
    level=logging.INFO,  # Log level
    format="%(asctime)s - %(levelname)s - %(message)s"  # Log format
)

def log_and_print(message):
    print(message)
    logging.info(message)

api_base_url = ""
api_key = ""

def send_request(prompt, chat_id, retries=3):
    payload = {
        "chatId": str(chat_id),
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
    for attempt in range(retries):
        try:
            response = requests.post(api_base_url, json=payload, headers=headers, timeout=360)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except requests.exceptions.Timeout:
            print(f"Request timeout, retrying {attempt + 1}/{retries}...")
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed after {retries} retries")
    return None

def find_and_replace_severity(file_path, new_severity):
    with open(file_path, 'r') as file:
        content = file.read()
    content = re.sub(r'severity:\s*(ERROR|WARNING|INFO)', f'severity: {new_severity}', content)
    with open(file_path, 'w') as file:
        file.write(content)

def process_file(file_path, chat_id):
    print(f"Processing file: {file_path}")
    with open(file_path, 'r') as f:
        file_content = f.read()
    result = send_request(file_content, chat_id=chat_id)
    if result:
        new_severity = 'ERROR' if 'ERROR' in result else 'WARNING' if 'WARNING' in result else 'INFO' if 'INFO' in result else None
        if new_severity:
            find_and_replace_severity(file_path, new_severity)
            log_and_print(f"Updated severity of {file_path} to {new_severity}")
        else:
            log_and_print(f"Unknown response: {result}")
    else:
        log_and_print(f"Failed to update severity for {file_path}")

def process_files_in_directory(directory, max_workers=5):
    chat_id = 1000
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = []
        for root, _, files in os.walk(directory):
            for file in files:
                if file.endswith((".yaml", ".yml")):
                    file_path = os.path.join(root, file)
                    futures.append(executor.submit(process_file, file_path, chat_id))
                    chat_id += 1
        for future in futures:
            future.result()

if __name__ == "__main__":
    directory_path = ""
    process_files_in_directory(directory_path)
