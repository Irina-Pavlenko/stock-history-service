package com.example.stockhistoryservice.config;

import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            // Находим пользователя по email (username = email в нашем случае)
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + username + " не найден"));

            // Преобразуем нашего User в Spring Security UserDetails
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_USER")  // Базовая роль для всех пользователей
                    .build();
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // 1. Включаем CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Отключаем CSRF для REST API
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Настраиваем авторизацию запросов
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC endpoints - доступны всем
                        //.requestMatchers("/api/user/test-jwt").permitAll()//ВРЕМЕННО для теста
                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()  // Важно для обработки ошибок
                        .requestMatchers("/actuator/**").permitAll()  // Для мониторинга

                        // PRIVATE endpoints - требуют аутентификации
                        .anyRequest().authenticated()
                )

                // 4. Настраиваем точку входа для аутентификации (возвращает 401 вместо 403)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // 5. Настраиваем сессии как STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 6. Добавляем заглушку для формы логина (отключаем)
                .formLogin(AbstractHttpConfigurer::disable)

                // 7. Отключаем базовую HTTP аутентификацию
                .httpBasic(AbstractHttpConfigurer::disable)

        // ★ 8. ДОБАВЛЯЕМ JWT ФИЛЬТР ★
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Разрешаем все origins для тестирования
        configuration.setAllowedOriginPatterns(List.of("*"));  // Разрешаем все домены

        // Разрешаем все методы
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Разрешаем все заголовки
        configuration.setAllowedHeaders(List.of("*"));

        // Разрешаем credentials (если нужно будет потом)
        configuration.setAllowCredentials(false); // Без cookies

        // Максимальное время кэширования preflight запроса
        configuration.setMaxAge(3600L); // Кэш на 1 час

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Применяем конфигурацию ко всем путям
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}