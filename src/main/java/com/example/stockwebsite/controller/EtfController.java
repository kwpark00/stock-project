package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.repository.EtfRepository;
import com.example.stockwebsite.service.EtfFavoriteService;
import com.example.stockwebsite.service.EtfPriceUpdateService;
import com.example.stockwebsite.service.NewsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/etf")
@RequiredArgsConstructor
public class EtfController {

    private final EtfRepository etfRepository;
    private final EtfPriceUpdateService etfPriceUpdateService;
    private final EtfFavoriteService etfFavoriteService;
    private final NewsService newsService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @GetMapping
    public String etfList(HttpSession session, Model model) {
        etfPriceUpdateService.updateIfNeeded();

        List<Etf> etfs = etfRepository.findAll();
        model.addAttribute("etfs", etfs);

        var last = etfPriceUpdateService.getLastUpdated();
        model.addAttribute("lastUpdated", last != null ? last.format(FMT) : "업데이트 중...");

        // 로그인한 경우 관심종목 여부 전달
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            List<Etf> favorites = etfFavoriteService.findMyFavorites(loginMember);
            model.addAttribute("favoriteIds",
                    favorites.stream().map(Etf::getId).toList());
        }

        return "etf/etfList";
    }

    @GetMapping("/{id}")
    public String etfDetail(@PathVariable Long id, HttpSession session, Model model) {
        Etf etf = etfRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ETF: " + id));

        List<Map<String, String>> news = newsService.fetchNews(etf.getTicker());
        model.addAttribute("etf", etf);
        model.addAttribute("newsList", news);

        // 관심종목 여부
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("isFavorite",
                    etfFavoriteService.isFavorite(loginMember, etf));
        }

        return "etf/etfDetail";
    }

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        boolean added = etfFavoriteService.toggle(loginMember, id);
        redirectAttributes.addFlashAttribute("message",
                added ? "관심종목에 추가됐어요." : "관심종목에서 제거됐어요.");

        return "redirect:/etf";
    }
}