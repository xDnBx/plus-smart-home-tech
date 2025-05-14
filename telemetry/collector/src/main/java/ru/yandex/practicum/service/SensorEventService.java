package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.SensorEventMapper;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventService implements EventService<SensorEvent> {
    private final KafkaClient kafkaClient;
    private final SensorEventMapper sensorEventMapper;

    @Value("${kafka.sensor-topic}")
    private String topic;

    @Override
    public void sendEvent(SensorEvent event) {
        SensorEventAvro sensorEventAvro = sensorEventMapper.toAvro(event);
        kafkaClient.getProducer().send(new ProducerRecord<>(topic, sensorEventAvro));
        log.info("Событие {} отправлено в топик {}", event, topic);
    }
}