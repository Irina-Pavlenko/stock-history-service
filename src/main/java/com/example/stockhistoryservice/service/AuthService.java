package com.example.stockhistoryservice.service;

import com.example.stockhistoryservice.dto.RegisterRequest;
import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.exception.UserAlreadyExistsException;
import com.example.stockhistoryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request){ // Содержит данные от клиента
        //  Проверяем, нет ли уже пользователя с таким email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw  new UserAlreadyExistsException(request.getEmail());
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }
}
