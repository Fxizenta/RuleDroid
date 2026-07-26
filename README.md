# RuleDroid and DroidCVE++ Benchmark

**RuleDroid** is a framework that leverages large language models (LLMs) to automatically generate [Semgrep](https://semgrep.dev/)-compatible static detection rules from up-to-date official Android security documentation. It addresses two critical issues: (i) the substantial manual effort required for rule generation and maintenance in traditional Static Application Security Testing (SAST), and (ii) the instability of applying pure LLM-based methods to large-scale Android app analysis. RuleDroid grounds LLM outputs with Retrieval-Augmented Generation (RAG) and a modular 12-phase workflow, then applies proven static-analysis techniques to deliver explainable, repeatable findings.

**DroidCVE++** is an extended benchmark that expands existing CVE-based datasets by covering more vulnerability types and providing precise, easily verifiable ground truth (e.g., vulnerability location details).

If you use RuleDroid or DroidCVE++ in your project, please cite **our IEEE TSE paper**:

[Zhentao Xie](https://fxizenta.github.io/), Mingyang Chen, Yaqi Gao, Shishuai Yang, [Wenrui Diao](https://diaowenrui.github.io/) (✉️), Xiangyu Liu, and Kehuan Zhang. RuleDroid: LLM-Augmented Synthesis of Static Security Detection Rules for Android Apps. IEEE Transactions on Software Engineering, 2026.

---

## Table of Contents

- [How It Works](#how-it-works)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Detailed Usage](#detailed-usage)
  - [1. Crawl Documentation](#1-crawl-documentation)
  - [2. Run the Rule Generation Pipeline](#2-run-the-rule-generation-pipeline)
  - [3. Validate & Run Generated Rules](#3-validate--run-generated-rules)
- [API Configuration](#api-configuration)
- [DroidCVE++ Benchmark](#droidcve-benchmark)
- [Example Generated Rules](#example-generated-rules)
- [Available Materials](#available-materials)

---

## How It Works

RuleDroid operates in a **12-phase pipeline**:

| Phase | Module | Description |
|-------|--------|-------------|
| 1 | `saevolucore/splitmd.py` | Split oversized Markdown documentation into manageable chunks |
| 2 | `llmevolucore/textblock.py` | Parse security requirements from docs and extract structured text blocks via LLM |
| 3 | `llmevolucore/filter_block.py` | LLM voting (5 rounds) to filter out blocks unsuitable for rule generation |
| 4 | `saevolucore/countvote.py` | Aggregate voting results; blocks marked "unsuitable" ≥4/5 times are excluded |
| 5 | `llmevolucore/makerule.py` | Generate Semgrep YAML rules from qualifying text blocks via LLM |
| 6 | `llmevolucore/rulefix.py` | Syntax-validate rules with `semgrep --validate` and auto-fix errors via LLM |
| 7 | `saevolucore/rmiddup.py` | Detect and rename duplicate rule IDs |
| 8 | `llmevolucore/rmduplicates.py` | Remove semantically-duplicate rules |
| 9 | `llmevolucore/rulestren.py` | Strengthen rules via LLM re-evaluation (identifies deficiencies, proposes improvements) |
| 10 | `llmevolucore/rulefix_r2.py` | Syntax-check the strengthened rules |
| 11 | `llmevolucore/rmduplicates.py` | Deduplicate the strengthened rule set |
| 12 | `llmevolucore/securitylevel.py` | Classify each rule's severity level |

This multi-stage design ensures that the final rule set is syntactically valid, semantically non-redundant, and of high detection quality.

---

## Project Structure

```
├── Craw/                          # Documentation crawler
│   └── craw.py                    #   Async crawler for Android/Semgrep doc sites
├── DroidCVE_benchmark/            # VulsTotal-CVE++ benchmark (88 APKs with CVEs)
├── RuleDroid_1_Rule/              # Example generated Semgrep rules (organized by category)
├── RuleDroid/                     # Core RuleDroid framework
│   ├── EvoluDroid.py              #   Main orchestrator — runs the full 12-phase pipeline
│   ├── llog.py                    #   Logging + print utility
│   ├── requirements.txt           #   Python dependencies
│   ├── llmevolucore/              #   LLM-based rule evolution modules
│   │   ├── common.py              #     Shared: API client, text/YAML parsing, file I/O
│   │   ├── textblock.py           #     Phase 2: Generate structured text blocks from docs
│   │   ├── filter_block.py        #     Phase 3: LLM voting to filter unsuitable blocks
│   │   ├── makerule.py            #     Phase 5: Generate Semgrep rules from text blocks
│   │   ├── rulefix.py             #     Phase 6: Validate & auto-fix rule syntax
│   │   ├── rulefix_r2.py          #     Phase 10: Validate strengthened rules
│   │   ├── rmduplicates.py        #     Phase 8/11: Remove duplicate rules
│   │   ├── rulestren.py           #     Phase 9: Strengthen rules via LLM
│   │   ├── securitylevel.py       #     Phase 12: Classify rule severity
│   │   └── rule_utils.py          #     Shared rule helpers (validation, ID extraction)
│   └── saevolucore/               #   Supporting analysis modules (non-LLM)
│       ├── splitmd.py             #     Phase 1: Split oversize Markdown files
│       ├── checkfalsenum.py       #     Phase 3 helper: Count false votes
│       ├── countvote.py           #     Phase 4: Aggregate voting results
│       ├── nocheck.py             #     Phase 4 helper: Read and extract filenames
│       ├── rmiddup.py             #     Phase 7: Rename duplicate rule IDs
│       ├── htmltomd.py            #     HTML-to-Markdown converter
│       └── splitmd.py             #     Markdown file splitter
└── README.md
```

---

## Quick Start

### Prerequisites

- **Python 3.10+**
- **Semgrep CLI** (for rule validation: `pip install semgrep`)
- **Access to an OpenAI-compatible LLM API** (base URL + API key)

### Installation

```bash
cd RuleDroid
pip install -r requirements.txt
```

Install Semgrep for rule validation:

```bash
pip install semgrep
```

---

## Detailed Usage

### 1. Crawl Documentation

The crawler in [Craw/craw.py](Craw/craw.py) asynchronously fetches pages from documentation sites (e.g., `developer.android.com`, `semgrep.dev/docs`), filters pages containing security-relevant keywords, and saves them locally.

**Configuration** — edit these variables in [Craw/craw.py](Craw/craw.py):

```python
start_url = "https://developer.android.com/"   # Base URL to crawl
max_stack_size = 70000                          # Max URLs to visit
concurrent_limit = 30                           # Concurrent connections
proxy = "http://ip:port"                        # Optional HTTP proxy
```

**Run the crawler:**

```bash
cd Craw
python craw.py
```

Saved HTML pages are stored under the configured `savepath` directory. These crawled docs become the input for the RuleDroid pipeline.

---

### 2. Run the Rule Generation Pipeline

The main entry point is [RuleDroid/EvoluDroid.py](RuleDroid/EvoluDroid.py). It orchestrates all 12 phases in sequence.

#### Step 1: Configure API Credentials

Edit [RuleDroid/llmevolucore/common.py](RuleDroid/llmevolucore/common.py) to set your LLM API endpoint:

```python
api_base_url = "https://your-llm-api.example.com/v1/chat/completions"
api_key = "your-api-key-here"
```

The API must be OpenAI-format compatible (supports `{"messages": [...], "stream": false}`).

#### Step 2: Set Input/Output Paths

Edit the paths in [RuleDroid/EvoluDroid.py](RuleDroid/EvoluDroid.py) (lines 57–64):

```python
doc_path = "/path/to/crawled/docs/"          # Input: crawled HTML/Markdown files
base_path = "/path/to/output/"               # Output root directory
textblock_path = base_path + "textblock_fin/" # Phase 2 output
vote_path = base_path + "vote_res/"           # Phase 3-4 output
rule_path = base_path + "LLMrule/"            # Phase 5+ output
```

#### Step 3: Adjust Thread Counts

Thread counts control the concurrency of LLM API calls. Adjust them based on your API provider's TPM (Tokens Per Minute) limits:

| Module | Parameter | Default | Location |
|--------|-----------|---------|----------|
| `makerule.py` | `max_workers` | `20` | `process_directory()` call |
| `filter_block.py` | `max_workers` | `30` | `process_directory()` call |
| `rulestren.py` | `max_threads` | `10` (10 in main, 20 in EvoluDroid) | `parse_and_save_rules_with_api_multithreaded()` call |
| `securitylevel.py` | `max_workers` | `20` | `process_files_in_directory()` call |

#### Step 4: Run the Pipeline

```bash
cd RuleDroid
python EvoluDroid.py
```

The pipeline will execute all 12 phases sequentially and log progress to both stdout and log files (`LLMmakerule.log`, `LLMmakerule2.log`).

#### Step 5: Run Individual Phases (Optional)

Each module can also be run standalone. For example, to only generate rules from existing text blocks:

```python
# In llmevolucore/makerule.py
if __name__ == "__main__":
    readpath = "/path/to/textblocks/"
    savepath = "/path/to/output/rules/"
    false_name_list = []  # or load from vote results
    process_directory(readpath, savepath, false_name_list)
```

Similarly, to only validate and fix existing rules:

```python
# In llmevolucore/rulefix.py
if __name__ == "__main__":
    rules_folder = "/path/to/rules/"
    output_file = "/path/to/debug.json"
    fix_debug(rules_folder, output_file)
```

---

### 3. Validate & Run Generated Rules

The final output is a directory of Semgrep YAML rule files. Use them with the Semgrep CLI:

```bash
# Validate all rules
semgrep --config /path/to/rules/ --validate

# Scan an Android project
semgrep --config /path/to/rules/ /path/to/android/source/
```

---

## API Configuration

RuleDroid uses an OpenAI-compatible chat completions API. Configure it in [llmevolucore/common.py](RuleDroid/llmevolucore/common.py):

```python
# In llmevolucore/common.py
api_base_url = "https://api.openai.com/v1/chat/completions"  # or any compatible endpoint
api_key = "sk-..."
```

The request format:

```json
{
  "stream": false,
  "detail": false,
  "chatId": "<unique-session-id>",
  "messages": [
    {"role": "user", "content": "<prompt>"}
  ]
}
```

The `chatId` parameter enables multi-turn conversations (used in `makerule.py` for rule generation → optimization flows).

> **Note:** System prompts for each LLM instantiation in the workflow will be released following publication of the paper.

---

## DroidCVE++ Benchmark

The [DroidCVE_benchmark/](DroidCVE_benchmark/) directory contains a curated benchmark of **88 real-world Android APKs** with known CVEs, organized by vulnerability category:

| Category | Example CVEs |
|----------|-------------|
| Manifest Backup Issue | CVE-2020-35454, CVE-2023-36620, CVE-2017-16835 |
| ContentProvider Permissions | CVE-2018-14902, CVE-2021-43863 |
| Runtime Command Execution | CVE-2022-30083, CVE-2023-36351, CVE-2023-29738 |
| Hardcoded Encryption Issues | CVE-2019-8919 (IV), CVE-2017-15997 (RC4), CVE-2021-41096 (AES) |
| Logging Data Exposure | CVE-2016-1518, CVE-2019-17398, CVE-2020-27413 (and 10+ more) |
| WebView Vulnerabilities | CVE-2019-12367~12370 (Local File Access), CVE-2022-28799 (JS Execution) |
| Allow All Hostname Verification | 40+ CVEs across many apps |
| SQL Injection | CVE-2023-27647, CVE-2023-29725, CVE-2021-43863 |
| Custom URL Scheme Issues | CVE-2021-20728, CVE-2022-41797, CVE-2023-39507 |
| Exported Not Protected Components | 20+ CVEs |
| Misuse Implicit Intent | CVE-2020-35173, CVE-2022-39915, CVE-2019-1677 |
| Hardcoded Sensitive Data | CVE-2022-37857, CVE-2020-7999, CVE-2017-13106 |
| Using HTTP (Cleartext Traffic) | CVE-2017-9045, CVE-2019-8345, CVE-2016-1520 |
| External/Internal Data Exposure | CVE-2022-39210, CVE-2018-3988, CVE-2021-25266 |
| Path Traversal | CVE-2023-24804, CVE-2021-40668 |
| Intent Redirection | CVE-2024-26131 |
| Other | IMEI Exposure, XXE Injection, Insecure Data Sharing |

Each benchmark entry maps to the specific vulnerable source files and `AndroidManifest.xml`, enabling precise evaluation of rule detection accuracy.

---

## Example Generated Rules

Example rules generated by RuleDroid are available in [RuleDroid_1_Rule/](RuleDroid_1_Rule/), organized by security category from the Android documentation:

```
RuleDroid_1_Rule/androidoc/declare-data-use/
├── Device or Other IDs Data Collection_14_0/
│   ├── android-api-advertising-identifier.yaml
│   ├── android-permission-read-privileged-phone-state.yaml
│   ├── android-api-mac-address.yaml
│   └── ... (7 rules)
├── Health and Fitness Data Collection_5_0/
│   ├── android-health-fitness-permissions.yaml
│   └── ... (4 rules)
├── Location Data Collection_2_0/
│   ├── detect-access-coarse-location.yaml
│   └── ... (4 rules)
├── Contacts Data Collection_10_0/
├── Files and Docs Data Collection_9_0/
├── App Activity Data Collection_11_0/
└── App Info and Performance Data Collection_13_0/
```

Each `.yaml` file is a fully-compliant Semgrep rule with `id`, `languages`, `patterns`, `severity`, `message`, `metadata`, and `remediation` fields.

---

## Available Materials

### Already Provided

| Resource | Location | Description |
|----------|----------|-------------|
| RuleDroid Source | [RuleDroid/](RuleDroid/) | Full 12-phase pipeline implementation |
| DroidCVE++ Benchmark | [DroidCVE_benchmark/](DroidCVE_benchmark/) | 88 real-world CVE-labeled APKs for evaluation |
| Example Generated Rules | [RuleDroid_1_Rule/](RuleDroid_1_Rule/) | Sample Semgrep rules generated from Android docs |
| Doc Crawler | [Craw/](Craw/) | Async crawler for security documentation |

### To Be Released (following publication)

- System prompts for each LLM instantiation in the workflow
- RAG knowledge base database and corresponding Docker image

---

## Citation

If you use RuleDroid or the DroidCVE++ benchmark in your research, please cite the corresponding paper (details forthcoming upon publication).
