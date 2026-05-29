package com.example.stockwebsite.service;

import com.example.stockwebsite.domain.Etf;
import com.example.stockwebsite.repository.EtfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfInitService {

    private final EtfRepository etfRepository;
    private final EtfPriceUpdateService etfPriceUpdateService;

    private static final String[][] ETF_LIST = {
            {"SPY",  "SPDR S&P 500 ETF",         "S&P 500 지수 추종 ETF. 미국 대형주 500개 분산투자."},
            {"QQQ",  "Invesco QQQ Trust",          "나스닥 100 추종. 애플·MS·엔비디아 등 빅테크 집중."},
            {"VTI",  "Vanguard Total Stock Market","미국 전체 주식시장 추종. 초분산·저비용."},
            {"ARKK", "ARK Innovation ETF",         "파괴적 혁신 기업 집중 투자. 고위험·고수익."},
            {"GLD",  "SPDR Gold Shares",           "금 현물 가격 추종. 인플레이션 헤지용."},
            {"TLT",  "iShares 20+ Year Treasury",  "미국 장기국채 추종. 안전자산 성격."},
            {"SOXX", "iShares Semiconductor ETF",  "반도체 섹터 집중. 엔비디아·TSMC 등 포함."},
            {"XLE",  "Energy Select Sector SPDR",  "미국 에너지 섹터. 엑손모빌·쉐브론 등."},
            {"VWO",  "Vanguard FTSE Emerging Markets","신흥국 주식시장 추종. 중국·인도·브라질 등."},
            {"SCHD", "Schwab US Dividend Equity",  "미국 고배당주 ETF. 안정적 배당 수익 추구."},
    };

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        log.info(">>> [EtfInitService] ETF 초기화 시작");

        for (String[] row : ETF_LIST) {
            String ticker = row[0];
            if (etfRepository.findByTicker(ticker).isEmpty()) {
                Etf etf = new Etf();
                etf.setTicker(ticker);
                etf.setName(row[1]);
                etf.setDescription(row[2]);
                etfRepository.save(etf);
                log.info(">>> ETF 추가: {}", ticker);
            }
        }

        // 백그라운드에서 주가 업데이트
        Thread t = new Thread(() -> etfPriceUpdateService.updateAllEtfPrices());
        t.setDaemon(true);
        t.setName("etf-init-price-updater");
        t.start();

        log.info(">>> [EtfInitService] 초기화 완료");
    }
}