package com.example.stockhistoryservice.service;

import com.example.stockhistoryservice.entity.Ticker;
import com.example.stockhistoryservice.exception.TickerNotFoundException;
import com.example.stockhistoryservice.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TickerService { //Сервис для работы со справочником тикеров (акций)

    // Репозиторий для работы с таблицей tickers в базе данных
    // Это зависимость, которая внедряется через конструктор
    private final TickerRepository tickerRepository;

    // Проверяет, существует ли тикер с указанным символом в справочнике
    public boolean existsBySymbol(String symbol){
        // Делегируем проверку репозиторию
        return tickerRepository.existsBySymbol(symbol);
    }

    // Находит тикер по символу или выбрасывает исключение если не найден
    public Ticker findBySymbolOrThrow(String symbol){
        // 1. Пытаемся найти тикер в репозитории
        return tickerRepository.findBySymbol(symbol) // возвращает Optional<Ticker>
        // Optional — это контейнер, который может содержать значение или быть пустым
        // 2. Если тикер не найден (Optional пустой), выбрасываем исключение
                .orElseThrow(() -> new TickerNotFoundException("Тикер '" + symbol + "' не найден в справочнике"));
    }
    // 3. Если тикер найден, возвращаем его (Optional автоматически развернется)
}
