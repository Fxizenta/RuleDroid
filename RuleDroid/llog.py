"""
Shared logging utility for the RuleDroid project.

Configures a root logger writing to ``LLMmakerule.log`` and provides a
convenience ``log_and_print`` that echoes to both stdout and the log.
"""

import logging

logging.basicConfig(
    filename="LLMmakerule.log",
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
)


def log_and_print(message: str) -> None:
    """Print *message* to stdout and also write it to the log."""
    print(message)
    logging.info(message)
