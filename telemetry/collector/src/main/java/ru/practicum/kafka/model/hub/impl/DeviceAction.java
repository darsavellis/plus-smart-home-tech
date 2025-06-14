package ru.practicum.kafka.model.hub.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.hub.ActionType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAction {
    String sensorId;
    ActionType type;
    int value;
}
