import os

# 可修改的参数
TEXT_FILE_EXTENSIONS = (
    ".java", ".py", ".js", ".ts", ".txt", ".md", ".html", ".css", ".json", ".xml", ".yml", ".yaml"
)
EXCLUDED_DIRS = {".git", "node_modules", "__pycache__"}

def is_text_file(filename):
    return filename.endswith(TEXT_FILE_EXTENSIONS)

def convert_to_crlf(root_dir):
    for root, dirs, files in os.walk(root_dir):
        # 忽略特定目录
        dirs[:] = [d for d in dirs if d not in EXCLUDED_DIRS]
        for file in files:
            if is_text_file(file):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, "rb") as f:
                        content = f.read()
                    # 统一为 LF，然后转为 CRLF
                    content_lf = content.replace(b"\r\n", b"\n").replace(b"\r", b"\n")
                    content_crlf = content_lf.replace(b"\n", b"\r\n")
                    with open(file_path, "wb") as f:
                        f.write(content_crlf)
                    print(f"✅ Converted: {file_path}")
                except Exception as e:
                    print(f"❌ Failed to convert {file_path}: {e}")

if __name__ == "__main__":
    convert_to_crlf(".")
