package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.domain.EtfFavorite;
import com.example.stockwebsite.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EtfFavoriteRepository extends JpaRepository<EtfFavorite, Long> {
    List<EtfFavorite> findByMember(Member member);
    Optional<EtfFavorite> findByMemberAndEtf(Member member, Etf etf);
}