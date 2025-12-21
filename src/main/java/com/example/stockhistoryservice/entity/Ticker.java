package com.example.stockhistoryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticker { // Справочник доступных акций для торговли на бирже

    @Id
    @NotBlank(message = "Символ тикера не может быть пустым") //Дополнительная валидация
    @Size(max = 10, message = "Символ тикера не может превышать 10 символов") //Дополнительная валидация
    @Column(length = 10, nullable = false)
    private String symbol; // Символ тикера (например, "AAPL")

    @NotBlank(message = "Название компании не может быть пустым") //Дополнительная валидация
    @Size(max = 100, message = "Название компании не может превышать 100 символов") //Дополнительная валидация
    @Column(length = 100, nullable = false)
    private String name; //Полное название компании
}
