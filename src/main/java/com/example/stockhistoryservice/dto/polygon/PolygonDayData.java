package com.example.stockhistoryservice.dto.polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// Дневные данные об акции
// Содержит информацию за ОДИН торговый день
// Polygon.io использует краткие имена для экономии трафика

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonDayData{
    private Long v; // Volume (объем торгов) - количество проданных акций за день
    private BigDecimal vw; // Volume Weighted Средневзвешенная цена по объему торгов
    // Рассчитывается как: (сумма (цена × объем)) / (общий объем)
    private BigDecimal o; // Open (цена открытия) - цена первой сделки дня
    private BigDecimal c; // Close (цена закрытия) - цена последней сделки дня
    private BigDecimal h; // High (максимальная цена) - самая высокая цена за день
    private BigDecimal l; // Low (минимальная цена) - самая низкая цена за день
    private Long t; // Timestamp (метка времени) в миллисекундах
    private Integer n; // Number of transactions (количество транзакций/сделок за день)

    @JsonIgnore // не будет сериализоваться в JSON
    public LocalDate getTradeDate() {
        // Проверка на null: если timestamp отсутствует, возвращаем null
        if (t == null) {
            return null;
        }
        // Instant (момент времени на временной шкале)
        Instant instant = Instant.ofEpochMilli(t);

        // ZonedDateTime (добавляем часовой пояс)
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));

        // LocalDate (извлекаем только дату)
        LocalDate tradeDate = zonedDateTime.toLocalDate();
        return tradeDate;
    }
}