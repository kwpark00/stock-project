package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.dto.AlphaVantageResponse;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Alpha Vantage API를 통해 실시간 주가를 가져와서 DB를 업데이트하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockPriceUpdateService {

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    private static final String API_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey={apikey}";

    // 마지막 업데이트 시각 (캐싱 용도 - 너무 자주 API 호출 방지)
    private LocalDateTime lastUpdated = null;

    // 최소 업데이트 간격: 10분
    private static final int MIN_UPDATE_INTERVAL_MINUTES = 10;

    /**
     * 홈페이지 접속 시 호출 - 마지막 업데이트로부터 10분 지났으면 자동 갱신
     */
    @Transactional
    public boolean updateIfNeeded() {
        if (lastUpdated == null ||
                LocalDateTime.now().isAfter(lastUpdated.plusMinutes(MIN_UPDATE_INTERVAL_MINUTES))) {
            updateAllStockPrices();
            return true; // 업데이트 실행됨
        }
        return false; // 캐시된 데이터 사용
    }

    /**
     * DB에 있는 모든 종목의 현재가를 Alpha Vantage에서 가져와 업데이트
     * (무료 플랜: 분당 5회 제한 → 종목 사이에 13초 딜레이)
     */
    @Transactional
    public void updateAllStockPrices() {
        List<Stock> stocks = stockRepository.findAll();

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            try {
                updateStockPrice(stock);
                // 무료 API 분당 5회 제한: 첫 번째 이후 13초 대기
                if (i < stocks.size() - 1) {
                    Thread.sleep(13000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("가격 업데이트 중 인터럽트 발생: {}", stock.getTicker());
            } catch (Exception e) {
                log.error("가격 업데이트 실패 - 티커: {}, 원인: {}", stock.getTicker(), e.getMessage());
            }
        }

        lastUpdated = LocalDateTime.now();
        log.info(">>> 전체 종목 가격 업데이트 완료: {}", lastUpdated);
    }

    /**
     * 단일 종목 가격 업데이트
     */
    @Transactional
    public void updateStockPrice(Stock stock) {
        AlphaVantageResponse response = restTemplate.getForObject(
                API_URL,
                AlphaVantageResponse.class,
                stock.getTicker(),
                apiKey
        );

        if (response == null || response.getGlobalQuote() == null) {
            log.warn("API 응답 없음 - 티커: {}", stock.getTicker());
            return;
        }

        AlphaVantageResponse.GlobalQuote quote = response.getGlobalQuote();

        if (quote.getCurrentPrice() == null) {
            log.warn("가격 정보 없음 - 티커: {} (무료 API 한도 초과 가능성)", stock.getTicker());
            return;
        }

        stock.setPrePrice(quote.getPreviousClose());
        stock.setCurrentPrice(quote.getCurrentPrice());

        log.info("가격 업데이트 성공 - {}: ${}  (전일: ${})",
                stock.getTicker(), stock.getCurrentPrice(), stock.getPrePrice());
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}