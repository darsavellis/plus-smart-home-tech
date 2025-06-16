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
public class MotionSensorEvent extends SensorEvent {
    int linkQuality;
    boolean motion;
    int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}

