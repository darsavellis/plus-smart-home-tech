package ru.yandex.practicum.collector.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaClient;
import ru.yandex.practicum.collector.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEventProto hubEvent) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(hubEvent.getDeviceAdded().getId())
                .setType(DeviceTypeAvro.valueOf(hubEvent.getDeviceAdded().getType().toString()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}
