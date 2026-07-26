"""Run Semgrep and optionally LLM-filter the generated JSON report."""

import argparse
import subprocess
from pathlib import Path
from typing import List, Optional

from llmevolucore.doublecheck import (
    add_double_check_arguments,
    config_from_args,
    double_check_report,
)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Scan a target with Semgrep, write JSON, then optionally run the "
            "LLM false-positive filter as the final step."
        )
    )
    parser.add_argument("target", help="source directory or file to scan")
    parser.add_argument(
        "-c", "--config", required=True, help="Semgrep rule file or directory"
    )
    parser.add_argument(
        "-o", "--output", required=True, help="raw Semgrep JSON report"
    )
    parser.add_argument(
        "--filtered-output",
        help="LLM-filtered sidecar path (defaults beside the raw report)",
    )
    parser.add_argument(
        "--semgrep", default="semgrep", help="Semgrep executable (default: semgrep)"
    )
    add_double_check_arguments(parser)
    return parser


def run(argv: Optional[List[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    report_path = Path(args.output).expanduser().resolve()
    report_path.parent.mkdir(parents=True, exist_ok=True)
    command = [
        args.semgrep,
        "scan",
        "--config",
        args.config,
        "--json",
        "--output",
        str(report_path),
        args.target,
    ]
    subprocess.run(command, check=True)
    print("Raw Semgrep report written to {}".format(report_path))

    if not args.llm_double_check:
        print("LLM double-check is disabled (default).")
        return 0

    filtered_path = (
        Path(args.filtered_output).expanduser().resolve()
        if args.filtered_output
        else report_path.with_name(report_path.stem + ".llm-filtered.json")
    )
    filtered = double_check_report(
        str(report_path),
        str(filtered_path),
        config=config_from_args(args),
    )
    counts = filtered["llm_double_check"]["counts"]
    print("LLM-filtered report written to {}".format(filtered_path))
    print(
        "confirmed={confirmed}, false_positive={false_positive}, "
        "unknown={unknown}, skipped={skipped}".format(**counts)
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(run())
