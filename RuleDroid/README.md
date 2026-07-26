# Setting

Before use, configure each LLM, and all prompts in the sysname solution are directly configured in the system prompt in the LLM instance.(base url and key in llmevolucore)

Adjust the thread according to the runtime environment and the TPM limits of the LLM vendor

## Optional report double-check

The scan entry point can optionally filter its JSON report with an LLM as its
final step. The feature is disabled unless `--llm-double-check` is present:

```bash
export RULEDROID_LLM_API_BASE_URL="https://your-llm.example/v1/chat/completions"
export RULEDROID_LLM_API_KEY="replace-with-your-key"
python scan.py path/to/source \
  --config path/to/rules \
  --output report.json \
  --llm-double-check
```

This creates `report.llm-filtered.json` without changing `report.json`.
False positives are removed from `results` but retained under
`llm_double_check.suppressed_results` for audit. LLM or source-reading
failures remain in the report with an `unknown` decision (fail-open).

Use `--all-rules` to review all rule IDs or `--rule-id-file FILE` for a custom
allowlist. See `python doublecheck.py --help` for API, model, output,
concurrency, timeout, and retry options.

An already-generated report can instead be processed with:

```bash
python doublecheck.py report.json --llm-double-check
```
