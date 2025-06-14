package ru.practicum.kafka.model.hub.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.model.hub.DeviceType;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    String id;
    @NotBlank
    @JsonProperty("type")
    DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
