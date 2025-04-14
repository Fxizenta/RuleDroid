import asyncio
import aiohttp
from bs4 import BeautifulSoup
from urllib.parse import urljoin, urlparse, urlunparse
import os

visited_urls = set()
max_stack_size = 70000
max_retries = 3
concurrent_limit = 30

focus_keywords = [
    "insecure", "susceptible", "error", "null", "exception", "unavailable",
    "not thread safe", "illegal", "inappropriate", "deprecate", "better",
    "best to", "recommended", "less desirable", "instead of", "do not",
    "note that", "caution", "beware", "warning", "danger", "must", "have to",
    "be not", "never", "only"
]

proxy = "http://ip:port"

def save_page_content(url, content):
    parsed_url = urlparse(url)
    if parsed_url.path == "/":
        folder = "/savepath"
        file_name = "index.html"
    else:
        path_parts = parsed_url.path.strip("/").split("/")
        folder = os.path.join(folder, *path_parts[:-1]) if len(path_parts) > 1 else "/data/fxizenta/doc"
        file_name = path_parts[-1] or "index.html"
        if not file_name.endswith(".html"):
            file_name += ".html"
    file_name = file_name.replace(":", "").replace("?", "").replace("&", "").replace("=", "").replace("#", "_")
    os.makedirs(folder, exist_ok=True)
    file_path = os.path.join(folder, file_name)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Saved page content to: {file_path}")

async def crawl(url, base_url, session, task_stack, semaphore):
    retries = 0
    while retries < max_retries:
        try:
            async with semaphore:
                async with session.get(url, proxy=proxy, timeout=10) as response:
                    if response.status != 200:
                        return
                    content = await response.text()
                    soup = BeautifulSoup(content, 'html.parser')
                    page_text = soup.get_text()
                    contains_keyword = any(keyword in page_text for keyword in focus_keywords)
                    print(f"Visited: {url}, Contains keyword: {contains_keyword}")
                    if contains_keyword:
                        save_page_content(url, content)
                    for link in soup.find_all('a', href=True):
                        href = link['href']
                        full_url = urljoin(base_url, href)
                        parsed_url = urlparse(full_url)
                        cleaned_url = urlunparse(parsed_url._replace(fragment=''))
                        base_parsed = urlparse(base_url)
                        if (parsed_url.scheme == base_parsed.scheme and 
                            parsed_url.netloc == base_parsed.netloc and 
                            cleaned_url not in visited_urls):
                            visited_urls.add(cleaned_url)
                            print(f"Task stack size: {len(task_stack)}")
                            if len(task_stack) < max_stack_size:
                                task_stack.append((cleaned_url, base_url))
            return
        except Exception as e:
            retries += 1
            print(f"Error visiting {url}, retry {retries}/{max_retries}: {e}")
    print(f"Failed to visit {url} after {max_retries} retries.")

async def worker(task_stack, session, semaphore):
    while task_stack:
        url, base_url = task_stack.pop()
        await crawl(url, base_url, session, task_stack, semaphore)

async def main():
    task_stack = []
    # start_url = "https://developer.android.com/"
    start_url = "https://semgrep.dev/docs/writing-rules/overview"
    visited_urls.add(start_url)
    task_stack.append((start_url, start_url))
    semaphore = asyncio.Semaphore(concurrent_limit)
    async with aiohttp.ClientSession() as session:
        workers = [asyncio.create_task(worker(task_stack, session, semaphore)) for _ in range(30)]
        await asyncio.gather(*workers)

if __name__ == "__main__":
    asyncio.run(main())
