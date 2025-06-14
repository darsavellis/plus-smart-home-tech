package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;
import ru.practicum.kafka.model.hub.impl.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;
        return DeviceAddedEventAvro.newBuilder()
            .setId(deviceAddedEvent.getId())
            .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().toString()))
            .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}
