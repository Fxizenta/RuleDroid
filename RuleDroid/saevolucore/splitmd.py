"""
Token-aware Markdown file splitting.

Splits ``.md`` files whose token count exceeds a threshold into smaller chunks
so they stay within LLM context-window limits.
"""

import os

import tiktoken

_model = "gpt-4o"
_encoding = tiktoken.encoding_for_model(_model)
TOKEN_LIMIT = 8190   # Maximum tokens per chunk
THRESHOLD = 7900      # Token count that triggers splitting


def count_tokens(text: str) -> int:
    """Return the number of tokens in *text* for the configured model."""
    return len(_encoding.encode(text))


def split_md_file(file_path: str, token_limit: int = TOKEN_LIMIT) -> None:
    """
    Split a Markdown file into chunks that each fit within *token_limit*.

    The original file is renamed to ``<name>.md.backup``.
    """
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    lines = content.splitlines()
    sections: list[list[str]] = []
    current_section: list[str] = []

    # Split on Markdown headers / horizontal rules
    for line in lines:
        is_header = line.strip().startswith(
            ("#", "##", "###", "####", "#####", "######")
        )
        is_divider = line.strip() in {"---", "***", "___"}
        if (is_header or is_divider) and current_section:
            sections.append(current_section)
            current_section = []
        current_section.append(line)

    if current_section:
        sections.append(current_section)

    # Balance chunks by line count
    total_lines = sum(len(s) for s in sections)
    target_lines_per_chunk = total_lines // (len(sections) or 1)
    chunks: list[list[str]] = []
    current_chunk: list[str] = []
    current_lines = 0

    for section in sections:
        section_lines = len(section)
        if current_lines + section_lines > target_lines_per_chunk and current_chunk:
            chunks.append(current_chunk)
            current_chunk = []
            current_lines = 0
        current_chunk.extend(section)
        current_lines += section_lines

    if current_chunk:
        chunks.append(current_chunk)

    # Respect token limits
    final_chunks: list[str] = []
    current_chunk_lines: list[str] = []
    current_tokens = 0

    for chunk in chunks:
        chunk_text = "\n".join(chunk)
        chunk_tokens = count_tokens(chunk_text)
        if current_tokens + chunk_tokens > token_limit and current_chunk_lines:
            final_chunks.append("\n".join(current_chunk_lines))
            current_chunk_lines = chunk
            current_tokens = chunk_tokens
        else:
            current_chunk_lines.extend(chunk)
            current_tokens += chunk_tokens

    if current_chunk_lines:
        final_chunks.append("\n".join(current_chunk_lines))

    output_dir = os.path.dirname(file_path)
    base_name = os.path.splitext(os.path.basename(file_path))[0]

    for i, chunk in enumerate(final_chunks, 1):
        output_file = os.path.join(output_dir, f"{base_name}_{i}.md")
        with open(output_file, "w", encoding="utf-8") as f:
            f.write(chunk)

    backup_file = f"{file_path}.backup"
    os.rename(file_path, backup_file)
    print(f"Original file has been renamed to: {backup_file}")
    print(f"Splitting completed. {len(final_chunks)} files generated in: {output_dir}")


def process_folder(folder_path: str) -> None:
    """
    Walk *folder_path* and split any ``.md`` file whose token count exceeds
    the global ``THRESHOLD``.
    """
    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith(".md"):
                file_path = os.path.join(root, file)
                with open(file_path, "r", encoding="utf-8") as f:
                    token_count = count_tokens(f.read())

                if token_count > THRESHOLD:
                    print(
                        f"File {file_path} has {token_count} tokens. Splitting..."
                    )
                    split_md_file(file_path, TOKEN_LIMIT)
                else:
                    print(
                        f"File {file_path} has {token_count} tokens. "
                        f"No splitting needed."
                    )


if __name__ == "__main__":
    folder_path = ""
    process_folder(folder_path)
