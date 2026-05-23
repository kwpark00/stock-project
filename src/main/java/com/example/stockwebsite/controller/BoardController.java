package com.example.stockwebsite.controller;

import com.example.stockwebsite.domain.Board;
import com.example.stockwebsite.domain.Member;
import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.StockRepository;
import com.example.stockwebsite.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final StockRepository stockRepository; // 게시판 상단에 종목 이름 띄워주기 용도

    // 1. 특정 종목의 게시판 목록 화면
    @GetMapping("/stocks/{stockId}/boards")
    public String boardList(@PathVariable("stockId") Long stockId, Model model) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 종목입니다."));
        List<Board> boards = boardService.getBoardsByStock(stockId);

        model.addAttribute("stock", stock);
        model.addAttribute("boards", boards);

        return "board/boardList"; // HTML 파일 경로
    }

    // 2. 글쓰기 폼 화면 띄우기
    @GetMapping("/stocks/{stockId}/boards/new")
    public String createBoardForm(@PathVariable("stockId") Long stockId, HttpSession session, Model model, RedirectAttributes rttr) {
        // 로그인 안 한 유저는 튕겨내기
        if (session.getAttribute("loginMember") == null) {
            rttr.addFlashAttribute("errorMessage", "ログインが必要です。");
            return "redirect:/login";
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 종목입니다."));
        model.addAttribute("stock", stock);

        return "board/boardForm";
    }

    // 3. 실제 글 작성 처리
    @PostMapping("/stocks/{stockId}/boards/new")
    public String createBoard(@PathVariable("stockId") Long stockId,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content,
                              HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        boardService.writeBoard(stockId, loginMember.getId(), title, content);

        // 글 작성이 끝나면 해당 종목의 게시판 목록으로 다시 리다이렉트
        return "redirect:/stocks/" + stockId + "/boards";
    }

    // 4. 게시글 상세 보기 화면
    @GetMapping("/boards/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board/boardDetail";
    }

    @GetMapping("/board")
    public String boardRedirect() {
        // 단순히 /board로 들어오면 주식 목록 페이지로 튕겨내기
        return "redirect:/stocks";
    }
}
