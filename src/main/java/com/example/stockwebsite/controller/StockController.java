package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks")
    public String stockList(Model model) {
        List<Stock> stocks = stockService.findAll();
        model.addAttribute("stocks", stocks);
        return "stocks/stockList";
    }
}
