package com.example.stockwebsite.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. FetchType.LAZY 필수 추가 (성능 최적화 및 N+1 장애 방어)
    // 2. @JoinColumn으로 외래키(FK) 컬럼명 명시적 지정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    // 3. 편의 메서드 추가 (생성 시 가독성 및 안전성 확보)
    public static Favorite createFavorite(Member member, Stock stock) {
        Favorite favorite = new Favorite();
        favorite.setMember(member);
        favorite.setStock(stock);
        return favorite;
    }
}