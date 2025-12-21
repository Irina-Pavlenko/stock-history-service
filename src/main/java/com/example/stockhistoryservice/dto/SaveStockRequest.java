package com.example.stockhistoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SaveStockRequest { //Запрос на сохранение данных об акциях

    @NotBlank(message = "Тикер не может быть пустым")
    private String ticker;

    @NotNull(message = "Дата начала не может быть пустой")
    private LocalDate start;

    @NotNull(message = "Дата окончания не может быть пустой")
    private LocalDate end;
}
