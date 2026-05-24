package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다. ID: " + id));
    }

    public Stock findByTicker(String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티커입니다: " + ticker));
    }

    @Transactional
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }
}