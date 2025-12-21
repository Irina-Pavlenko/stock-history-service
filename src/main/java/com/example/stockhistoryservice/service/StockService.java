package com.example.stockhistoryservice.service;

import com.example.stockhistoryservice.dto.SaveStockRequest;
import com.example.stockhistoryservice.entity.StockHistory;
import com.example.stockhistoryservice.entity.Ticker;
import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.repository.StockHistoryRepository;
import com.example.stockhistoryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// -- Основной сервис приложения для работы с историческими данными акций

@Service
@RequiredArgsConstructor
public class StockService {

    // Репозиторий для работы с историческими данными акций
    // Используется для сохранения данных в таблицу stock_history
    private final StockHistoryRepository stockHistoryRepository;

    // Репозиторий для работы с пользователями
    // Используется для поиска пользователя по ID (из JWT токена)
    private final UserRepository userRepository;

    // Сервис для работы со справочником тикеров
    // Используется для проверки существования тикера перед сохранением данных
    private final TickerService tickerService;

    // Сохраняет исторические данные акций для указанного пользователя
    @Transactional
    public void saveStockData(UUID userId, SaveStockRequest request) {
        // === ШАГ 1: ВАЛИДАЦИЯ ВХОДНЫХ ДАННЫХ ===
        validateDates(request);

        // === ШАГ 2: ПРОВЕРКА СУЩЕСТВОВАНИЯ ТИКЕРА ===
        checkTickerExists(request.getTicker());

        // === ШАГ 3: ПОИСК ПОЛЬЗОВАТЕЛЯ ===
        User user = findUser(userId);

        // === ШАГ 4: ПОЛУЧЕНИЕ ДАННЫХ ИЗ ВНЕШНЕГО API ===
        // TODO: Интеграция с Polygon.io будет позже
        // Пока используем тестовые данные
        System.out.println("DEBUG: Запрос на сохранение данных:");
        System.out.println("  Пользователь: " + user.getEmail());
        System.out.println("  Тикер: " + request.getTicker());
        System.out.println("  Период: " + request.getStart() + " - " + request.getEnd());

        // === ШАГ 5: СОХРАНЕНИЕ ТЕСТОВЫХ ДАННЫХ ===
        // TODO: Позже заменим на реальные данные из Polygon.
        saveTestData(user, request.getTicker(), request.getStart(), request.getEnd());

    }
    // Проверяет корректность дат в запросе
    private void validateDates(SaveStockRequest request) {
        // isAfter() проверяет, является ли одна дата позже другой
        if (request.getStart().isAfter(request.getEnd())){
            throw new IllegalArgumentException(
                    "Ошибка валидации: дата начала (" + request.getStart() +
                            ") не может быть позже даты окончания (" +
                            request.getEnd() + ")"
            );
        } // Если даты корректны, метод просто завершается без исключения
    }
    // Проверяет, существует ли тикер в справочнике
    private void checkTickerExists(String tickerSymbol){
        // Используем TickerService для проверки существования тикера
        // Если тикер не существует, TickerService выбросит TickerNotFoundException
        Ticker ticker = tickerService.findBySymbolOrThrow(tickerSymbol);
        System.out.println("DEBUG: Тикер '" + tickerSymbol + "' найден: " + ticker.getName());
    }
    // Находит пользователя по ID
    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException(
                "Ошибка: пользователь ID " + userId + " не найден"
        ));
    }
    // Сохраняет тестовые данные в базу (временная реализация)
    // Этот метод будет заменен на получение реальных данных из Polygon.io.
    private void saveTestData(User user, String tickerSymbol,
                              LocalDate startDate, LocalDate endDate){
        System.out.println("DEBUG: Сохранение тестовых данных для " + tickerSymbol);
        // Проходим по всем дням в указанном периоде
        LocalDate currentDate = startDate;
        int savedCount = 0;

        while (!currentDate.isAfter(endDate)) {  // Пока текущая дата не после endDate
            // Создаем тестовую запись для текущего дня
            StockHistory record = createTestRecord(user, tickerSymbol, currentDate);

            // Проверяем, нет ли уже такой записи (требование ТЗ - уникальность)
            boolean exists = stockHistoryRepository.existsByTickerAndDateAndUserId(
                    tickerSymbol, currentDate, user.getId());

            if (!exists) {
                // Сохраняем запись в базу данных
                stockHistoryRepository.save(record);
                savedCount++;
                System.out.println("  Сохранена запись за " + currentDate);
            } else {
                System.out.println("  Пропущен дубликат за " + currentDate);
            }

            // Переходим к следующему дню
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("DEBUG: Всего сохранено записей: " + savedCount);
    }

    // Создает тестовую запись исторических данных
    private StockHistory createTestRecord(User user, String tickerSymbol, LocalDate date) {
        // Тестовые данные (в реальном проекте будут приходить из Polygon.io)
        BigDecimal openPrice = new BigDecimal("100.50").add(
                new BigDecimal(date.getDayOfMonth()));  // Просто для разнообразия
        BigDecimal closePrice = openPrice.add(new BigDecimal("1.25"));
        BigDecimal highPrice = closePrice.add(new BigDecimal("0.75"));
        BigDecimal lowPrice = openPrice.subtract(new BigDecimal("0.50"));
        Long volume = 1000000L + date.getDayOfMonth() * 50000L;

        // Создаем и возвращаем объект StockHistory
        return new StockHistory(
                tickerSymbol,   // Тикер
                date,           // Дата
                openPrice,      // Цена открытия
                closePrice,     // Цена закрытия
                highPrice,      // Максимальная цена
                lowPrice,       // Минимальная цена
                volume,         // Объем торгов
                user            // Владелец данных
        );
    }
}
