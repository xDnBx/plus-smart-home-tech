package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyzerRunner implements ApplicationRunner {
    final HubEventProcessor hubEventProcessor;
    final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // запускаем в отдельном потоке обработчик событий
        // от пользовательских хабов
        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        // В текущем потоке начинаем обработку
        // снимков состояния датчиков
//        snapshotProcessor.start();
    }
}