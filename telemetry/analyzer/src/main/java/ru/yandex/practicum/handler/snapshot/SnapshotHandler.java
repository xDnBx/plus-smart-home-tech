package ru.yandex.practicum.handler.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.HubRouterClient;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotHandler {
    final ActionRepository actionRepository;
    final ConditionRepository conditionRepository;
    final ScenarioRepository scenarioRepository;
    final HubRouterClient hubRouterClient;

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Handling snapshot: {}", snapshot);
        Map<String, SensorStateAvro> sensorState = snapshot.getSensorsState();
        scenarioRepository.findByHubId(snapshot.getHubId()).stream()
                .filter(scenario -> checkScenario(scenario, sensorState))
                .forEach(scenario -> {
                    log.info("Send action from scenario {}", scenario);
                    sendAction(scenario);
                });
    }

    private Boolean checkScenario(Scenario scenario, Map<String, SensorStateAvro> sensorState) {
        return conditionRepository.findAllByScenario(scenario).stream()
                .allMatch(condition -> checkCondition(condition, sensorState));
    }

    private Boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorState) {
        SensorStateAvro sensorStateAvro = sensorState.get(condition.getSensor().getId());

        if (sensorStateAvro == null) return false;

        switch (condition.getType()) {
            case MOTION -> {
                MotionSensorAvro motionSensor = (MotionSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, motionSensor.getMotion() ? 1 : 0);
            }
            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, lightSensor.getLuminosity());
            }
            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, switchSensor.getState() ? 1 : 0);
            }
            case TEMPERATURE -> {
                ClimateSensorAvro temperatureSensor = (ClimateSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, temperatureSensor.getTemperatureC());
            }
            case CO2LEVEL -> {
                ClimateSensorAvro co2Sensor = (ClimateSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, co2Sensor.getCo2Level());
            }
            case HUMIDITY -> {
                ClimateSensorAvro humiditySensor = (ClimateSensorAvro) sensorStateAvro.getData();
                return checkOperation(condition, humiditySensor.getHumidity());
            }
            default -> {
                return false;
            }
        }
    }

    private Boolean checkOperation(Condition condition, Integer value) {
        Integer conditionValue = condition.getValue();

        switch (condition.getOperation()) {
            case EQUALS -> {
                return Objects.equals(value, conditionValue);
            }
            case GREATER_THAN -> {
                return value > conditionValue;
            }
            case LOWER_THAN -> {
                return value < conditionValue;
            }
            default -> {
                return false;
            }
        }
    }

    private void sendAction(Scenario scenario) {
        actionRepository.findAllByScenario(scenario).forEach(hubRouterClient::sendRequest);
    }
}