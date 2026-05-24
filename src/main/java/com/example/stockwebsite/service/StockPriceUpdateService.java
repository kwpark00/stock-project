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

    /**
     * DB에 있는 모든 종목의 현재가를 Alpha Vantage에서 가져와 업데이트
     * (무료 플랜: 분당 5회 제한 → 종목 사이에 12초 딜레이 적용)
     */
    @Transactional
    public void updateAllStockPrices() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            try {
                updateStockPrice(stock);
                // 무료 API 분당 5회 제한 대응: 종목마다 12초 대기
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("가격 업데이트 중 인터럽트 발생: {}", stock.getTicker());
            } catch (Exception e) {
                // 한 종목 실패해도 나머지 계속 진행
                log.error("가격 업데이트 실패 - 티커: {}, 원인: {}", stock.getTicker(), e.getMessage());
            }
        }

        log.info(">>> 전체 종목 가격 업데이트 완료");
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
            log.warn("가격 정보 없음 - 티커: {} (무료 API 한도 초과 가능성 있음)", stock.getTicker());
            return;
        }

        // DB 업데이트 (JPA 변경 감지로 자동 저장)
        stock.setPrePrice(quote.getPreviousClose());
        stock.setCurrentPrice(quote.getCurrentPrice());

        log.info("가격 업데이트 성공 - {}: {}원 (전일: {}원)",
                stock.getTicker(), stock.getCurrentPrice(), stock.getPrePrice());
    }
}