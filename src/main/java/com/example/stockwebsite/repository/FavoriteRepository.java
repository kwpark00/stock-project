package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Favorite;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByMember(Member member);

    Optional<Favorite> findByMemberAndStock(Member member, Stock stock);

    // 특정 종목에 연결된 관심종목 레코드 전부 삭제 (Stock 삭제 전 FK 정리용)
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.stock = :stock")
    void deleteAllByStock(@Param("stock") Stock stock);
}