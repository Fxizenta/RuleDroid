import os
import tiktoken

# Initialize the model and tokenizer
model = "gpt-4o"
encoding = tiktoken.encoding_for_model(model)
token_limit = 8190  # Maximum token limit per chunk
threshold = 7900    # Token threshold for triggering splitting

def count_tokens(text):
    """Count the number of tokens in a given text."""
    return len(encoding.encode(text))

def split_md_file(file_path, token_limit=8190):
    """
    Split a Markdown file so that each part contains fewer tokens than the specified limit.
    Rename the original file to a .backup file.

    Args:
        file_path (str): Path to the input Markdown file.
        token_limit (int): Maximum token limit per chunk.
    """
    # Read the Markdown file
    with open(file_path, "r", encoding="utf-8") as file:
        content = file.read()

    # Split the text by lines
    lines = content.splitlines()
    sections = []
    current_section = []

    # Split into sections using Markdown headers or dividers
    for line in lines:
        if line.strip().startswith(("#", "##", "###", "####", "#####", "######")) or line.strip() in {"---", "***", "___"}:
            if current_section:
                sections.append(current_section)
                current_section = []
        current_section.append(line)

    if current_section:
        sections.append(current_section)

    # Optimize chunking: aim for similar number of lines per chunk
    total_lines = sum(len(section) for section in sections)
    target_lines_per_chunk = total_lines // (len(sections) or 1)
    chunks = []
    current_chunk = []
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

    # Ensure token limits are respected; adjust oversized chunks
    final_chunks = []
    current_chunk = []
    current_tokens = 0

    for chunk in chunks:
        chunk_text = "\n".join(chunk)
        chunk_tokens = count_tokens(chunk_text)
        if current_tokens + chunk_tokens > token_limit and current_chunk:
            final_chunks.append("\n".join(current_chunk))
            current_chunk = chunk
            current_tokens = chunk_tokens
        else:
            current_chunk.extend(chunk)
            current_tokens += chunk_tokens

    if current_chunk:
        final_chunks.append("\n".join(current_chunk))

    # Get the directory of the input file
    output_dir = os.path.dirname(file_path)
    base_name = os.path.splitext(os.path.basename(file_path))[0]

    # Save the split files
    for i, chunk in enumerate(final_chunks, 1):
        output_file = os.path.join(output_dir, f"{base_name}_{i}.md")
        with open(output_file, "w", encoding="utf-8") as file:
            file.write(chunk)

    # Rename the original file to a .backup
    backup_file = f"{file_path}.backup"
    os.rename(file_path, backup_file)
    print(f"Original file has been renamed to: {backup_file}")
    print(f"Splitting completed. {len(final_chunks)} files generated in: {output_dir}")

def process_folder(folder_path):
    """
    Recursively traverse a folder and check token count of Markdown files.
    Split files if token count exceeds the threshold.

    Args:
        folder_path (str): Path to the folder.
    """
    for root, _, files in os.walk(folder_path):
        for file in files:
            if file.endswith(".md"):
                file_path = os.path.join(root, file)
                with open(file_path, "r", encoding="utf-8") as f:
                    content = f.read()
                    token_count = count_tokens(content)
                
                if token_count > threshold:
                    print(f"File {file_path} has {token_count} tokens. Splitting...")
                    split_md_file(file_path, token_limit)
                else:
                    print(f"File {file_path} has {token_count} tokens. No splitting needed.")

if __name__ == "__main__":
    folder_path = ""
    process_folder(folder_path)
