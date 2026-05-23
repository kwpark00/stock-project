package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Favorite;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.FavoriteRepository;
import com.example.stockwebsite.repository.MemberRepository;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 1. 조회 최적화 (SRE/성능 필수)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;

    /**
     * 관심종목 토글 (찜하기 / 찜취소)
     */
    @Transactional // 2. 쓰기 작업이 있으므로 별도로 붙여줌
    public String toggleFavorite(Long memberId, Long stockId) {
        // 방어 코드: 없는 회원이나 없는 주식이면 예외 터트리기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주식 종목입니다."));

        // 이미 관심종목에 등록했는지 조회
        Optional<Favorite> alreadyFavorite = favoriteRepository.findByMemberAndStock(member, stock);

        if (alreadyFavorite.isPresent()) {
            // 이미 존재하면 삭제 (찜 취소)
            favoriteRepository.delete(alreadyFavorite.get());
            return "REMOVE_SUCCESS";
        } else {
            // 없으면 새로 생성해서 저장 (찜 등록)
            Favorite favorite = Favorite.createFavorite(member, stock);
            favoriteRepository.save(favorite);
            return "ADD_SUCCESS";
        }
    }

    /**
     * 특정 회원의 관심종목 리스트 전체 조회
     */
    public List<Favorite> getFavoriteList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return favoriteRepository.findByMember(member);
    }
}