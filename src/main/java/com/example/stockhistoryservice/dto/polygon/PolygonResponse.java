package com.example.stockhistoryservice.dto.polygon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

// DTO для ответа от Polygon.io API
// Этот класс представляет структуру JSON ответа от Polygon.io
// при запросе исторических данных акций

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonResponse {
    private String ticker;
    private String status; // Статус запроса: "OK" при успехе, "ERROR" при ошибке

    // Флаг, указывающий были ли данные скорректированы
    // Значения: "true" (скорректировано) или "false" (не скорректировано)
    private Boolean adjusted;

    // Количество запрашиваемых дней (максимум 50000 для бесплатного тарифа)
    // Может быть больше чем resultsCount если есть пропуски данных
    private Integer queryCount;

    // Фактическое количество полученных результатов
    // Может быть меньше queryCount если нет данных за некоторые дни
    // (например, выходные или праздники)
    private Integer resultsCount;

    // Массив дневных данных об акции
    // Каждый элемент содержит данные за один торговый день
    private List <PolygonDayData> results;

    // Кастомный сеттер для String → Boolean
    public void setAdjusted(String adjustedStr) {
        this.adjusted = adjustedStr != null &&
                "true".equalsIgnoreCase(adjustedStr);
    }
}


