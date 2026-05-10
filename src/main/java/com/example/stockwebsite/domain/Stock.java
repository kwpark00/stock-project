package com.example.stockwebsite.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;

    private String name;

    private Integer currentPrice;

    private Integer prePrice;

    public Integer getChange() {
        // prePrice가 null이면 0으로 취급해서 NPE 방지
        if (this.prePrice == null) return 0;
        return currentPrice - prePrice;
    }

    public Double getChangeRate() {
        // prePrice가 null이거나 0이면 계산 안 하고 0.0 반환
        if (this.prePrice == null || this.prePrice == 0) return 0.0;
        return Math.round(((double) getChange() / prePrice * 100) * 100) / 100.0;
    }

}
