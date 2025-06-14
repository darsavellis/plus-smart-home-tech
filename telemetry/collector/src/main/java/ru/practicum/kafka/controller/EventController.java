package ru.practicum.kafka.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;
import ru.practicum.kafka.service.handler.HubEventHandler;
import ru.practicum.kafka.service.handler.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(List<SensorEventHandler> sensorEventHandlers,
                           List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
            .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
            .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    public void collectSensorEvents(@RequestBody SensorEvent sensorEvent) {
        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(sensorEvent.getType());
        if (Objects.isNull(sensorEventHandler)) {
            throw new IllegalArgumentException("Sensor event handler for type " + sensorEvent.getType() + " not found");
        }
        sensorEventHandler.handle(sensorEvent);
    }

    @PostMapping("/hubs")
    public void collectHubEvents(@RequestBody HubEvent hubEvent) {
        HubEventHandler hubEventHandler = hubEventHandlers.get(hubEvent.getType());
        if (Objects.isNull(hubEventHandler)) {
            throw new IllegalArgumentException("Hub event handler for type " + hubEvent.getType() + " not found");
        }
        hubEventHandler.handle(hubEvent);
    }
}
