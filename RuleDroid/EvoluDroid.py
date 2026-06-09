"""
EvoluDroid — Main orchestrator for the RuleDroid pipeline.

Phases
------
1. Split oversized Markdown files.
2. Generate text blocks via LLM.
3. Filter unsuitable blocks (LLM voting).
4. Aggregate votes and exclude false blocks.
5. Generate Semgrep rules from remaining blocks.
6. Syntax-check and fix rules.
7. Rename duplicate rule IDs.
8. Remove semantically-duplicate rules.
9. Strengthen rules via LLM re-evaluation.
10. Syntax-check strengthened rules.
11. Deduplicate strengthened rules.
12. Classify rule severity.
"""

import os
import shutil

from llmevolucore import (
    filter_block,
    makerule,
    rmduplicates,
    rulefix,
    rulefix_r2,
    rulestren,
    securitylevel,
    textblock,
)
from saevolucore import (
    checkfalsenum,
    countvote,
    nocheck,
    rmiddup,
    splitmd,
)
from llog import log_and_print


def ensure_directory(path: str) -> str:
    """
    Create a directory at *path* if it does not already exist.

    Returns a message describing whether the directory already existed or was
    created.
    """
    if not os.path.exists(path):
        os.makedirs(path)
        return f"Directory '{path}' created."
    return f"Directory '{path}' already exists."


if __name__ == "__main__":
    doc_path = ""
    base_path = ""
    textblock_path = base_path + "textblock_fin/"
    vote_path = base_path + "vote_res/"
    rule_path = base_path + "LLMrule/"
    debug_file_path = base_path + "debug.json"
    r2_debug_file_path = base_path + "debug_r2.json"

    # ------------------------------------------------------------------
    # Phase 1 — Split oversize Markdown files
    # ------------------------------------------------------------------
    splitmd.process_folder(doc_path)

    # ------------------------------------------------------------------
    # Phase 2 — Generate text blocks
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start generating text blocks " + "*" * 10)
    ensure_directory(textblock_path)
    textblock.process_directory(doc_path, textblock_path)
    log_and_print("*" * 10 + " Text block generation completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 3 — LLM voting to filter unsuitable blocks
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start filtering unsuitable text blocks " + "*" * 10)

    log_and_print("-" * 10 + " LLM voting " + "-" * 10)
    ensure_directory(vote_path)
    vote_list: list[str] = []
    threshold = 4
    for i in range(threshold + 1):
        vote_false_res_path = vote_path + f"vote_false_files_{i}.txt"
        vote_list.append(vote_false_res_path)
        vote_false_path = vote_path + "vote_false_" + str(i) + "/"
        vote_true_path = vote_path + "vote_true_" + str(i) + "/"
        filter_block.process_directory(
            textblock_path, vote_false_path, vote_true_path
        )
        checkfalsenum.count_md_files(vote_false_path, vote_false_res_path)

    # ------------------------------------------------------------------
    # Phase 4 — Aggregate voting results
    # ------------------------------------------------------------------
    log_and_print("-" * 10 + " Aggregating voting results " + "-" * 10)
    count_res_path = vote_path + f"at_least_{threshold}_files.txt"
    countvote.process_files(vote_list, count_res_path, threshold)
    false_list = nocheck.read_txt(count_res_path)
    false_name_list = nocheck.extract_filenames(false_list)
    log_and_print("*" * 10 + " Filtering of unsuitable text blocks completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 5 — Generate rules from qualifying text blocks
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start generating rules " + "*" * 10)
    ensure_directory(rule_path)
    makerule.process_directory(textblock_path, rule_path, false_name_list)
    log_and_print("*" * 10 + " Rule generation completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 6 — Syntax-check and fix rules
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start syntax checking and rule fixing " + "*" * 10)
    rulefix.fix_debug(rule_path, debug_file_path, textblock_path)
    log_and_print("*" * 10 + " Syntax check and fix completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 7 — Rename duplicate rule IDs
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start duplicate rule ID check and renaming " + "*" * 10)
    rmiddup.rename_dup(rule_path)
    log_and_print("*" * 10 + " Duplicate rule ID renaming completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 8 — Backup & remove semantically-duplicate rules
    # ------------------------------------------------------------------
    if not os.path.exists(base_path + "LLMrule_before_dup/"):
        shutil.copytree(rule_path, base_path + "LLMrule_before_dup/")

    log_and_print("*" * 10 + " Start removing duplicate detection rules " + "*" * 10)
    rmduplicates.rm_duplicates(rule_path)
    log_and_print("*" * 10 + " Duplicate detection rule removal completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 9 — Rule strengthening
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start rule strengthening " + "*" * 10)
    rulestren.parse_and_save_rules_with_api_multithreaded(
        base_path + "LLMrule_strength6",
        base_path + "LLMrule_strength",
        max_threads=20,
    )
    log_and_print("*" * 10 + " Rule strengthening completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 10 — Syntax-check strengthened rules
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start syntax check for strengthened rules " + "*" * 10)
    rulefix_r2.fix_debug(
        base_path + "LLMrule_strength", r2_debug_file_path, "", 10
    )
    log_and_print("*" * 10 + " Syntax check for strengthened rules completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 11 — Backup & deduplicate strengthened rules
    # ------------------------------------------------------------------
    if not os.path.exists(base_path + "LLMrule_stren_duplicate/"):
        shutil.copytree(
            base_path + "LLMrule_strength9",
            base_path + "LLMrule_stren_duplicate/",
        )

    log_and_print("*" * 10 + " Start removing duplicate strengthened rules " + "*" * 10)
    rmduplicates.rm_duplicates(base_path + "LLMrule_stren_duplicate/")
    log_and_print("*" * 10 + " Duplicate strengthened rule removal completed " + "*" * 10)

    # ------------------------------------------------------------------
    # Phase 12 — Rule severity classification
    # ------------------------------------------------------------------
    log_and_print("*" * 10 + " Start rule severity classification " + "*" * 10)
    securitylevel.process_files_in_directory(
        base_path + "LLMrule_stren_duplicate", max_workers=20
    )

    log_and_print("*" * 10 + " All processes completed successfully " + "*" * 10)
