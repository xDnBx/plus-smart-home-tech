package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.SensorEventMapper;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventService implements EventService<SensorEvent> {
    private final KafkaProducerService kafkaProducer;
    private final SensorEventMapper sensorEventMapper;

    @Value("${kafka.sensor-topic}")
    private String topic;

    @Override
    public void sendEvent(SensorEvent event) {
        SensorEventAvro sensorEventAvro = sensorEventMapper.toAvro(event);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                event.getTimestamp().toEpochMilli(),
                sensorEventAvro.getHubId(),
                sensorEventAvro
        );
        kafkaProducer.send(record);
        kafkaProducer.flush();
        log.info("Событие {} отправлено в топик {}", sensorEventAvro, topic);
    }
}