package ru.practicum.kafka.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class HubEvent {
    @NotBlank
    String hubId;
    final Instant timestamp = Instant.now();

    @NotNull
    public abstract HubEventType getType();
}
