import sys
import feedparser
from datetime import datetime

def fetch_news(ticker: str):
    results = []

    # Google News RSS (일본어) - JS 렌더링 불필요, 안정적
    feeds = [
        f"https://news.google.com/rss/search?q={ticker}+ETF&hl=ja&gl=JP&ceid=JP:ja",
        f"https://news.google.com/rss/search?q={ticker}&hl=ja&gl=JP&ceid=JP:ja",
    ]

    for feed_url in feeds:
        try:
            feed = feedparser.parse(feed_url)
            for entry in feed.entries[:10]:
                title  = entry.get("title", "").strip()
                href   = entry.get("link", "").strip()
                source = entry.get("source", {}).get("title", "Google News")

                # 날짜 파싱
                published = entry.get("published_parsed")
                if published:
                    date_str = datetime(*published[:3]).strftime("%Y-%m-%d")
                else:
                    date_str = datetime.now().strftime("%Y-%m-%d")

                if title and href:
                    results.append(f"{title}|||{href}|||{date_str}|||{source}")

            if results:
                break  # 첫 번째 피드에서 결과 나오면 stop

        except Exception as e:
            continue

    for line in results:
        print(line)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(1)
    fetch_news(sys.argv[1].upper())