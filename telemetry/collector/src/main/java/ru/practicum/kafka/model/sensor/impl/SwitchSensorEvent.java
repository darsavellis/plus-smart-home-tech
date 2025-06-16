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
public class SwitchSensorEvent extends SensorEvent {
    boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
