package ru.yandex.practicum.handler.hub.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioRemovedHandler implements HubEventHandler {
    final ActionRepository actionRepository;
    final ConditionRepository conditionRepository;
    final ScenarioRepository scenarioRepository;

    @Override
    public String getType() {
        return ScenarioRemovedHandler.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedAvro = (ScenarioRemovedEventAvro) event.getPayload();
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(event.getHubId(),
                scenarioRemovedAvro.getName());

        if (scenario.isPresent()) {
            Scenario scenarioEntity = scenario.get();
            actionRepository.deleteByScenario(scenarioEntity);
            conditionRepository.deleteByScenario(scenarioEntity);
            scenarioRepository.delete(scenarioEntity);
            log.info("Scenario {} deleted", scenarioEntity.getName());
        } else {
            log.warn("Scenario {} not found", scenarioRemovedAvro.getName());
        }
    }
}