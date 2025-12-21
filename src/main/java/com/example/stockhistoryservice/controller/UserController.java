package com.example.stockhistoryservice.controller;

import com.example.stockhistoryservice.dto.RegisterRequest;
import com.example.stockhistoryservice.dto.SaveStockRequest;
import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.repository.UserRepository;
import com.example.stockhistoryservice.service.AuthService;
import com.example.stockhistoryservice.service.JwtService;
import com.example.stockhistoryservice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final StockService stockService;
    private final UserRepository userRepository;

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

    // Сохраняет исторические данные акций по запросу пользователя.
    // Основной бизнес-метод приложения.
    @PostMapping("/save")
    public ResponseEntity<Void> saveStockData(

            //Расшифровка аннотации @AuthenticationPrincipal:
            //Spring Security автоматически передает аутентифицированного пользователя
            //Это НЕ наша сущность User, а Spring Security User
            //Из него берем username (это email)
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser,
            @Valid @RequestBody SaveStockRequest request) {

        // 1. Найти нашего User по email (из Spring Security User)
        User user = userRepository.findByEmail(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Передать в StockService
        stockService.saveStockData(user.getId(), request);

        // 3. Вернуть статус 201 с пустым телом (по ТЗ)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ВРЕМЕННЫЙ метод для проверки работы JwtService - позже удалим!
   /* @GetMapping("/test-jwt")
    public ResponseEntity<Map<String, String>> testJwt() {
        // 1. Генерируем тестовые данные
        UUID testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String testEmail = "test@example.com";

        // 2. Генерируем токен с помощью JwtService
        String token = jwtService.generateToken(testUserId, testEmail);

        // 3. Извлекаем данные обратно из токена для проверки
        UUID extractedUserId = jwtService.getUserIdFromToken(token);
        String extractedEmail = jwtService.getEmailFromToken(token);
        boolean isValid = jwtService.validateToken(token);

        // 4. Формируем ответ
        Map<String, String> response = new HashMap<>();
        response.put("message", "Тест JwtService выполнен успешно!");
        response.put("generated_token", token);
        response.put("extracted_userId", extractedUserId.toString());
        response.put("extracted_email", extractedEmail);
        response.put("is_token_valid", String.valueOf(isValid));

        return ResponseEntity.ok(response);
    }
*/
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        // 1. Получаем аутентификацию из SecurityContext (её установил JWT фильтр)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Проверяем, что пользователь аутентифицирован
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. Получаем email пользователя (это username в Spring Security)
        String email = authentication.getName();

        // 4. Формируем ответ
        Map<String, String> response = new HashMap<>();
        response.put("message", "Доступ к защищенному эндпоинту успешен!");
        response.put("authenticated_user", email);
        response.put("timestamp", LocalDateTime.now().toString());

        // 5. Можно также получить authorities (роли)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (!authorities.isEmpty()) {
            response.put("roles", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", ")));
        }

        return ResponseEntity.ok(response);
    }
}
