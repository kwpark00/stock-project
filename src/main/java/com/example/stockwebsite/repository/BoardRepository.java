package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 특정 주식(stockId)에 달린 게시글들을 작성일 기준 내림차순(최신순)으로 조회
    List<Board> findByStockIdOrderByCreatedDateDesc(Long stockId);
}