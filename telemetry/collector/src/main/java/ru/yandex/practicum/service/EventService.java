package ru.yandex.practicum.service;

public interface EventService<T> {
    void sendEvent(T event);
}