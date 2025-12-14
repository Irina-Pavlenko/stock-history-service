package com.example.stockhistoryservice.repository;

import com.example.stockhistoryservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Этот метод Spring реализует автоматически!
    // Находит пользователя по email. Spring сам сгенерирует SQL:SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Проверяет, существует ли пользователь с таким email
    boolean existsByEmail(String email); //Spring сгенерирует: SELECT COUNT(*) > 0 FROM users WHERE email = ?
}
