package com.example.stockhistoryservice.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String email){
        super("Пользователь с электронной почтой " + email + " уже существует");
    }
}
