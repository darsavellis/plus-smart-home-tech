package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    final KafkaClient kafkaClient;
    final KafkaProducerConfig kafkaProducerConfig;

    public abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent hubEvent) {
        if (hubEvent.getType().equals(getMessageType())) {
            throw new IllegalArgumentException();
        }

        T event = mapToAvro(hubEvent);

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
            .setHubId(hubEvent.getHubId())
            .setTimestamp(hubEvent.getTimestamp())
            .setPayload(event)
            .build();

        kafkaClient.getProducer().send(new ProducerRecord<>(
            kafkaProducerConfig.getTopics().get("hubs-events"),
            null,
            hubEvent.getTimestamp().toEpochMilli(),
            hubEventAvro.getHubId(),
            hubEventAvro));
    }
}
