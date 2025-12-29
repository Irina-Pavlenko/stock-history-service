package com.example.stockhistoryservice.controller;

import com.example.stockhistoryservice.dto.LoginRequest;
import com.example.stockhistoryservice.dto.LoginResponse;
import com.example.stockhistoryservice.dto.RegisterRequest;
import com.example.stockhistoryservice.dto.SaveStockRequest;
import com.example.stockhistoryservice.entity.StockHistory;
import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.repository.StockHistoryRepository;
import com.example.stockhistoryservice.repository.UserRepository;
import com.example.stockhistoryservice.service.AuthService;
//import com.example.stockhistoryservice.service.JwtService;
import com.example.stockhistoryservice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    //private final JwtService jwtService;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final View error;

    // РЕГИСТРАЦИЯ
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //ЛОГИН
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /* заглушка для saved
    @GetMapping("/saved")
    public ResponseEntity<?> getSavedStocks(@RequestParam(required = false) String ticker) {
        // TODO: Позже реализуем получение сохранённых акций
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint /api/user/saved ещё не реализован");
        response.put("status", "NOT_IMPLEMENTED");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }
    */

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

    // ПОЛУЧЕНИЕ СОХРАНЕННЫХ ДАННЫХ АКЦИЙ
    // Возвращает исторические данные акций, сохраненные пользователем
    @GetMapping("/saved")
    public ResponseEntity<?> getSavedStocks(
            //автоматически внедряет аутентифицированного пользователя в метод контроллера(аннотация) через параметр springUser
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser,
            @RequestParam(required = false) String ticker){ //Аннотация Spring MVC для получения параметров из URL запроса.
        try { // УСПЕШНЫЙ СЦЕНАРИЙ
            // 1. Найти нашего User по email
            User user = userRepository.findByEmail(springUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            System.out.println("Запрос сохранённых данных для: " + user.getEmail());

            // 2. Получить данные из базы
            List<StockHistory> stocks;
            if (ticker != null && ticker.trim().isEmpty()) {
                // Фильтр по тикеру
                stocks = stockHistoryRepository.findByUserIdAndTicker(user.getId(), ticker.toUpperCase());

                System.out.println("Фильтр по тикеру: " + ticker);

            }else {
                // Все данные пользователя
                stocks = stockHistoryRepository.findByUserId(user.getId());
                System.out.println("Запрошены все тикеры");
            }
            System.out.println("Найдено записей: " + stocks.size());

            // 3. Преобразовать в формат ответа
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId().toString());
            response.put("ticker", ticker != null ? ticker: "all");

            // 4. Формируем массив данных
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (StockHistory stock : stocks) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", stock.getDate().toString());
                dayData.put("open", stock.getOpen());
                dayData.put("close", stock.getClose());
                dayData.put("high", stock.getHigh());
                dayData.put("low", stock.getLow());

                dataList.add(dayData);
            }
            response.put("data", dataList);

            return ResponseEntity.ok(response);

            // ОШИБОЧНЫЙ СЦЕНАРИЙ
        } catch (Exception e) {
            System.err.println("Ошибка в /api/user/saved: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("message", "Ошибка при получении сохранённых данных: " + e.getMessage());
        }
        return ResponseEntity //Создаем HTTP ответ
                .status(HttpStatus.INTERNAL_SERVER_ERROR) //Устанавливаем статус 500
                .body(error); //Добавляем тело ответа (JSON с ошибкой)

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
    //Вспомогательный эндпоигт для отладки и тестирования во время разработки
    //Работает ли аутентификация
    /*@GetMapping("/me")
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
     */
}
