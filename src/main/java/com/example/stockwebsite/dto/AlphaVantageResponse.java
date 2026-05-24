package com.example.stockwebsite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Alpha Vantage GLOBAL_QUOTE API 응답 매핑 DTO
 * 예시 응답:
 * {
 *   "Global Quote": {
 *     "01. symbol": "AAPL",
 *     "05. price": "189.3000",
 *     "08. previous close": "188.5000",
 *     ...
 *   }
 * }
 */
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

        // 현재가 (소수점 버리고 정수 반환)
        public Integer getCurrentPrice() {
            if (price == null || price.isBlank()) return null;
            return (int) Double.parseDouble(price);
        }

        // 전일 종가
        public Integer getPreviousClose() {
            if (previousClose == null || previousClose.isBlank()) return null;
            return (int) Double.parseDouble(previousClose);
        }
    }
}
