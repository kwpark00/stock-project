package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = "loginMember", required = false) Member loginMember,
            Model model) {

        // 세션에 로그인 정보가 없으면 일반 홈화면으로
        if (loginMember == null) {
            return "home";
        }

        // 세션에 정보가 있으면 로그인 완료된 전용 홈화면으로!
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}