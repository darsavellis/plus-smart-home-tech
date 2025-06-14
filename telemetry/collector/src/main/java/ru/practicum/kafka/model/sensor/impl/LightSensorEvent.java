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
public class LightSensorEvent extends SensorEvent {
    int linkQuality;
    int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
