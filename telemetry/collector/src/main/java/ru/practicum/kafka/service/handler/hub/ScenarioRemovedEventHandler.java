package ru.practicum.kafka.service.handler.hub;

import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;
import ru.practicum.kafka.model.hub.impl.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

public class ScenarioRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public ScenarioRemovedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public DeviceRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;
        return DeviceRemovedEventAvro.newBuilder()
            .setId(deviceRemovedEvent.getId())
            .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
