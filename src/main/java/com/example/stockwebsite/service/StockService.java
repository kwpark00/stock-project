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



    public List<Stock> findAll() {
        return stockRepository.findAll();
    }
}