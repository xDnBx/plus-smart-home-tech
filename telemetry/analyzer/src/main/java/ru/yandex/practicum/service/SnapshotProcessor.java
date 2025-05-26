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
import ru.yandex.practicum.handler.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.ConsumerSnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotProcessor {
    final ConsumerSnapshotService consumer;
    final SnapshotHandler snapshotHandler;

    @Value("${kafka.topics.snapshot}")
    String topic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", topic + "...");
            consumer.subscribe(List.of(topic));

            while (true) {
                Thread.sleep(2000);
                log.info("Ожидание сообщений...");
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        SensorsSnapshotAvro sensorsSnapshot = (SensorsSnapshotAvro) record.value();
                        log.info("Обработка снэпшота {}", sensorsSnapshot);
                        snapshotHandler.handleSnapshot(sensorsSnapshot);
                        log.info("Снэпшот {} обработан", sensorsSnapshot);
                    }
                    log.info("Выполнение фиксации смещений");
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений", e);
        } finally {
            try {
                log.info("Фиксация смещений");
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка во время сброса данных", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}