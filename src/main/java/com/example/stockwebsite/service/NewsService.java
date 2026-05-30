package com.example.stockwebsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class NewsService {

    /**
     * 파이썬 크롤러를 호출해서 해당 ETF 티커의 일본어 기사 목록 반환
     * 반환 형식: List<Map> 각 Map은 title, url, date, source 키를 가짐
     */
    public List<Map<String, String>> fetchNews(String ticker) {
        List<Map<String, String>> newsList = new ArrayList<>();
        try {
            // 파이썬 스크립트 경로 (프로젝트 루트 기준)
            String scriptPath = "scripts/news_crawler.py";

            String pythonCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";
            String projectRoot = System.getProperty("user.dir");
            String fullScriptPath = projectRoot + "/scripts/news_crawler.py";
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, fullScriptPath, ticker);
            pb.environment().put("PYTHONIOENCODING", "utf-8");  // 추가
            pb.environment().put("PYTHONUTF8", "1");             // 추가
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                // 파이썬에서 "제목|URL|날짜|출처" 형식으로 출력
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length == 4) {
                    Map<String, String> article = new LinkedHashMap<>();
                    article.put("title",  parts[0].trim());
                    article.put("url",    parts[1].trim());
                    article.put("date",   parts[2].trim());
                    article.put("source", parts[3].trim());
                    newsList.add(article);
                }
            }
            process.waitFor();

        } catch (Exception e) {
            log.error("뉴스 크롤링 실패 - ticker: {}, 원인: {}", ticker, e.getMessage());
        }
        return newsList;
    }
}
