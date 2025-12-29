package com.example.stockhistoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Конфигурация для RestTemplate
@Configuration
public class RestTemplateConfig {
    // Создает и настраивает RestTemplate для HTTP запросов
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return  restTemplate;
    }
}
