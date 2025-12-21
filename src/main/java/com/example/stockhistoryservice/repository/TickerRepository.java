package com.example.stockhistoryservice.repository;

import com.example.stockhistoryservice.entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {
    Optional<Ticker> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);
}
