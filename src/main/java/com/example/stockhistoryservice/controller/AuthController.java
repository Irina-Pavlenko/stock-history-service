package com.example.stockhistoryservice.controller;

import com.example.stockhistoryservice.dto.LoginRequest;
import com.example.stockhistoryservice.dto.LoginResponse;
import com.example.stockhistoryservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    private ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        //1. Делегирование логики: Контроллер передаёт данные в сервисный слой.
        //2. Получение результата: Сервис возвращает объект LoginResponse с токеном и данными пользователя.
        LoginResponse response = authService.login(request);
        //Создаёт HTTP-ответ со статусом 200 OK и помещает объект response в тело ответа
        return ResponseEntity.ok(response);
    }
}
