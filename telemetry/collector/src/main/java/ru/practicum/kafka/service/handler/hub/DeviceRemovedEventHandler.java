package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;
import ru.practicum.kafka.model.hub.impl.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
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
        return HubEventType.DEVICE_REMOVED;
    }
}
