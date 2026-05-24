package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.service.StockPriceUpdateService;
import com.example.stockwebsite.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockPriceUpdateService stockPriceUpdateService;

    @GetMapping("/stocks")
    public String stockList(Model model) {
        List<Stock> stocks = stockService.findAll();
        model.addAttribute("stocks", stocks);
        return "stocks/stockList";
    }

    /**
     * "시세 새로고침" 버튼 클릭 시 Alpha Vantage에서 전체 종목 가격 업데이트
     */
    @PostMapping("/stocks/refresh")
    public String refreshPrices(RedirectAttributes redirectAttributes) {
        try {
            stockPriceUpdateService.updateAllStockPrices();
            redirectAttributes.addFlashAttribute("message", "시세가 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "시세 업데이트 실패: " + e.getMessage());
        }
        return "redirect:/stocks";
    }
}
