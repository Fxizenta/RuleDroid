import os
import yaml
import re
from collections import defaultdict
import requests
import time
import logging

'''
Used to detect and remove duplicate Semgrep rules.
'''

logging.basicConfig(
    filename="makerule.log",  # Log file name
    level=logging.INFO,                     # Log level
    format="%(asctime)s - %(levelname)s - %(message)s"  # Log format
)

api_base_url = ""
api_key = ""

def log_and_print(message):
    """Print and log message"""
    print(message)
    logging.info(message)

def find_all_files(root_dir, extensions=(".yml", ".yaml")):
    """Recursively traverse directory to find all files with the given extensions."""
    for root, _, files in os.walk(root_dir):
        for file in files:
            if file.endswith(extensions):
                yield os.path.join(root, file)

def normalize_api(api):
    """Normalize variables starting with $ into a common form like $Var.methodName."""
    if api.startswith("$"):
        parts = api.split(".", 1)
        if len(parts) == 2:
            return f"$Var.{parts[1]}"
        return "$Var"
    return api

def extract_apis_from_patterns(patterns):
    """Extract and normalize Java/Android APIs from patterns."""
    api_pattern = re.compile(r'(\$[a-zA-Z_][\w]*(?:\.[a-zA-Z_][\w]*)*|[a-zA-Z_][\w]*\.[a-zA-Z_][\w]*)\b')
    apis = set()

    def process_value(value):
        matched_apis = api_pattern.findall(value)
        apis.update(normalize_api(api) for api in matched_apis)

    if isinstance(patterns, list):
        for pattern in patterns:
            if isinstance(pattern, dict):
                for key, value in pattern.items():
                    if isinstance(value, str):
                        process_value(value)
                    elif isinstance(value, list):
                        apis.update(extract_apis_from_patterns(value))
    elif isinstance(patterns, str):
        process_value(patterns)

    return apis

def parse_semgrep_rule(file_path):
    """Parse Semgrep rule files and extract Java/Android APIs."""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rule_content = yaml.safe_load(f)
            if isinstance(rule_content, dict) and "rules" in rule_content:
                for rule in rule_content["rules"]:
                    rule_id = rule.get("id", "unknown")
                    file_apis = set()
                    for key in ["patterns", "pattern-either", "pattern-not", "pattern-inside", "pattern-not-inside", "pattern"]:
                        if key in rule:
                            apis = extract_apis_from_patterns(rule[key])
                            file_apis.update(apis)
                    if file_apis:
                        yield file_apis, rule_id, rule
    except Exception as e:
        print(f"Error parsing file {file_path}: {e}")

def group_rules_by_api(root_dir):
    """Group Semgrep rules based on Java/Android APIs."""
    api_groups = defaultdict(list)
    for file_path in find_all_files(root_dir):
        for apis, rule_id, rule_content in parse_semgrep_rule(file_path):
            for api in apis:
                api_groups[api].append((rule_id, file_path, rule_content))
    return api_groups

def send_request(prompt, chatid="o3mini", retries=3):
    payload = {
        "chatId": chatid,
        "stream": False,
        "detail": False,
        "messages": [{"role": "user", "content": prompt}]
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
            print(f"Request timed out. Retrying {attempt + 1}/{retries}...")
            attempt += 1
            time.sleep(2)
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")
            return None
    print(f"Request failed after {retries} attempts.")
    return None

def parse_response(response):
    result = {}
    response = response.replace("*", "")
    duplicates_match = re.search(r"\*?\*?\s*Duplicates\*?\*?\s*\*?\*?:\*?\*?\s*(True|False|Unknown)", response, re.IGNORECASE)
    if duplicates_match:
        result["Duplicates"] = duplicates_match.group(1).capitalize()
    else:
        result["Duplicates"] = "Unknown"
    if result["Duplicates"] in ["False", "Unknown"]:
        return result

    sections = response.split("---")
    repeats = {}
    for section in sections:
        repeat_match = re.search(
            r"Repeat\s+(\d+)((?:\s+rule\d+\s*:\s*\S+)+)\s+The Best\s*:\s*(\S+)",
            section, re.IGNORECASE)
        if repeat_match:
            repeat_id = repeat_match.group(1)
            rules_block = repeat_match.group(2)
            best = repeat_match.group(3)
            rules = re.findall(r"rule\d+\s*:\s*(\S+)", rules_block, re.IGNORECASE)
            repeat_key = f"Repeat {repeat_id}"
            repeats[repeat_key] = {f"rule{i+1}": rule for i, rule in enumerate(rules)}
            repeats[repeat_key]["The Best"] = best

    result.update(repeats)
    return result

def validate_response_format(response):
    """Validate the response format returned by the API."""
    if "---" not in response or not re.search(r"(?m)^---$", response):
        log_and_print("Invalid response format: missing delimiter ---")
        return False

    repeat_blocks = re.findall(r"(?<=---).*?Repeat", response, re.S)
    if len(repeat_blocks) != response.count("Repeat"):
        log_and_print("Invalid response format: missing delimiter between Repeat blocks")
        return False

    return True

def check_duplicate_rules(api, entries):
    prompt = ""
    for rule_id, file_path, rule_content in entries:
        prompt += f"ruleid: {rule_id}\n```yaml\n"
        prompt += yaml.dump(rule_content, allow_unicode=True)
        prompt += "\n```\n---"

    while True:
        response = send_request(prompt)
        log_and_print(f"response:\n{response}")
        result = parse_response(response)
        if result["Duplicates"] in ["False", "Unknown"]:
            log_and_print(f"\nfile_path: {file_path}\nAPI: {api}")
            log_and_print("duplicate result: " + result["Duplicates"])
            return []
        if not validate_response_format(response):
            log_and_print("Invalid format, resending request...")
            continue
        break

    del_list = []
    best_list = []
    all_rule = []

    log_and_print(f"\nfile_path: {file_path}\nAPI: {api}")
    log_and_print("duplicate result: " + result["Duplicates"])
    log_and_print("duplicate rules:")
    for repeat_key, rules in result.items():
        if repeat_key.startswith("Repeat"):
            best_list.append(rules.pop("The Best"))
            log_and_print(f"\n{repeat_key}:")
            log_and_print(f"\n{rules}:")
            for rule_key, rule in rules.items():
                all_rule.append(rule)

    for rule in all_rule:
        if rule not in best_list:
            del_list.append(rule)
            log_and_print(f"Deleting rule: {rule}")

    log_and_print(f"del_list: {del_list}")
    log_and_print(f"best_list: {best_list}")
    return del_list

def delete_duplicate_rules(api, del_list, entries):
    """Delete rules based on given list of rule IDs."""
    for rule_id, file_path, rule_content in entries:
        if rule_id in del_list:
            try:
                with open(file_path, 'r') as file:
                    data = yaml.safe_load(file)

                if "rules" not in data or not isinstance(data["rules"], list):
                    raise ValueError("Invalid YAML structure: 'rules' section missing or not a list.")

                if len(data["rules"]) == 1:
                    log_and_print(f"Only one rule found in {file_path}. Deleting the entire file.")
                    os.remove(file_path)
                    continue

                original_length = len(data["rules"])
                data["rules"] = [rule for rule in data["rules"] if rule.get("id") != rule_id]
                updated_length = len(data["rules"])

                if original_length == updated_length:
                    log_and_print(f"Rule with ID '{rule_id}' not found in {file_path}.")
                    continue

                with open(file_path, 'w') as file:
                    yaml.safe_dump(data, file, sort_keys=False)

                log_and_print(f"Rule with ID '{rule_id}' successfully deleted from {file_path}.")

            except Exception as e:
                log_and_print(f"Error processing file {file_path}: {e}")
    log_and_print(f"{api} completed.")
    log_and_print("=" * 50)

def rule_exists_in_file(rule_id, file_path):
    """Check whether the given rule ID still exists in the YAML file."""
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            rule_content = yaml.safe_load(f)
            if isinstance(rule_content, dict) and "rules" in rule_content:
                for rule in rule_content["rules"]:
                    if rule.get("id") == rule_id:
                        return True
    except Exception as e:
        print(f"Error checking file {file_path}: {e}")
    log_and_print(f"Rule {rule_id} no longer exists in file {file_path}.")
    return False

def rm_duplicates(root_dir):
    """Main entry point to remove duplicate rules grouped by APIs."""
    if not os.path.isdir(root_dir):
        print("Invalid directory path.")
        return

    api_groups = group_rules_by_api(root_dir)
    black_list = ["e.g", "$Var"]
    print("\nAPI Groups (with more than 2 rules):")
    num = 0
    for api, entries in api_groups.items():
        valid_entries = [entry for entry in entries if rule_exists_in_file(entry[0], entry[1])]
        if len(valid_entries) >= 2 and api not in black_list:
            num += 1
            log_and_print("=" * 50)
            log_and_print(f"\nAPI: {api}")
            print("Rules:")
            for rule_id, file_path, rule_content in entries:
                log_and_print(f"- Rule ID: {rule_id}")
                log_and_print(f"    File: {file_path}:{rule_id}")
            dup_list = check_duplicate_rules(api, entries)
            print(f"Rules to delete: {dup_list}")
            delete_duplicate_rules(api, dup_list, entries)
    print(f"\nFound {num} API groups.")

if __name__ == "__main__":
    root_dir = ""
    rm_duplicates(root_dir)
