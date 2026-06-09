"""
Convert Android developer documentation HTML files to clean Markdown.

Extracts the main content area, removes navigation / footer / header / scripts,
and normalises the output for downstream LLM processing.
"""

import os
import re
from concurrent.futures import ThreadPoolExecutor, as_completed

from bs4 import BeautifulSoup
from markdownify import markdownify as md


def clean_markdown_content(markdown_text: str) -> str:
    """Remove boilerplate fragments from the converted Markdown."""
    markdown_text = re.sub(
        r' \\\[\\\[\\\[\"Easy to understand\".*Last updated .*',
        "",
        markdown_text,
        flags=re.DOTALL,
    )
    markdown_text = re.sub(
        r"Last updated .* UTC", "", markdown_text, flags=re.DOTALL
    )
    markdown_text = re.sub(
        r'\\\[\\\[\\\[.*\\\[\]\,\\\[\]\]', "", markdown_text, flags=re.DOTALL
    )
    markdown_text = re.sub(r"\n\s*\n", "\n\n", markdown_text, flags=re.DOTALL)
    markdown_text = re.sub(
        r"\* \[.*\]\(https://developer\.android\.com.*\)\n",
        "",
        markdown_text,
        flags=re.DOTALL,
    )
    return markdown_text


def extract_main_content(html_file_path: str) -> str:
    """Parse an HTML file and convert its main content to Markdown."""
    with open(html_file_path, "r", encoding="utf-8") as f:
        soup = BeautifulSoup(f, "html.parser")
        for element in soup(["nav", "footer", "aside", "header", "script", "style"]):
            element.extract()
        main_content = soup.find("main") or soup.find("article") or soup.body
        markdown_content = md(str(main_content))
    return clean_markdown_content(markdown_content)


def save_markdown(markdown_text: str, md_file_path: str) -> None:
    """Write *markdown_text* to *md_file_path*, creating directories as needed."""
    os.makedirs(os.path.dirname(md_file_path), exist_ok=True)
    with open(md_file_path, "w", encoding="utf-8") as f:
        f.write(markdown_text)


def _process_single_file(
    html_file_path: str, input_dir: str, output_dir: str
) -> None:
    """Convert a single HTML file to Markdown and save it."""
    relative_path = os.path.relpath(html_file_path, input_dir)
    md_file_path = os.path.join(
        output_dir, os.path.splitext(relative_path)[0] + ".md"
    )
    markdown_text = extract_main_content(html_file_path)
    save_markdown(markdown_text, md_file_path)
    print(f"Processed: {html_file_path} -> {md_file_path}")


def process_html_files(
    input_dir: str, output_dir: str, max_workers: int = 4
) -> None:
    """Recursively convert all HTML files under *input_dir* to Markdown."""
    html_files = [
        os.path.join(root, file)
        for root, _, files in os.walk(input_dir)
        for file in files
        if file.endswith(".html")
    ]

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(_process_single_file, hf, input_dir, output_dir)
            for hf in html_files
        ]
        for i, future in enumerate(as_completed(futures), 1):
            try:
                future.result()
                print(f"Completed {i}/{len(html_files)} files")
            except Exception as e:
                print(f"Error processing file: {e}")

    print(f"Finished processing {len(html_files)} HTML files.")


if __name__ == "__main__":
    input_directory = "path/to/input"
    output_directory = "path/to/output"
    process_html_files(input_directory, output_directory, max_workers=4)
