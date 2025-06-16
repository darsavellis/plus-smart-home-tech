package ru.practicum.kafka.service.handler;

import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent hubEvent);
}
