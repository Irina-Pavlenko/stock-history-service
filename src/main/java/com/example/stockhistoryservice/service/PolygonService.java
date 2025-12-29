package com.example.stockhistoryservice.service;

import com.example.stockhistoryservice.dto.polygon.PolygonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

// Сервис для работы с Polygon.io API. Отвечает за получение исторических данных акций
@Slf4j // // Lombok: автоматически создает logger
@Service
@RequiredArgsConstructor
public class PolygonService {

    // API ключ для доступа к Polygon.io
    @Value("${polygon.api.key}")
    private String apiKey;

    // Базовый URL Polygon.io API
    @Value("${polygon.api.base-url}")
    private String baseUrl;

    // Выполнения HTTP запросов. Spring автоматически внедрит сконфигурированный бин
    private final RestTemplate restTemplate;

    // Получает исторические данные акции за указанный период
    public PolygonResponse getHistoricalData(String ticker, String startDate, String endDate) {
        log.info("Запрос исторических данных: {} с {} по {}", ticker, startDate, endDate);

        String url = buildUrl(ticker, startDate, endDate);
        log.debug("URL запроса к Polygon.io отправлен (ключ замаскирован)");

        try {
            PolygonResponse response = restTemplate.getForObject(url, PolygonResponse.class);

            if (response == null) {
                log.error("Пустой ответ от Polygon.io для тикера {}", ticker);
                throw new RuntimeException("Пустой ответ от Polygon.io");
            }

            if (!"OK".equals(response.getStatus())) {
                log.error("Ошибка Polygon.io: статус {}", response.getStatus());
                throw new RuntimeException("Ошибка Polygon.io: " + response.getStatus());
            }

            log.info("Получено {} записей по тикеру {}",
                    response.getResultsCount(), ticker);
            return response;

        } catch (Exception e) {
            log.error("Ошибка при запросе данных с Polygon.io: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить данные с Polygon.io: " + e.getMessage(), e);
        }
    }

    // Строит URL для запроса к Polygon.io API
    private String buildUrl(String ticker, String startDate, String endDate) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)  // ← Исправить!
                .path("/v2/aggs/ticker/{ticker}/range/1/day/{from}/{to}")
                .queryParam("apiKey", apiKey)
                .queryParam("adjusted", "true")
                .buildAndExpand(ticker, startDate, endDate)
                .toUriString();
    }
/*
    // Проверяет доступность Polygon.io API (тестовый метод)
    // return true если API доступен, false если нет
    public boolean isApiAvailable() {
        try {
            // Простой тестовый запрос (AAPL за последний день)
            String testUrl = baseUrl + "/v2/aggs/ticker/AAPL/range/1/day/2024-01-01/2024-01-01?apiKey=" + apiKey;
            PolygonResponse response = restTemplate.getForObject(testUrl, PolygonResponse.class);
            return response != null && "OK".equals(response.getStatus());
        } catch (Exception e) {
            log.warn("Polygon.io API недоступен: {}", e.getMessage());
            return false;
        }
    }
    */
}
