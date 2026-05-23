package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Favorite;
import com.example.stockwebsite.domain.Member;
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

    /**
     * 1. [신규 추가] 관심종목 리스트 화면 띄우기 (GET 방식)
     */
    @GetMapping
    public String favoriteList(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 로그인 안 한 유저는 튕겨내기
        if (loginMember == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です。");
            return "redirect:/login";
        }

        // 서비스에서 현재 로그인한 유저의 관심종목 리스트를 가져와서 화면(Model)에 전달
        List<Favorite> favorites = favoriteService.getFavoriteList(loginMember.getId());
        model.addAttribute("favorites", favorites);

        return "favoriteList"; // favoriteList.html을 렌더링
    }

    /**
     * 2. [기존 로직 업그레이드] 관심종목 등록/해제 처리
     */
    @PostMapping("/toggle/{stockId}")
    public String toggleFavorite(@PathVariable("stockId") Long stockId,
                                 HttpSession session,
                                 HttpServletRequest request, // 이전 페이지 URL을 알아내기 위해 추가
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

        // UX 최적화: 무조건 /stocks로 가지 않고, 사용자가 클릭했던 이전 화면으로 똑똑하게 돌려보냄
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/stocks");
    }
}
