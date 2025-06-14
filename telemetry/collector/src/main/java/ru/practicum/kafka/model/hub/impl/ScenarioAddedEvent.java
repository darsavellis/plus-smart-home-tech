package ru.practicum.kafka.model.hub.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;

import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    String name;
    @NotNull
    List<ScenarioCondition> conditions;
    @NotNull
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
