package com.example.stockwebsite;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final StockRepository stockRepository; // Repository 주입 추가

    @EventListener(ApplicationReadyEvent.class)
    @Transactional // 데이터 삭제 및 저장을 하나의 트랜잭션으로 묶어줍니다.
    public void initData() {
        // 1. 기존에 쌓인 데이터를 싹 밀어버린다 (중복 방지 멱등성 확보!)
        stockRepository.deleteAll();

        // 2. 그다음 깔끔하게 4개만 넣는다 (메서드 호출)
        saveStock("7203", "トヨタ自動車", 2500, 2470);
        saveStock("9984", "ソフトバンクG", 6800, 6850);
        saveStock("AAPL", "Apple Inc", 27000, 26000);
        saveStock("TSLA", "Tesla Inc", 35000, 36500);

        System.out.println(">>> 초기 데이터 초기화 및 재생성 완료!");
    }

    // 3. 데이터를 저장하기 위한 헬퍼 메서드 정의
    private void saveStock(String ticker, String name, int currentPrice, int prePrice) {
        Stock stock = new Stock();
        stock.setTicker(ticker);
        stock.setName(name);
        stock.setCurrentPrice(currentPrice);
        stock.setPrePrice(prePrice);
        stockRepository.save(stock);
    }
}