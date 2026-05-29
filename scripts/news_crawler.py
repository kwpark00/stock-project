import sys
import requests
from bs4 import BeautifulSoup
from datetime import datetime

def fetch_yahoo_japan_news(ticker: str):
    """
    Yahoo Japan ファイナンスから関連ニュースをクロールする
    出力形式: タイトル|||URL|||日付|||出典
    """
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                      "AppleWebKit/537.36 (KHTML, like Gecko) "
                      "Chrome/120.0.0.0 Safari/537.36",
        "Accept-Language": "ja-JP,ja;q=0.9"
    }

    # Google ニュース検索 (日本語)
    query = f"{ticker} ETF"
    url = f"https://news.google.com/search?q={query}&hl=ja&gl=JP&ceid=JP:ja"

    try:
        resp = requests.get(url, headers=headers, timeout=10)
        resp.encoding = "utf-8"
        soup = BeautifulSoup(resp.text, "html.parser")

        articles = soup.select("article")[:15]
        results = []

        for article in articles:
            try:
                title_el = article.select_one("h3 a, h4 a")
                if not title_el:
                    continue

                title = title_el.get_text(strip=True)
                href  = title_el.get("href", "")

                # Google News의 상대경로를 절대경로로 변환
                if href.startswith("./"):
                    href = "https://news.google.com/" + href[2:]

                # 날짜
                time_el = article.select_one("time")
                date_str = time_el.get("datetime", "")[:10] if time_el else datetime.now().strftime("%Y-%m-%d")

                # 출처
                source_el = article.select_one("div[data-n-tid]") or article.select_one("a[data-n-tid]")
                source = source_el.get_text(strip=True) if source_el else "Google News"

                if title:
                    results.append(f"{title}|||{href}|||{date_str}|||{source}")

            except Exception:
                continue

        for line in results:
            print(line)

    except Exception as e:
        print(f"クロールエラー: {e}|||#|||{datetime.now().strftime('%Y-%m-%d')}|||エラー", file=sys.stderr)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python news_crawler.py <TICKER>", file=sys.stderr)
        sys.exit(1)

    ticker = sys.argv[1].upper()
    fetch_yahoo_japan_news(ticker)