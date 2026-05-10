package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.StockRepository;
import yahoofinance.YahooFinance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // 티커를 넣으면 야후에서 데이터를 가져와 DB에 저장/업데이트
    public void updateStockFromYahoo(String ticker) {
        try {
            yahoofinance.Stock yahooStock = YahooFinance.get(ticker);

            BigDecimal price = yahooStock.getQuote().getPrice();
            BigDecimal change = yahooStock.getQuote().getChange();
            String name = yahooStock.getName();

            // DB에서 해당 티커를 찾거나 새로 생성
            Stock stock = stockRepository.findByTicker(ticker)
                    .orElse(new Stock());

            stock.setTicker(ticker);
            stock.setName(name);
            stock.setCurrentPrice(price.intValue());
            // 아까 만든 prePrice 계산을 위해 (현재가 - 변동분) 저장
            stock.setPrePrice(price.subtract(change).intValue());

            stockRepository.save(stock);
        } catch (IOException e) {
            System.err.println("야후 API 호출 에러: " + ticker);
        }
    }

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }
}