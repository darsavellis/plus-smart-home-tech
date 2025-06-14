package ru.practicum.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    final Map<HubEventType, HubEventHandler> hubEventHandlers;

    @Autowired
    public EventController(List<SensorEventHandler> sensorEventHandlers,
                           List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
            .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
            .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        log.info("EventController initialized with {} sensor handlers and {} hub handlers",
            this.sensorEventHandlers.size(), this.hubEventHandlers.size());
    }

    @PostMapping("/sensors")
    public void collectSensorEvents(@RequestBody SensorEvent sensorEvent) {
        log.info("Received sensor event: id={}, type={}", sensorEvent.getId(), sensorEvent.getType());
        log.debug("Full sensor event details: {}", sensorEvent);

        SensorEventHandler sensorEventHandler = sensorEventHandlers.get(sensorEvent.getType());
        if (Objects.isNull(sensorEventHandler)) {
            log.error("Sensor event handler for type {} not found", sensorEvent.getType());
            throw new IllegalArgumentException("Sensor event handler for type " + sensorEvent.getType() + " not found");
        }

        sensorEventHandler.handle(sensorEvent);
        log.info("Sensor event processed successfully: id={}", sensorEvent.getId());
    }

    @PostMapping("/hubs")
    public void collectHubEvents(@RequestBody HubEvent hubEvent) {
        log.info("Received hub event: hubId={}, type={}", hubEvent.getHubId(), hubEvent.getType());
        log.debug("Full hub event details: {}", hubEvent);

        HubEventHandler hubEventHandler = hubEventHandlers.get(hubEvent.getType());
        if (Objects.isNull(hubEventHandler)) {
            log.error("Hub event handler for type {} not found", hubEvent.getType());
            throw new IllegalArgumentException("Hub event handler for type " + hubEvent.getType() + " not found");
        }

        hubEventHandler.handle(hubEvent);
        log.info("Hub event processed successfully: hubId={}", hubEvent.getHubId());
    }
}
