package ru.yandex.practicum.exception;

public class WarehouseUnavailableException extends RuntimeException {
    public WarehouseUnavailableException(String message) {
        super(message);
    }
}