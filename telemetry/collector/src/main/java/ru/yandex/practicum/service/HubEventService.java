package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.model.hub.HubEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService implements EventService<HubEvent> {
    private final KafkaProducerService kafkaProducer;
    private final HubEventMapper hubEventMapper;

    @Value("${kafka.hub-topic}")
    private String topic;

    @Override
    public void sendEvent(HubEvent event) {
        HubEventAvro hubEventAvro = hubEventMapper.toAvro(event);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                event.getTimestamp().toEpochMilli(),
                hubEventAvro.getHubId(),
                hubEventAvro
        );
        kafkaProducer.send(record);
        kafkaProducer.flush();
        log.info("Отправлено событие в топик {}: {}", topic, hubEventAvro);
    }
}