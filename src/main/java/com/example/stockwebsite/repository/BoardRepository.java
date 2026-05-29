package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Board;
import com.example.stockwebsite.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByStockIdOrderByCreatedDateDesc(Long stockId);

    // Stock 삭제 전 FK 정리용
    @Modifying
    @Query("DELETE FROM Board b WHERE b.stock = :stock")
    void deleteAllByStock(@Param("stock") Stock stock);
}