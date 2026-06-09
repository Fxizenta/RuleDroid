"""
LLM Evolution Core package.

Handles text-block generation, filtering, rule creation, syntax fixing,
deduplication, and rule strengthening via LLM APIs.
"""

from llmevolucore.common import (
    api_base_url,
    api_key,
    send_request,
    parse_text,
    extract_yaml_from_response,
    save_yaml_file,
    read_files_recursive,
)
from llmevolucore.rule_utils import (
    load_error_report,
    run_semgrep_validate,
    find_debug_files,
    extract_rule_ids_from_folder,
    get_rule_source_code,
)

__all__ = [
    # common
    "api_base_url",
    "api_key",
    "send_request",
    "parse_text",
    "extract_yaml_from_response",
    "save_yaml_file",
    "read_files_recursive",
    # rule_utils
    "load_error_report",
    "run_semgrep_validate",
    "find_debug_files",
    "extract_rule_ids_from_folder",
    "get_rule_source_code",
]
