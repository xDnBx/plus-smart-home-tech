package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.model.hub.HubEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService implements EventService<HubEvent> {
    private final KafkaClient kafkaClient;
    private final HubEventMapper hubEventMapper;

    @Value("${kafkaHubTopic:telemetry.hubs.v1}")
    private String topic;

    @Override
    public void sendEvent(HubEvent event) {
        HubEventAvro hubEventAvro = hubEventMapper.toAvro(event);
        kafkaClient.getProducer().send(new ProducerRecord<>(topic, hubEventAvro));
        log.info("Отправлено событие в топик {}: {}", topic, hubEventAvro);
    }
}