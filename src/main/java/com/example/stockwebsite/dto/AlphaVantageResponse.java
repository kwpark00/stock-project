package com.example.stockwebsite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlphaVantageResponse {

    @JsonProperty("Global Quote")
    private GlobalQuote globalQuote;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GlobalQuote {

        @JsonProperty("01. symbol")
        private String symbol;

        @JsonProperty("05. price")
        private String price;

        @JsonProperty("08. previous close")
        private String previousClose;

        public Double getCurrentPrice() {
            if (price == null || price.isBlank()) return null;
            return Math.round(Double.parseDouble(price) * 100.0) / 100.0;
        }

        public Double getPreviousClose() {
            if (previousClose == null || previousClose.isBlank()) return null;
            return Math.round(Double.parseDouble(previousClose) * 100.0) / 100.0;
        }
    }
}
