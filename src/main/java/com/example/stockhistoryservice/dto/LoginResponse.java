package com.example.stockhistoryservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse { //Ответ после успешного входа
    private String token;

    private String tokenType = "Bearer"; //Тот, кто предъявит этот токен, получает доступ

    private UUID userId;
    private String email;
    private String message = "Вход выполнен успешно";

    public LoginResponse(String token, UUID userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }
}
