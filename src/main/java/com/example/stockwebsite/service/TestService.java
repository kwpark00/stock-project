package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void test() {

        Member member = new Member();

        member.setLoginId("test123");
        member.setPassword("1234");
        member.setName("홍길동");
        member.setEmail("test@test.com");

        memberRepository.save(member);

        System.out.println("会員保存完了");
    }
}