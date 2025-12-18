package com.example.stockhistoryservice.config;

import com.example.stockhistoryservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Пропускаем публичные эндпоинты
        if (request.getServletPath().contains("/api/auth/login") ||
                request.getServletPath().contains("/api/user/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Извлекаем токен из заголовка Authorization
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Извлекаем JWT токен (убираем "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 4. Извлекаем email из токена
            final String userEmail = jwtService.getEmailFromToken(jwt);

            // 5. Если email есть и пользователь еще не аутентифицирован в текущем контексте
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Загружаем данные пользователя из базы
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Проверяем валидность токена
                if (jwtService.validateToken(jwt)) {

                    // 8. Создаем объект аутентификации
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // 9. Добавляем детали запроса
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Устанавливаем аутентификацию в контекст безопасности
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Логируем ошибку, но пропускаем запрос дальше (вернется 401 на защищенном эндпоинте)
            logger.error("JWT authentication error: " + e.getMessage());
        }

        // 11. Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}
