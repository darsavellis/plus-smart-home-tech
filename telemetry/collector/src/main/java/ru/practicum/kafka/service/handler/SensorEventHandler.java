package ru.practicum.kafka.service.handler;

import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent sensorEvent);
}
