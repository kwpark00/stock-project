package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.repository.EtfRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfPriceUpdateService {

    private final EtfRepository etfRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        for (Etf etf : etfs) {
            try {
                updateSingleEtf(etf);
            } catch (Exception e) {
                log.error("ETF 가격 업데이트 실패: {}", etf.getTicker(), e);
            }
        }
        lastUpdated = LocalDateTime.now();
    }

    private void updateSingleEtf(Etf etf) throws Exception {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + etf.getTicker()
                + "?interval=1d&range=2d";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode meta = root.path("chart").path("result").get(0).path("meta");

        double currentPrice  = meta.path("regularMarketPrice").asDouble();
        double previousClose = meta.path("chartPreviousClose").asDouble();

        if (currentPrice > 0) {
            etf.setCurrentPrice(Math.round(currentPrice  * 100.0) / 100.0);
            etf.setPrePrice   (Math.round(previousClose  * 100.0) / 100.0);
            log.info("ETF 가격 업데이트: {} = ${}", etf.getTicker(), etf.getCurrentPrice());
        }
    }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
}