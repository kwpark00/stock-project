package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.domain.Favorite;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.service.EtfFavoriteService;
import com.example.stockwebsite.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final EtfFavoriteService etfFavoriteService;

    @GetMapping
    public String favoriteList(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です。");
            return "redirect:/login";
        }

        // 기존 주식 관심종목
        List<Favorite> favorites = favoriteService.getFavoriteList(loginMember.getId());
        model.addAttribute("favorites", favorites);

        // ETF 관심종목 추가
        List<Etf> etfFavorites = etfFavoriteService.findMyFavorites(loginMember);
        model.addAttribute("etfFavorites", etfFavorites);

        return "favoriteList";
    }

    @PostMapping("/toggle/{stockId}")
    public String toggleFavorite(@PathVariable("stockId") Long stockId,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            String result = favoriteService.toggleFavorite(loginMember.getId(), stockId);
            if ("ADD_SUCCESS".equals(result)) {
                redirectAttributes.addFlashAttribute("message", "お気に入りに登録しました。");
            } else if ("REMOVE_SUCCESS".equals(result)) {
                redirectAttributes.addFlashAttribute("message", "お気に入りを解除しました。");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/stocks");
    }
}
