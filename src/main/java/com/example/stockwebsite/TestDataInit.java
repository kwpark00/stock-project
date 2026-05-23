package com.example.stockwebsite;

import com.example.stockwebsite.domain.Stock;
import com.example.stockwebsite.repository.FavoriteRepository;
import com.example.stockwebsite.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final StockRepository stockRepository;
    private final FavoriteRepository favoriteRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        // 1. 자식 테이블(관심종목)부터 비우고
        favoriteRepository.deleteAll();

        // 2. 부모 테이블(주식) 비우기
        stockRepository.deleteAll();

        // 3. 데이터 4개 세팅
        saveStock("7203", "トヨタ自動車", 2500, 2470);
        saveStock("9984", "ソフトバンクG", 6800, 6850);
        saveStock("AAPL", "Apple Inc", 27000, 26000);
        saveStock("TSLA", "Tesla Inc", 35000, 36500);

        System.out.println(">>> 초기 데이터 세팅 완료!");
    }

    // 4. 누락되었던 핵심 저장 로직 복구
    private void saveStock(String ticker, String name, int currentPrice, int param4) {
        Stock stock = new Stock();
        stock.setTicker(ticker);
        stock.setName(name);
        stock.setCurrentPrice(currentPrice);

        // ※ 이전에 'prevClosePrice' 필드가 없다고 하셨으니 해당 부분은 뺐습니다.
        // 다른 세팅할 필드가 있다면 여기에 추가하시면 됩니다.

        // 이 한 줄이 누락되어 DB에 인서트가 안 되고 화면이 비었던 겁니다.
        stockRepository.save(stock);
    }
}