package ru.practicum.kafka.model.hub.impl;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
