package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Favorite;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 特定の会員の関心銘柄リストだけを一括取得
    List<Favorite> findByMember(Member member);

    // すでにウィッシュリストに入っている銘柄かどうかを確認する用途（重複防止）
    Optional<Favorite> findByMemberAndStock(Member member, Stock stock);
}