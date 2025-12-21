package com.example.stockhistoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_history",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_ticker_date_user",
                columnNames = {"ticker", "date", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String ticker; // Например: "AAPL", "GOOGL", "TSLA"

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal open; // Цена открытия

    @Column(nullable = false, precision = 10,scale = 2)
    private BigDecimal close; // Цена закрытия

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal high; // Максимальная цена дня

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal low; // Минимальная цена дня

    private Long volume;  //Объем торгов (количество проданных акций за день)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Пользователь, который сохранил данные

    @CreationTimestamp // Автоматически ставит текущее время при СОЗДАНИИ
    @Column(name = "created_at", updatable = false) // updatable=false - нельзя изменить
    private LocalDateTime createdAt;

    @UpdateTimestamp // Автоматически обновляется при ИЗМЕНЕНИИ
    @Column(name = "updated_at") // Можно обновлять
    private LocalDateTime updatedAt;

    // Конструктор для удобства
    public StockHistory(String ticker, LocalDate date,
                        BigDecimal open, BigDecimal close,
                        BigDecimal high, BigDecimal low,
                        Long volume, User user) {
        this.ticker = ticker;
        this.date = date;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.user = user;
    }
}

