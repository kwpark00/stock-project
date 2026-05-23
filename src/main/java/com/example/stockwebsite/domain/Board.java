package com.example.stockwebsite.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 글 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 글 내용

    private LocalDateTime createdDate; // 작성일시

    // 작성자 (Member 테이블 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 대상 종목 (Stock 테이블 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    // DB에 인서트 되기 직전에 현재 시간을 자동으로 세팅
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
