package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Board;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.BoardRepository;
import com.example.stockwebsite.repository.MemberRepository;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;

    // 1. 특정 종목의 게시글 목록 가져오기
    public List<Board> getBoardsByStock(Long stockId) {
        return boardRepository.findByStockIdOrderByCreatedDateDesc(stockId);
    }

    // 2. 게시글 한 건 상세 조회
    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    // 3. 게시글 작성 (저장)
    @Transactional
    public Long writeBoard(Long stockId, Long memberId, String title, String content) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Board board = new Board();
        board.setStock(stock);
        board.setMember(member);
        board.setTitle(title);
        board.setContent(content);

        boardRepository.save(board);
        return board.getId();
    }
}