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
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.handler.hub.HubEventHandlers;
import ru.yandex.practicum.kafka.ConsumerHubService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventProcessor implements Runnable {
    final ConsumerHubService consumer;
    final HubEventHandlers hubHandlers;

    @Value("${kafka.topics.hub}")
    String topic;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", topic + "...");
            consumer.subscribe(List.of(topic));
            log.info("Получение обработчиков...");
            Map<String, HubEventHandler> hubHandlersMap = hubHandlers.getHandlers();

            while (true) {
                log.info("Ожидание сообщений...");
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        HubEventAvro hubEvent = (HubEventAvro) record.value();
                        String payloadName = hubEvent.getPayload().getClass().getSimpleName();

                        if (hubHandlersMap.containsKey(payloadName)) {
                            hubHandlersMap.get(payloadName).handle(hubEvent);
                        } else {
                            throw new IllegalArgumentException("Невозможно найти обработчик для события: " + hubEvent);
                        }
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