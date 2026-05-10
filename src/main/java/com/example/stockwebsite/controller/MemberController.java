package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会員管理コントローラー (画面表示用)
 */
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 会員登録画面の表示
     * GET /members/new
     */
    @GetMapping("/members/new")
    public String createMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/createMemberForm";
    }

    /**
     * 会員登録の実行
     * POST /members/new
     */
    @PostMapping("/members/new")
    public String createMember(Member member) {
        memberService.save(member);
        return "redirect:/members"; // 登録完了後、一覧画面へ
    }

    /**
     * 会員一覧の表示
     * GET /members
     */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "members/memberList";
    }

    @GetMapping("/members/{id}/edit")
    public String updateMemberForm(@PathVariable("id") Long id, Model model) {
        Member member = memberService.findById(id);
        model.addAttribute("member", member); // 수정할 데이터를 모델에 담아 전달
        return "members/updateMemberForm";
    }

    /**
     * 会員情報修正実行 (Update)
     */
    @PostMapping("/members/{id}/edit")
    public String updateMember(@PathVariable("id") Long id, @ModelAttribute("member") Member member) {

        memberService.update(id, member.getName(), member.getEmail());
        return "redirect:/members";
    }

    /**
     * 会員削除実行 (Delete)
     */
    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable("id") Long id) {
        memberService.delete(id);
        return "redirect:/members";
    }
}