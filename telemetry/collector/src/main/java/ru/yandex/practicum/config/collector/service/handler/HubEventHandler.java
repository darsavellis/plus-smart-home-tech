package ru.yandex.practicum.config.collector.service.handler;


import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto hubEvent);
}
