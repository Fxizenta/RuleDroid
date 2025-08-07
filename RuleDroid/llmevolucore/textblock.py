import requests
import os
import time
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
from llog import log_and_print

'''
This script is used for multithreaded generation of code blocks related to security issues.
'''

# Set API base URL and authentication info
api_base_url = 'http://'
api_key = ''

def check_text_format(text):
    """
    Check whether the input text matches the required format:
    1. Check whether "Has" is True or False.
    2. If "Has" is True, there must be at least one "safety standard" block.
    
    Parameters:
        text (str): The input text.
        
    Returns:
        bool: True if the format is valid, False otherwise.
    """
    # Check the first line for the "Has" field
    has_match = re.search(r"Has\s?:\s?(True|False)", text, re.IGNORECASE)
    if not has_match:
        print("The first line does not match the required format.")
        return False
    
    # Check the value of "Has"
    has_value = has_match.group(1)
    if has_value == "False":
        return True  # No need to check further if Has is False
    
    # If Has is True, check for at least one safety standard block
    safety_standard_pattern = r"(?<=\n---\n)\s*\#*\s*safety standard\s?\d+(\.|:|\n)"
    print(re.search(safety_standard_pattern, text, re.IGNORECASE))
    if not re.search(safety_standard_pattern, text, re.IGNORECASE):
        print("At least one 'safety standard' block is required.")
        return False
    else:
        return True


def process_file(filepath, readpath, savepath_base, max_retries=5, retry_delay=2):
    """
    Process a single file: read it, submit to the custom API, validate the response, and save the result.
    Includes retry mechanism in case of failures.
    """
    if not filepath.endswith('.md'):
        return

    with open(filepath, 'r', encoding='utf-8') as file:
        content = file.read()

    payload = {
        "stream": False,
        "detail": False,
        "messages": [
            {
                "role": "user",
                "content": content
            }
        ]
    }
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }

    for attempt in range(max_retries):
        try:
            response = requests.post(api_base_url, json=payload, headers=headers, timeout=600)
            response.raise_for_status()
            response_data = response.json()
            response_text = response_data['choices'][0]['message']['content']
            
            # Ensure format is correct
            if not check_text_format(response_text):
                log_and_print(f"Invalid format returned for {filepath}, retrying...")
                log_and_print(f"Retry attempt {attempt + 1}/{max_retries}")
                continue

            # Generate save path
            relative_path = os.path.relpath(filepath, start=readpath)
            new_filename = os.path.splitext(relative_path)[0] + "_textres.md"
            save_filepath = os.path.join(savepath_base, new_filename)

            os.makedirs(os.path.dirname(save_filepath), exist_ok=True)

            # Save the response content
            with open(save_filepath, 'w', encoding='utf-8') as save_file:
                save_file.write(response_text)
            log_and_print(f"Saved result to: {save_filepath}")

            break  # Exit loop if successful
        except (requests.ConnectionError, requests.Timeout, requests.HTTPError) as e:
            print(f"Error processing {filepath}, retrying {attempt + 1}/{max_retries}: {e}")
            time.sleep(retry_delay)
        except Exception as e:
            print(f"Unexpected error while processing {filepath}: {e}")
            break


def process_directory(readpath, savepath, max_workers=5):
    """
    Recursively read all files in a directory and process them concurrently.
    
    Parameters:
        readpath (str): Path to the directory containing input files.
        savepath (str): Path where the processed files will be saved.
        max_workers (int): Maximum number of worker threads.
    """
    print(f"Starting to process directory: {readpath}")
    all_files = []
    for root, _, files in os.walk(readpath):
        for file in files:
            all_files.append(os.path.join(root, file))

    with ThreadPoolExecutor(max_workers) as executor:
        futures = [executor.submit(process_file, file, readpath, savepath) for file in all_files]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f"Error occurred while processing file: {exc}")


if __name__ == "__main__":
    readpath = ""
    savepath = ""

    process_directory(readpath, savepath)
