from llmevolucore import (
    textblock, filter_block, makerule,
    rulefix, rulestren, rulefix_r2, securitylevel, rmduplicates
)
from saevolucore import (
    countvote, checkfalsenum, nocheck,
    rmiddup, splitmd
)
from llog import log_and_print
import os
import shutil


def ensure_directory(path):
    """
    Check if a directory exists at the given path. If it does not exist, create it.

    Parameters:
    path (str): The path to the directory to check or create.

    Returns:
    str: A message indicating whether the directory already existed or was created.
    """
    if not os.path.exists(path):
        os.makedirs(path)
        return f"Directory '{path}' created."
    else:
        return f"Directory '{path}' already exists."


if __name__ == "__main__":
    doc_path = ""
    base_path = ""
    textblock_path = base_path + "textblock_fin/"
    vote_path = base_path + "vote_res/"
    rule_path = base_path + "LLMrule/"
    debug_file_path = base_path + "debug.json"
    r2_debug_file_path = base_path + "debug_r2.json"

    # Split markdown files that exceed token limits
    splitmd.process_folder(doc_path)

    # Generate text blocks
    log_and_print("*" * 10 + " Start generating text blocks " + "*" * 10)
    ensure_directory(textblock_path)
    textblock.process_directory(doc_path, textblock_path)
    log_and_print("*" * 10 + " Text block generation completed " + "*" * 10)

    # Remove unsuitable text blocks for rule generation
    log_and_print("*" * 10 + " Start filtering unsuitable text blocks " + "*" * 10)

    # LLM voting
    log_and_print("-" * 10 + " LLM voting " + "-" * 10)
    ensure_directory(vote_path)
    vote_list = []
    threshold = 4
    for i in range(threshold + 1):
        vote_false_res_path = vote_path + f"vote_false_files_{i}.txt"
        vote_list.append(vote_false_res_path)
        vote_false_path = vote_path + "vote_false_" + str(i) + "/"
        vote_true_path = vote_path + "vote_true_" + str(i) + "/"
        filter_block.process_directory(textblock_path, vote_false_path, vote_true_path)
        checkfalsenum.count_md_files(vote_false_path, vote_false_res_path)

    # Aggregate voting results
    log_and_print("-" * 10 + " Aggregating voting results " + "-" * 10)
    count_res_path = vote_path + f"at_least_{threshold}_files.txt"
    countvote.process_files(vote_list, count_res_path, threshold)
    false_list = nocheck.read_txt(count_res_path)
    false_name_list = nocheck.extract_filenames(false_list)
    log_and_print("*" * 10 + " Filtering of unsuitable text blocks completed " + "*" * 10)

    # Generate rules based on remaining text blocks
    log_and_print("*" * 10 + " Start generating rules " + "*" * 10)
    ensure_directory(rule_path)
    makerule.process_directory(textblock_path, rule_path, false_name_list)
    log_and_print("*" * 10 + " Rule generation completed " + "*" * 10)

    # Syntax checking and fix invalid rules
    log_and_print("*" * 10 + " Start syntax checking and rule fixing " + "*" * 10)
    rulefix.fix_debug(rule_path, debug_file_path, textblock_path)
    log_and_print("*" * 10 + " Syntax check and fix completed " + "*" * 10)

    # Rename duplicate rule IDs
    log_and_print("*" * 10 + " Start duplicate rule ID check and renaming " + "*" * 10)
    rmiddup.rename_dup(rule_path)
    log_and_print("*" * 10 + " Duplicate rule ID renaming completed " + "*" * 10)

    # Backup rules before deduplication
    if not os.path.exists(base_path + "LLMrule_before_dup/"):
        shutil.copytree(rule_path, base_path + "LLMrule_before_dup/")

    # Remove rules that detect the same issue
    log_and_print("*" * 10 + " Start removing duplicate detection rules " + "*" * 10)
    rmduplicates.rm_duplicates(rule_path)
    log_and_print("*" * 10 + " Duplicate detection rule removal completed " + "*" * 10)

    # Rule strengthening
    log_and_print("*" * 10 + " Start rule strengthening " + "*" * 10)
    rulestren.parse_and_save_rules_with_api_multithreaded(
        base_path + "LLMrule_strength6", base_path + "LLMrule_strength", max_threads=20
    )
    log_and_print("*" * 10 + " Rule strengthening completed " + "*" * 10)

    # Syntax checking and fix for strengthened rules
    log_and_print("*" * 10 + " Start syntax check for strengthened rules " + "*" * 10)
    rulefix_r2.fix_debug(base_path + "LLMrule_strength", r2_debug_file_path, 10)
    log_and_print("*" * 10 + " Syntax check for strengthened rules completed " + "*" * 10)

    # Backup strengthened rules before deduplication
    if not os.path.exists(base_path + "LLMrule_stren_duplicate/"):
        shutil.copytree(base_path + "LLMrule_strength9", base_path + "LLMrule_stren_duplicate/")

    # Remove duplicated strengthened rules
    log_and_print("*" * 10 + " Start removing duplicate strengthened rules " + "*" * 10)
    rmduplicates.rm_duplicates(base_path + "LLMrule_stren_duplicate/")
    log_and_print("*" * 10 + " Duplicate strengthened rule removal completed " + "*" * 10)

    # Rule severity classification
    log_and_print("*" * 10 + " Start rule severity classification " + "*" * 10)
    securitylevel.process_files_in_directory(base_path + "LLMrule_stren_duplicate", max_workers=20)

    log_and_print("*" * 10 + " All processes completed successfully " + "*" * 10)
