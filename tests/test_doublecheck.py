import json
import sys
import tempfile
import unittest
from pathlib import Path


PROJECT_DIR = Path(__file__).resolve().parents[1] / "RuleDroid"
sys.path.insert(0, str(PROJECT_DIR))

from llmevolucore.doublecheck import (  # noqa: E402
    DoubleCheckConfig,
    double_check_report,
    parse_verdict,
)


class DoubleCheckReportTests(unittest.TestCase):
    def setUp(self):
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.root = Path(self.temporary_directory.name)
        self.source = self.root / "MainActivity.java"
        self.source.write_text(
            "\n".join("line {}".format(number) for number in range(1, 121)),
            encoding="utf-8",
        )
        self.report = self.root / "report.json"

    def tearDown(self):
        self.temporary_directory.cleanup()

    def _write_report(self, findings):
        payload = {"version": "1.0", "results": findings}
        self.report.write_text(json.dumps(payload), encoding="utf-8")
        return payload

    def _config(self, **changes):
        values = {
            "enabled": True,
            "api_base_url": "https://llm.invalid/v1/chat/completions",
            "api_key": "test-key",
            "max_workers": 2,
            "rule_ids": None,
        }
        values.update(changes)
        return DoubleCheckConfig(**values)

    def _finding(self, rule_id, path=None, line=60):
        return {
            "check_id": "android.security.{}".format(rule_id),
            "path": str(path or self.source),
            "start": {"line": line},
            "extra": {"message": "中文漏洞"},
        }

    def test_disabled_is_a_no_op_and_does_not_call_llm_or_write_output(self):
        original = self._write_report([self._finding("rule-a")])
        output = self.root / "unused.json"

        def unexpected_call(_prompt, _config):
            self.fail("disabled feature must not call the LLM")

        result = double_check_report(
            str(self.report),
            str(output),
            config=DoubleCheckConfig(),
            caller=unexpected_call,
        )

        self.assertEqual(original, result)
        self.assertFalse(output.exists())

    def test_filters_false_positive_but_keeps_it_in_audit_data(self):
        self._write_report(
            [
                self._finding("confirmed"),
                self._finding("false-positive"),
                self._finding("unparseable"),
            ]
        )

        def fake_llm(prompt, _config):
            if "false-positive" in prompt:
                return '{"vulnerable": false, "reason": "guarded before use"}'
            if "unparseable" in prompt:
                return "I cannot decide."
            return '{"vulnerable": true, "reason": "unsafe value reaches sink"}'

        output = self.root / "report.llm-filtered.json"
        result = double_check_report(
            str(self.report), str(output), self._config(), fake_llm
        )

        self.assertEqual(
            ["android.security.confirmed", "android.security.unparseable"],
            [finding["check_id"] for finding in result["results"]],
        )
        metadata = result["llm_double_check"]
        self.assertEqual(
            {"confirmed": 1, "false_positive": 1, "unknown": 1, "skipped": 0},
            metadata["counts"],
        )
        self.assertEqual(
            "android.security.false-positive",
            metadata["suppressed_results"][0]["check_id"],
        )
        self.assertEqual(
            "false_positive",
            metadata["suppressed_results"][0]["extra"]["llm_double_check"]["status"],
        )
        self.assertTrue(output.exists())
        self.assertEqual(result, json.loads(output.read_text(encoding="utf-8")))
        self.assertEqual(3, len(json.loads(self.report.read_text())["results"]))

    def test_missing_source_is_unknown_and_processing_continues(self):
        self._write_report(
            [
                self._finding("missing", self.root / "missing.java"),
                self._finding("valid"),
            ]
        )
        called = []

        def fake_llm(_prompt, _config):
            called.append(True)
            return "Vul: False\nThe surrounding guard prevents exploitation."

        result = double_check_report(
            str(self.report), config=self._config(), caller=fake_llm
        )

        self.assertEqual(1, len(called))
        self.assertEqual(1, result["llm_double_check"]["counts"]["unknown"])
        self.assertEqual(1, result["llm_double_check"]["counts"]["false_positive"])
        self.assertEqual(["android.security.missing"], [
            finding["check_id"] for finding in result["results"]
        ])

    def test_allowlist_skips_non_candidates_without_an_llm_call(self):
        self._write_report(
            [self._finding("review-me"), self._finding("leave-me")]
        )
        prompts = []

        def fake_llm(prompt, _config):
            prompts.append(prompt)
            return '{"vulnerable": true, "reason": "confirmed"}'

        result = double_check_report(
            str(self.report),
            config=self._config(rule_ids=frozenset({"review-me"})),
            caller=fake_llm,
        )

        self.assertEqual(1, len(prompts))
        self.assertEqual(1, result["llm_double_check"]["counts"]["confirmed"])
        self.assertEqual(1, result["llm_double_check"]["counts"]["skipped"])
        self.assertNotIn(
            "llm_double_check", result["results"][1].get("extra", {})
        )

    def test_allowlist_supports_rule_ids_that_contain_dots(self):
        self._write_report(
            [self._finding("android.mutable-pending-intent")]
        )

        result = double_check_report(
            str(self.report),
            config=self._config(
                rule_ids=frozenset({"android.mutable-pending-intent"})
            ),
            caller=lambda _prompt, _config: (
                '{"vulnerable": true, "reason": "confirmed"}'
            ),
        )

        self.assertEqual(1, result["llm_double_check"]["counts"]["confirmed"])
        self.assertEqual(0, result["llm_double_check"]["counts"]["skipped"])

    def test_relative_source_path_resolves_from_report_directory(self):
        self._write_report([self._finding("relative", self.source.name)])
        excerpts = []

        def fake_llm(prompt, _config):
            excerpts.append(prompt)
            return '{"vulnerable": true, "reason": "confirmed"}'

        double_check_report(
            str(self.report), config=self._config(), caller=fake_llm
        )

        self.assertIn("60: line 60", excerpts[0])
        self.assertNotIn("1: line 1", excerpts[0])

    def test_enabled_mode_requires_credentials(self):
        self._write_report([])
        with self.assertRaisesRegex(ValueError, "api_base_url and api_key"):
            double_check_report(
                str(self.report), config=DoubleCheckConfig(enabled=True)
            )

    def test_refuses_to_overwrite_original_report(self):
        original = self._write_report([])
        with self.assertRaisesRegex(ValueError, "must not overwrite"):
            double_check_report(
                str(self.report),
                str(self.report),
                config=self._config(),
            )
        self.assertEqual(original, json.loads(self.report.read_text()))


class VerdictParsingTests(unittest.TestCase):
    def test_parses_json_code_fence(self):
        self.assertEqual(
            ("confirmed", "evidence"),
            parse_verdict(
                '```json\n{"vulnerable": true, "reason": "evidence"}\n```'
            ),
        )

    def test_parses_legacy_format(self):
        status, reason = parse_verdict("Analysis\nVul: False")
        self.assertEqual("false_positive", status)
        self.assertIn("Vul: False", reason)

    def test_unrecognized_response_is_unknown(self):
        self.assertEqual(
            ("unknown", "LLM response did not contain a supported verdict"),
            parse_verdict("maybe"),
        )


if __name__ == "__main__":
    unittest.main()
