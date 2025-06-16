package ru.practicum.kafka.model.hub.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.hub.ConditionOperation;
import ru.practicum.kafka.model.hub.ConditionType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    String sensorId;
    ConditionType type;
    ConditionOperation operation;
    int value;
}
