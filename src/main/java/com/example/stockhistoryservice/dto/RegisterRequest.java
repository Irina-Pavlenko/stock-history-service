package com.example.stockhistoryservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest { //Регистрация нового пользователя
    @NotBlank(message = "Введите адрес электронной почты")
    @Email(message = "Адрес электронной почты должен быть действительным")
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
    message = "Пароль должен содержать цифры, символы, строчные и заглавные буквы английского алфавита")
    private String password;

    private String fullName;
}
