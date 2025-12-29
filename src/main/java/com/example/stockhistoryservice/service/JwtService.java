package com.example.stockhistoryservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    // Секретный ключ для подписи токена (берётся из application.properties)
    @Value("${jwt.secret}")
    private String secret;

    // Время жизни токена (24 часа)
    private static final long EXPIRATION_TIME = 24*60*60*1000;

    // Генерация секретного ключа из строки
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Генерация JWT токена для пользователя
    public String generateToken(UUID userId, String email){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .subject(userId.toString()) // В subject кладём ID пользователя
                .claim("email", email)   // Дополнительные данные (email)
                .issuedAt(now)              // Время создания
                .expiration(expiryDate)     // Время истечения
                .signWith(getSigningKey(),Jwts.SIG.HS256)  // Подпись ключом
                .compact();                 // Финальное построение строки токена
    }

    // Извлечение email из токена
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    // Проверка валидности токена (не истёк ли)
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true; // Если не выброшено исключение — токен валиден
        } catch (Exception e){
            return false; // Любая ошибка (подпись, срок) — токен невалиден
        }
    }
}
