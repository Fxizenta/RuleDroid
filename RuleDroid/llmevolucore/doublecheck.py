"""Optional LLM post-processing for Semgrep JSON vulnerability reports."""

import argparse
import copy
import json
import os
import re
import tempfile
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from pathlib import Path
from typing import Callable, Dict, FrozenSet, List, Optional, Tuple

import requests


DEFAULT_RULE_IDS: FrozenSet[str] = frozenset(
    """
ANDROID-CONTENT-PROVIDER-PERMISSIONS
ANDROID-CONTENT-PROVIDER-SIGNATURE-PERMISSION
ANDROID-GRANT-URI-PERMISSIONS
MASVS-NETWORK-TLS-ENFORCEMENT
MSTG-NETWORK-3
MSTG-PLATFORM-4.2
abuse_untrusted_file_uri_contentresolver
accept_self_signed_certificate
aes_hardcoded_key
android-action-new-picture-video
android-activitymanager-api
android-applicationexitinfo-api
android-backup-encryption-check
android-certificate-pinning
android-check-uri-permissions
android-choreographer-api
android-cleartext-manifest
android-cleartext-traffic-permitted
android-cleartext-traffic-permitted-attribute
android-cleartext-traffic-permitted-autofix
android-cleartext-traffic-permitted-base-config
android-cleartext-traffic-permitted-domain
android-cleartext-traffic-permitted-generic
android-cleartext-traffic-permitted-legacy
android-cleartext-traffic-permitted-manifest
android-collect-address
android-content-provider-permission-enforcement
android-contentprovider-filename-trust
android-contentresolver-untrusted-uri
android-create-device-protected-storage-context-java
android-deep-link-authentication-check
android-deep-link-authorization-check
android-deep-link-parameter-validation
android-direct-boot-aware-components
android-disable-adb-backup
android-exported-activity-missing-permission
android-exported-activity-no-permission
android-exported-provider-no-permission
android-exported-receiver-no-permission
android-exported-service-no-permission
android-external-storage-input-validation
android-external-storage-integrity-check
android-external-storage-password-file
android-external-storage-scoped-storage
android-fingerprint-error-codes
android-fragment-injection-basic
android-grant-uri-permission
android-grant-uri-permissions-manifest
android-implicit-broadcast-receiver-limitation
android-insecure-trustmanager-class-empty-checkserver
android-intent-filter-missing-permission
android-intent-sanitization
android-internal-storage-deprecated-modes
android-is-debugger-connected
android-keystore-biometric-enrollment
android-keystore-key-pair-generation
android-keystore-keygenparameterspec-builder-config
android-keystore-strongbox-algorithms
android-manifest-activity-permission
android-manifest-exported-provider
android-manifest-min-sdk-version
android-network-security-config-limited-cas
android-network-security-config-location
android-network-state-changed-action
android-null-sslsocketfactory
android-send-ordered-broadcast-missing-permission
android-service-enforce-calling-permission
android-sharedpreferences-insecure-mode
android-sms-broadcast-intent
android-sms-sensitive-operations
android-sql-injection
android-uri-permissions
android-uses-permission
android-validate-calling-activity
android-webview-untrusted-input
android.bindservice.implicit-intent
android.mutable-pending-intent
android_certificate_pinning_unsafe
android_cleartext_traffic_opt_out
android_detect_allowTaskReparenting
android_detect_taskAffinity
android_logging_sensitive_data
android_ndef_message_validation
android_providerinstaller_sync_missing_try_catch
android_root_detection_custom_builds
android_root_detection_installed_packages
android_strandhogg_allow_task_reparenting_enabled
android_use_tls_traffic
avoid-android-debuggable-production
avoid-camera-permission
avoid-direct-camera-api-import
avoid-dynamic-code-loading
avoid-securerandom-seed
cleartext-communication-android
content-provider-security
contentresolver-file-uri-abuse
custom-trust-anchors
debug_mode_flag
debuggable_apps
debugging_symbols_removal
detect-access-fine-location
detect-custom-permissions
detect-debug-build
detect-debuggable-manifest
detect-file-integrity-checks
detect-got-hooks
detect-hmac-integrity-check
detect-inline-hooks
detect-java-runtime-tampering_1
detect-ptrace-deny-attach
detect-ptrace-usage
enforce-custom-permissions-on-components
exclude-api-keys-from-source-control
external_storage_access
fingerprint-enrolled-check
fingerprint-hardware-detection
fingerprint-permission-check
fingerprintmanager-authenticate-cryptoobject
full-occlusion-mitigation-xml
google_play_integrity_api_device_integrity_check
hardcoded-api-key-java
hardcoded-api-key-xml
hardcoded-cryptographic-secrets
hardcoded-encryption-key_1
improper-contentprovider-filename-variable
improper-fileprovider-root-path
improper_hostname_verification_with_ssl_socket
improperly-exposed-directories-to-fileprovider
insecure-broadcast-receiver-dynamic
insecure-broadcast-receiver-manifest
insecure-broadcast-receiver-programmatic
insecure-broadcast-receiver-unregister
insecure-createPackageContext
insecure-file-storage
insecure-key-storage-core-data
insecure-key-storage-property-files
insecure-key-storage-sharedpreferences_1
insecure-network-security-config
insecure-x509trustmanager
insecure_or_deprecated_crypto
insecure_random_usage
intent-redirection-without-validation
intent-untrusted-html
log-info-disclosure-format-args
pbe-ciphers-missing-IV
pbkdf2-algorithm-deprecated
pbkdf2-iteration-count
pbkdf2-key-derivation
predictable-key-derivation
predictable-key-derivation_1
public-key-storage
remove_javascript_interface
replaying-pending-intents
risk-bluetooth-incorrect-discoverability-time
salt-storage-backup
secure-salt-generation
shared-key-across-accounts
trust-additional-cas
unsafe-hostname-verifier
unsafe_deserialization_objectinputstream
unsafe_deserialization_parcelable_arraylist_extra
unsafe_deserialization_parcelable_extra
unsafe_hostname_verifier
unsafe_jackson_deserialization
unsafe_object_deserialization
unwanted_object_deserialization
unzip-destination-dir-check-java
use-content-uri
use-environment-specific-keys
use-fileprovider
use-securerandom-getinstancestrong
use_tls_traffic_java
use_tls_traffic_xml
uses_cleartext_traffic
weak-cryptographic-signature
weak_or_broken_crypto_encryption_functions_xml
webview-allow-universal-access-from-file-urls
webview-html-untrusted-input
webview-javascript-interface
webview-set-webcontents-debugging-enabled
webview_debugging_1
xml-external-entity
xml_decoder_xxe
xmlinputfactory_xxe
""".split()
)


@dataclass(frozen=True)
class DoubleCheckConfig:
    """Configuration for the opt-in report post-processor."""

    enabled: bool = False
    api_base_url: str = ""
    api_key: str = ""
    model: str = ""
    max_workers: int = 4
    retries: int = 3
    timeout: int = 300
    context_lines: int = 40
    max_source_chars: int = 20_000
    rule_ids: Optional[FrozenSet[str]] = DEFAULT_RULE_IDS


LLMCaller = Callable[[str, DoubleCheckConfig], str]


def _eligible(finding: Dict, rule_ids: Optional[FrozenSet[str]]) -> bool:
    if rule_ids is None:
        return True
    check_id = str(finding.get("check_id", ""))
    parts = check_id.split(".")
    return any(".".join(parts[index:]) in rule_ids for index in range(len(parts)))


def _source_path(raw_path: str, report_path: Path) -> Optional[Path]:
    if not raw_path:
        return None
    path = Path(raw_path).expanduser()
    candidates = [path] if path.is_absolute() else [
        report_path.parent / path,
        Path.cwd() / path,
    ]
    return next((candidate for candidate in candidates if candidate.is_file()), None)


def _source_excerpt(finding: Dict, report_path: Path, config: DoubleCheckConfig) -> str:
    path = _source_path(str(finding.get("path", "")), report_path)
    if path is None:
        raise FileNotFoundError("finding source file is missing")

    lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    line = finding.get("start", {}).get("line", 1)
    try:
        line_index = max(int(line) - 1, 0)
    except (TypeError, ValueError):
        line_index = 0
    start = max(line_index - config.context_lines, 0)
    end = min(line_index + config.context_lines + 1, len(lines))
    numbered = "\n".join(
        "{:>6}: {}".format(number + 1, lines[number])
        for number in range(start, end)
    )
    if len(numbered) > config.max_source_chars:
        numbered = numbered[: config.max_source_chars] + "\n...[truncated]"
    return numbered


def _prompt(finding: Dict, source: str) -> str:
    finding_json = json.dumps(finding, ensure_ascii=False, sort_keys=True)
    return (
        "Act as a conservative Android security reviewer. Decide whether the "
        "static-analysis finding is a real exploitable vulnerability in the "
        "provided context. Return JSON only in this exact shape: "
        '{{"vulnerable": true, "reason": "brief evidence-based reason"}}. '
        "Use false only when the code clearly disproves the finding.\n\n"
        "Finding:\n{}\n\nRelevant source excerpt:\n```\n{}\n```"
    ).format(finding_json, source)


def _call_llm(prompt: str, config: DoubleCheckConfig) -> str:
    payload = {
        "stream": False,
        "detail": False,
        "messages": [{"role": "user", "content": prompt}],
    }
    if config.model:
        payload["model"] = config.model
    headers = {
        "Authorization": "Bearer {}".format(config.api_key),
        "Content-Type": "application/json",
    }
    last_error = None
    for attempt in range(config.retries):
        try:
            response = requests.post(
                config.api_base_url,
                json=payload,
                headers=headers,
                timeout=config.timeout,
            )
            response.raise_for_status()
            content = response.json()["choices"][0]["message"]["content"]
            if not isinstance(content, str):
                raise ValueError("LLM response content is not text")
            return content
        except (requests.RequestException, KeyError, TypeError, ValueError) as error:
            last_error = error
            if attempt + 1 < config.retries:
                time.sleep(min(2 ** attempt, 4))
    raise RuntimeError("LLM request failed: {}".format(last_error))


def parse_verdict(response: str) -> Tuple[str, str]:
    """Parse JSON responses and the legacy ``Vul: True/False`` format."""
    candidate = response.strip()
    fenced = re.search(r"```(?:json)?\s*(\{.*?\})\s*```", candidate, re.DOTALL)
    if fenced:
        candidate = fenced.group(1)
    try:
        payload = json.loads(candidate)
        value = payload.get("vulnerable")
        if isinstance(value, bool):
            return (
                "confirmed" if value else "false_positive",
                str(payload.get("reason", "")).strip(),
            )
    except (json.JSONDecodeError, AttributeError):
        pass

    match = re.search(r"\bVul(?:nerable)?\s*:\s*(True|False)\b", response, re.I)
    if match:
        status = "confirmed" if match.group(1).lower() == "true" else "false_positive"
        return status, response.strip()
    return "unknown", "LLM response did not contain a supported verdict"


def _review(
    index: int,
    finding: Dict,
    report_path: Path,
    config: DoubleCheckConfig,
    caller: LLMCaller,
) -> Tuple[int, str, Dict]:
    try:
        source = _source_excerpt(finding, report_path, config)
        response = caller(_prompt(finding, source), config)
        status, reason = parse_verdict(response)
        review = {"status": status, "reason": reason, "raw_response": response}
    except Exception as error:  # one failure must never discard another finding
        status = "unknown"
        review = {"status": status, "reason": str(error), "raw_response": ""}
    return index, status, review


def double_check_report(
    report_path: str,
    output_path: Optional[str] = None,
    config: Optional[DoubleCheckConfig] = None,
    caller: Optional[LLMCaller] = None,
) -> Dict:
    """Return and optionally write an LLM-filtered copy of a Semgrep report."""
    config = config or DoubleCheckConfig()
    source_path = Path(report_path).resolve()
    with source_path.open("r", encoding="utf-8") as report_file:
        report = json.load(report_file)
    if not isinstance(report, dict) or not isinstance(report.get("results"), list):
        raise ValueError("report must contain a top-level 'results' array")
    if not all(isinstance(finding, dict) for finding in report["results"]):
        raise ValueError("every item in 'results' must be an object")

    result = copy.deepcopy(report)
    if not config.enabled:
        return result
    if output_path and Path(output_path).expanduser().resolve() == source_path:
        raise ValueError("output path must not overwrite the original report")
    if not config.api_base_url or not config.api_key:
        raise ValueError(
            "LLM double-check requires api_base_url and api_key when enabled"
        )
    if config.max_workers < 1:
        raise ValueError("max_workers must be at least 1")
    if config.retries < 1:
        raise ValueError("retries must be at least 1")
    if config.timeout < 1:
        raise ValueError("timeout must be at least 1")

    findings: List[Dict] = result["results"]
    candidates = [
        (index, finding)
        for index, finding in enumerate(findings)
        if _eligible(finding, config.rule_ids)
    ]
    reviews: Dict[int, Tuple[str, Dict]] = {}
    active_caller = caller or _call_llm
    with ThreadPoolExecutor(max_workers=config.max_workers) as executor:
        futures = [
            executor.submit(
                _review, index, finding, source_path, config, active_caller
            )
            for index, finding in candidates
        ]
        for future in as_completed(futures):
            index, status, review = future.result()
            reviews[index] = (status, review)

    kept: List[Dict] = []
    suppressed: List[Dict] = []
    counts = {"confirmed": 0, "false_positive": 0, "unknown": 0, "skipped": 0}
    for index, finding in enumerate(findings):
        review_result = reviews.get(index)
        if review_result is None:
            counts["skipped"] += 1
            kept.append(finding)
            continue
        status, review = review_result
        counts[status] += 1
        annotated = copy.deepcopy(finding)
        if not isinstance(annotated.get("extra"), dict):
            annotated["extra"] = {}
        extra = annotated["extra"]
        extra["llm_double_check"] = review
        if status == "false_positive":
            suppressed.append(annotated)
        else:
            kept.append(annotated)

    result["results"] = kept
    result["llm_double_check"] = {
        "enabled": True,
        "mode": "fail_open",
        "counts": counts,
        "suppressed_results": suppressed,
    }
    if output_path:
        _atomic_json_write(result, Path(output_path))
    return result


def _atomic_json_write(data: Dict, output_path: Path) -> None:
    output_path = output_path.expanduser().resolve()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    descriptor, temporary = tempfile.mkstemp(
        prefix="." + output_path.name + ".", suffix=".tmp", dir=str(output_path.parent)
    )
    try:
        with os.fdopen(descriptor, "w", encoding="utf-8") as output_file:
            json.dump(data, output_file, indent=2, ensure_ascii=False)
            output_file.write("\n")
        os.replace(temporary, str(output_path))
    except Exception:
        try:
            os.unlink(temporary)
        except FileNotFoundError:
            pass
        raise


def _load_rule_ids(path: str) -> FrozenSet[str]:
    values = []
    for line in Path(path).read_text(encoding="utf-8").splitlines():
        value = line.strip()
        if value and not value.startswith("#"):
            values.append(value)
    return frozenset(values)


def add_double_check_arguments(parser: argparse.ArgumentParser) -> None:
    """Add the shared opt-in LLM options to a command-line parser."""
    parser.add_argument(
        "--llm-double-check",
        action="store_true",
        help="enable LLM false-positive review (disabled by default)",
    )
    parser.add_argument(
        "--api-base-url", default=os.getenv("RULEDROID_LLM_API_BASE_URL", "")
    )
    parser.add_argument("--api-key", default=os.getenv("RULEDROID_LLM_API_KEY", ""))
    parser.add_argument("--model", default=os.getenv("RULEDROID_LLM_MODEL", ""))
    parser.add_argument("--max-workers", type=int, default=4)
    parser.add_argument("--timeout", type=int, default=300)
    parser.add_argument("--retries", type=int, default=3)
    rules = parser.add_mutually_exclusive_group()
    rules.add_argument("--all-rules", action="store_true")
    rules.add_argument("--rule-id-file", help="one eligible rule ID per line")


def config_from_args(args: argparse.Namespace) -> DoubleCheckConfig:
    """Build a double-check configuration from shared CLI arguments."""
    rule_ids = (
        None
        if args.all_rules
        else _load_rule_ids(args.rule_id_file)
        if args.rule_id_file
        else DEFAULT_RULE_IDS
    )
    return DoubleCheckConfig(
        enabled=args.llm_double_check,
        api_base_url=args.api_base_url,
        api_key=args.api_key,
        model=args.model,
        max_workers=args.max_workers,
        retries=args.retries,
        timeout=args.timeout,
        rule_ids=rule_ids,
    )


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Optionally remove LLM-confirmed false positives from a Semgrep report."
    )
    parser.add_argument("report", help="input Semgrep JSON report")
    parser.add_argument("-o", "--output", help="output sidecar JSON path")
    add_double_check_arguments(parser)
    return parser


def main(argv: Optional[List[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    if not args.llm_double_check:
        print("LLM double-check is disabled; the original report is unchanged.")
        return 0
    input_path = Path(args.report).resolve()
    output_path = (
        Path(args.output).resolve()
        if args.output
        else input_path.with_name(input_path.stem + ".llm-filtered.json")
    )
    config = config_from_args(args)
    result = double_check_report(
        str(input_path), str(output_path), config=config
    )
    counts = result["llm_double_check"]["counts"]
    print("LLM-filtered report written to {}".format(output_path))
    print(
        "confirmed={confirmed}, false_positive={false_positive}, "
        "unknown={unknown}, skipped={skipped}".format(**counts)
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
