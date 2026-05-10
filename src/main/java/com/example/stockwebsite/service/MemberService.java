package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findById(Long id) {

        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("対象の会員が見つかりません。 ID: " + id));
    }

    public void update(Long id, String name, String email) {
        Member member = findById(id); // 먼저 기존 데이터를 가져온 뒤
        member.setName(name);         // 이름을 바꾸고
        member.setEmail(email);       // 이메일을 바꿉니다

        memberRepository.save(member); // 바뀐 내용을 다시 저장!
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * ログイン処理
     * @param loginId ユーザー入力ID
     * @param password ユーザー入力PW
     * @return ログイン成功時は会員エンティティ、失敗時はnull
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}