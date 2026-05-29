package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.BoardRepository;
import com.example.stockwebsite.repository.FavoriteRepository;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInitService {

    private final StockRepository stockRepository;
    private final FavoriteRepository favoriteRepository;
    private final BoardRepository boardRepository;
    private final StockPriceUpdateService stockPriceUpdateService;

    private static final String[][] US_STOCKS = {
            {"AAPL",  "Apple Inc."},
            {"MSFT",  "Microsoft Corp."},
            {"GOOGL", "Alphabet Inc."},
            {"AMZN",  "Amazon.com Inc."},
            {"NVDA",  "NVIDIA Corp."},
            {"META",  "Meta Platforms Inc."},
            {"TSLA",  "Tesla Inc."},
            {"BRK.B", "Berkshire Hathaway"},
            {"JPM",   "JPMorgan Chase & Co."},
            {"V",     "Visa Inc."},
    };

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        log.info(">>> [StockInitService] 종목 초기화 시작");

        Set<String> validTickers = Arrays.stream(US_STOCKS)
                .map(row -> row[0].toUpperCase())
                .collect(Collectors.toSet());

        // 1. 더미 종목 삭제: Favorite → Board → Stock 순서로
        List<Stock> existing = stockRepository.findAll();
        for (Stock s : existing) {
            if (!validTickers.contains(s.getTicker().toUpperCase())) {
                log.info(">>> 더미 종목 삭제: {} ({})", s.getTicker(), s.getName());
                favoriteRepository.deleteAllByStock(s);
                favoriteRepository.flush();
                boardRepository.deleteAllByStock(s);
                boardRepository.flush();
                stockRepository.delete(s);
            }
        }
        stockRepository.flush();

        // 2. 미국 주요 종목 없으면 추가
        for (String[] row : US_STOCKS) {
            String ticker = row[0];
            String name   = row[1];
            if (stockRepository.findByTicker(ticker).isEmpty()) {
                Stock stock = new Stock();
                stock.setTicker(ticker);
                stock.setName(name);
                stockRepository.save(stock);
                log.info(">>> 종목 추가: {}", ticker);
            }
        }
        stockRepository.flush();

        // 3. 주가 업데이트 (백그라운드 스레드 - 앱 기동 블로킹 안 함)
        Thread priceThread = new Thread(() -> stockPriceUpdateService.updateAllStockPrices());
        priceThread.setDaemon(true);
        priceThread.setName("stock-init-price-updater");
        priceThread.start();

        log.info(">>> [StockInitService] 초기화 완료");
    }
}
