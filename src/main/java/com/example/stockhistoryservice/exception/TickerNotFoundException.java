package com.example.stockhistoryservice.exception;

public class TickerNotFoundException extends RuntimeException{
    public TickerNotFoundException(String message) {
        super(message);
    }
}
