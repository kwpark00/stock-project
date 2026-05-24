package com.example.stockwebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate: 외부 REST API 호출에 사용
     * Alpha Vantage API 호출 시 StockPriceUpdateService에서 주입받아 사용
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}