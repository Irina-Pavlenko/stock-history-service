package com.example.stockhistoryservice.service;

import com.example.stockhistoryservice.dto.LoginRequest;
import com.example.stockhistoryservice.dto.LoginResponse;
import com.example.stockhistoryservice.dto.RegisterRequest;
import com.example.stockhistoryservice.entity.User;
import com.example.stockhistoryservice.exception.InvalidCredentialsException;
import com.example.stockhistoryservice.exception.UserAlreadyExistsException;
import com.example.stockhistoryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
    // Метод для входа
    public LoginResponse login(LoginRequest request){
        // 1. Найти пользователя по email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // 2. Проверить пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Неверный пароль");
        }

        // 3. Сгенерировать JWT токен. Этот токен внутри себя содержит информацию об ID и Email пользователя
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        // 4. Вернуть ответ с токеном
        return new LoginResponse(token, user.getId(), user.getEmail());
    }
}
