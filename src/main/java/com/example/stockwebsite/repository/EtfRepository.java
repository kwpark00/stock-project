package com.example.stockwebsite.repository;

import com.example.stockwebsite.domain.Etf;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtfRepository extends JpaRepository<Etf, Long> {
    Optional<Etf> findByTicker(String ticker);
}
