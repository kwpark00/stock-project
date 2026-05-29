package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.dto.AlphaVantageResponse;
import com.example.stockwebsite.repository.EtfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfPriceUpdateService {

    private final EtfRepository etfRepository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    private static final String API_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey={apikey}";

    private LocalDateTime lastUpdated = null;
    private static final int MIN_UPDATE_INTERVAL_MINUTES = 10;

    @Transactional
    public boolean updateIfNeeded() {
        if (lastUpdated == null ||
                LocalDateTime.now().isAfter(lastUpdated.plusMinutes(MIN_UPDATE_INTERVAL_MINUTES))) {
            updateAllEtfPrices();
            return true;
        }
        return false;
    }

    @Transactional
    public void updateAllEtfPrices() {
        List<Etf> etfs = etfRepository.findAll();
        for (int i = 0; i < etfs.size(); i++) {
            Etf etf = etfs.get(i);
            try {
                AlphaVantageResponse response = restTemplate.getForObject(
                        API_URL, AlphaVantageResponse.class, etf.getTicker(), apiKey);

                if (response != null && response.getGlobalQuote() != null) {
                    AlphaVantageResponse.GlobalQuote q = response.getGlobalQuote();
                    if (q.getCurrentPrice() != null) {
                        etf.setCurrentPrice(q.getCurrentPrice());
                        etf.setPrePrice(q.getPreviousClose());
                        log.info("ETF 가격 업데이트: {} = ${}", etf.getTicker(), etf.getCurrentPrice());
                    }
                }
                if (i < etfs.size() - 1) Thread.sleep(13000);
            } catch (Exception e) {
                log.error("ETF 가격 업데이트 실패: {}", etf.getTicker(), e);
            }
        }
        lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
}