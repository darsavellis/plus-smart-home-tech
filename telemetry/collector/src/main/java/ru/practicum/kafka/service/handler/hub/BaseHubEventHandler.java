package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    final KafkaClient kafkaClient;
    final KafkaProducerConfig kafkaProducerConfig;

    public abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent hubEvent) {
        log.debug("Processing hub event: hubId={}, type={}", hubEvent.getHubId(), getMessageType());

        if (!hubEvent.getType().equals(getMessageType())) {
            log.error("Incorrect hub event type: expected={}, actual={}", getMessageType(), hubEvent.getType());
            throw new IllegalArgumentException(
                String.format("Expected %s but got %s", getMessageType(), hubEvent.getType()));
        }

        log.debug("Mapping hub event to Avro format: hubId={}", hubEvent.getHubId());
        T event = mapToAvro(hubEvent);

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
            .setHubId(hubEvent.getHubId())
            .setTimestamp(hubEvent.getTimestamp())
            .setPayload(event)
            .build();

        String topicName = kafkaProducerConfig.getTopics().get("hubs-events");
        log.debug("Sending hub event to Kafka topic {}: hubId={}", topicName, hubEvent.getHubId());

        Future<RecordMetadata> future = kafkaClient.getProducer().send(new ProducerRecord<>(
            topicName,
            null,
            hubEvent.getTimestamp().toEpochMilli(),
            hubEventAvro.getHubId(),
            hubEventAvro));
    }
}

