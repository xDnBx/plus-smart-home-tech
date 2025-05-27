package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConsumerService;
import ru.yandex.practicum.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationStarter {

    final KafkaConsumerService consumer;
    final KafkaProducerService producer;
    final SensorSnapshotService sensorSnapshotService;

    @Value("${kafka.sensor-topic}")
    String sensorTopic;

    @Value("${kafka.snapshot-topic}")
    String snapshotTopic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", sensorTopic + "...");
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                log.info("Ожидание сообщений...");
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(5000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        SensorEventAvro event = (SensorEventAvro) record.value();
                        log.info("Обработка события от датчика {}", event);
                        sensorSnapshotService.updateState(event).ifPresent(snapshot ->
                                producer.send(snapshotTopic, snapshot.getHubId(), snapshot));
                        log.info("Событие от датчика {} обработано", event);
                    }
                    log.info("Выполнение фиксации смещений");
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Сбрасывание всех данных в буфере");
                producer.flush();
                log.info("Фиксация смещений");
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка во время сброса данных", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}