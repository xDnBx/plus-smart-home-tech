package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ShoppingStoreApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApp.class, args);
    }
}