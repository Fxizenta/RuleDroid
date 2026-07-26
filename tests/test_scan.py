import subprocess
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch


PROJECT_DIR = Path(__file__).resolve().parents[1] / "RuleDroid"
sys.path.insert(0, str(PROJECT_DIR))

import scan  # noqa: E402


class ScanPipelineTests(unittest.TestCase):
    def setUp(self):
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.root = Path(self.temporary_directory.name)
        self.report = self.root / "scan.json"

    def tearDown(self):
        self.temporary_directory.cleanup()

    @patch("scan.double_check_report")
    @patch("scan.subprocess.run")
    def test_default_scan_does_not_run_llm_step(self, run_semgrep, double_check):
        exit_code = scan.run(
            ["app", "--config", "rules", "--output", str(self.report)]
        )

        self.assertEqual(0, exit_code)
        run_semgrep.assert_called_once_with(
            [
                "semgrep",
                "scan",
                "--config",
                "rules",
                "--json",
                "--output",
                str(self.report.resolve()),
                "app",
            ],
            check=True,
        )
        double_check.assert_not_called()

    @patch("scan.double_check_report")
    @patch("scan.subprocess.run")
    def test_enabled_scan_runs_double_check_after_report(self, run_semgrep, double_check):
        state = {"scanned": False}

        def finish_scan(*_args, **_kwargs):
            state["scanned"] = True

        def finish_double_check(*_args, **_kwargs):
            self.assertTrue(state["scanned"])
            return {
                "llm_double_check": {
                    "counts": {
                        "confirmed": 1,
                        "false_positive": 2,
                        "unknown": 0,
                        "skipped": 3,
                    }
                }
            }

        run_semgrep.side_effect = finish_scan
        double_check.side_effect = finish_double_check

        exit_code = scan.run(
            [
                "app",
                "--config",
                "rules",
                "--output",
                str(self.report),
                "--llm-double-check",
                "--api-base-url",
                "https://llm.invalid/v1/chat/completions",
                "--api-key",
                "test-key",
            ]
        )

        self.assertEqual(0, exit_code)
        double_check.assert_called_once()
        report_path, filtered_path = double_check.call_args.args
        self.assertEqual(str(self.report.resolve()), report_path)
        self.assertEqual(
            str((self.root / "scan.llm-filtered.json").resolve()), filtered_path
        )
        self.assertTrue(double_check.call_args.kwargs["config"].enabled)

    @patch("scan.double_check_report")
    @patch("scan.subprocess.run")
    def test_failed_scan_never_runs_double_check(self, run_semgrep, double_check):
        run_semgrep.side_effect = subprocess.CalledProcessError(2, ["semgrep"])

        with self.assertRaises(subprocess.CalledProcessError):
            scan.run(
                [
                    "app",
                    "--config",
                    "rules",
                    "--output",
                    str(self.report),
                    "--llm-double-check",
                ]
            )

        double_check.assert_not_called()


if __name__ == "__main__":
    unittest.main()
