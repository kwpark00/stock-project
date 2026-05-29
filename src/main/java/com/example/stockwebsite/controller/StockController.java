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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockPriceUpdateService stockPriceUpdateService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 종목 리스트 페이지 - 접속 시 자동으로 최신 주가 반영 (10분 캐시)
     */
    @GetMapping("/stocks")
    public String stockList(Model model) {
        // 10분이 지났으면 자동 업데이트 (백그라운드 아님 - 처음엔 시간이 걸릴 수 있음)
        stockPriceUpdateService.updateIfNeeded();

        List<Stock> stocks = stockService.findAll();
        model.addAttribute("stocks", stocks);

        // 마지막 업데이트 시각 전달
        LocalDateTime last = stockPriceUpdateService.getLastUpdated();
        model.addAttribute("lastUpdated", last != null ? last.format(FMT) : "아직 없음");

        return "stocks/stockList";
    }

    /**
     * 수동 새로고침 버튼 - 즉시 API 호출해서 강제 업데이트
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

