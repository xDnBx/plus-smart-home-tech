package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class CollectorController {
    private final EventService<HubEvent> hubEventService;
    private final EventService<SensorEvent> sensorEventService;

    @PostMapping("/hubs")
    public void sendHubEvent(@RequestBody @Valid HubEvent event) {
        log.info("Получен запрос на обработку события от хаба: {}", event);
        hubEventService.sendEvent(event);
    }

    @PostMapping("/sensors")
    public void sendSensorEvent(@RequestBody @Valid SensorEvent event) {
        log.info("Получен запрос на обработку события от датчика: {}", event);
        sensorEventService.sendEvent(event);
    }
}