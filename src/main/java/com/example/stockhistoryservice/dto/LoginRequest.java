package com.example.stockhistoryservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {//Вход существующего пользователя

    @NotBlank(message = "Введите email")
    @Email(message ="Адрес электронной почты должен быть действительным")
    private String email;

    @NotBlank(message = "Введите пароль")
    private String password;
}
