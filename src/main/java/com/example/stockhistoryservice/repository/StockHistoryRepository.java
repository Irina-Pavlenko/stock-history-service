package com.example.stockhistoryservice.repository;

import com.example.stockhistoryservice.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StockHistoryRepository extends JpaRepository<StockHistory, UUID> {
    // методы save(), findAll(), findById(), delete() и т.д.
    // уже унаследованы от родительских интерфейсов!

    //Найти все сохраненные данные пользователя по тикеру
    List<StockHistory> findByUserIdAndTicker(UUID userId,String ticker);

    //Проверить, существует ли уже запись для данного тикера, даты и пользователя
    boolean existsByTickerAndDateAndUserId(String  ticker, LocalDate date, UUID userId);
}
