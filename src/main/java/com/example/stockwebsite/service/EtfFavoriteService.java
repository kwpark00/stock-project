package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.domain.EtfFavorite;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.repository.EtfFavoriteRepository;
import com.example.stockwebsite.repository.EtfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EtfFavoriteService {

    private final EtfFavoriteRepository etfFavoriteRepository;
    private final EtfRepository etfRepository;

    // 관심종목 토글 (추가/제거)
    @Transactional
    public boolean toggle(Member member, Long etfId) {
        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(() -> new IllegalArgumentException("ETF 없음"));

        Optional<EtfFavorite> existing = etfFavoriteRepository.findByMemberAndEtf(member, etf);
        if (existing.isPresent()) {
            etfFavoriteRepository.delete(existing.get());
            return false; // 제거됨
        } else {
            EtfFavorite fav = new EtfFavorite();
            fav.setMember(member);
            fav.setEtf(etf);
            etfFavoriteRepository.save(fav);
            return true; // 추가됨
        }
    }

    // 내 관심종목 목록
    public List<Etf> findMyFavorites(Member member) {
        return etfFavoriteRepository.findByMember(member)
                .stream()
                .map(EtfFavorite::getEtf)
                .toList();
    }

    // 이미 관심종목인지 확인
    public boolean isFavorite(Member member, Etf etf) {
        return etfFavoriteRepository.findByMemberAndEtf(member, etf).isPresent();
    }
}