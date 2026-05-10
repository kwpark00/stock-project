package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    /** ログイン画面を表示 */
    @GetMapping("/login")
    public String loginForm() {
        return "login/loginForm";
    }

    /** ログイン実行 */
    @PostMapping("/login")
    public String login(@RequestParam("loginId") String loginId,
                        @RequestParam("password") String password,
                        HttpServletRequest request) {

        Member loginMember = memberService.login(loginId, password);

        if (loginMember == null) {
            // 로그인 실패 시 다시 로그인 화면으로
            return "login/loginForm";
        }

        // 로그인 성공 시 세션에 회원 정보 저장 (기모찌!)
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        return "redirect:/"; // 메인 화면으로 이동
    }

    /** ログアウト */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 날리기
        }
        return "redirect:/";
    }
}