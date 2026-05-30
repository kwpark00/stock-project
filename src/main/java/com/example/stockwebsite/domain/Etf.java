package com.example.stockwebsite.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Etf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private String name;
    private String description;
    private Double currentPrice;
    private Double prePrice;

    public Double getChange() {
        if (currentPrice == null || prePrice == null) return 0.0;
        return Math.round((currentPrice - prePrice) * 100.0) / 100.0;
    }

    public Double getChangeRate() {
        if (prePrice == null || prePrice == 0.0) return 0.0;
        return Math.round((getChange() / prePrice * 100) * 100.0) / 100.0;
    }

    public String getChangeFormatted() {
        double change = getChange();
        return (change > 0 ? "+" : "") + change;
    }

    public String getChangeRateFormatted() {
        double rate = getChangeRate();
        return (rate > 0 ? "+" : "") + rate + "%";
    }
}