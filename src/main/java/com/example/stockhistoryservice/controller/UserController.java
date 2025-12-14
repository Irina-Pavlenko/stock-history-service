package com.example.stockhistoryservice.controller;

import com.example.stockhistoryservice.dto.RegisterRequest;
import com.example.stockhistoryservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // заглушка для saved
    @GetMapping("/saved")
    public ResponseEntity<?> getSavedStocks(@RequestParam(required = false) String ticker) {
        // TODO: Позже реализуем получение сохранённых акций
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint /api/user/saved ещё не реализован");
        response.put("status", "NOT_IMPLEMENTED");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }

    // заглушка для save
    @PostMapping("/save")
    public ResponseEntity<?> saveStockData(@RequestBody Map<String, String> request) {
        // TODO: Позже реализуем сохранение данных об акциях
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint /api/user/save ещё не реализован");
        response.put("status", "NOT_IMPLEMENTED");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }
}
