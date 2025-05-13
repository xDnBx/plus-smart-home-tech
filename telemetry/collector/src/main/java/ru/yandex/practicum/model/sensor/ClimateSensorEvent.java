package ru.yandex.practicum.model.sensor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEvent extends SensorEvent {
    Integer temperatureC;
    Integer humidity;
    Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVEMT;
    }
}