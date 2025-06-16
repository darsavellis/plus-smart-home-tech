package ru.practicum.kafka.model.sensor.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEvent extends SensorEvent {
    int temperatureC;
    int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
