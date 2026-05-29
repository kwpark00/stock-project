package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.repository.EtfRepository;
import com.example.stockwebsite.service.EtfPriceUpdateService;
import com.example.stockwebsite.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/etf")
@RequiredArgsConstructor
public class EtfController {

    private final EtfRepository etfRepository;
    private final EtfPriceUpdateService etfPriceUpdateService;
    private final NewsService newsService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ETF 목록 페이지
    @GetMapping
    public String etfList(Model model) {
        etfPriceUpdateService.updateIfNeeded();

        List<Etf> etfs = etfRepository.findAll();
        model.addAttribute("etfs", etfs);

        var last = etfPriceUpdateService.getLastUpdated();
        model.addAttribute("lastUpdated", last != null ? last.format(FMT) : "업데이트 중...");

        return "etf/etfList";
    }

    // ETF 상세 + 뉴스 페이지
    @GetMapping("/{id}")
    public String etfDetail(@PathVariable Long id, Model model) {
        Etf etf = etfRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ETF: " + id));

        // 파이썬 크롤러로 일본어 기사 가져오기
        List<Map<String, String>> news = newsService.fetchNews(etf.getTicker());

        model.addAttribute("etf", etf);
        model.addAttribute("newsList", news);

        return "etf/etfDetail";
    }
}