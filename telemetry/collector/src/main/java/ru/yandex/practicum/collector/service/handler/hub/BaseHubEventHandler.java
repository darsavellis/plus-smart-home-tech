package ru.yandex.practicum.collector.service.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaClient;
import ru.yandex.practicum.collector.config.KafkaProducerConfig;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    final KafkaClient kafkaClient;
    final KafkaProducerConfig kafkaProducerConfig;

    public abstract T mapToAvro(HubEventProto hubEvent);

    @Override
    public void handle(HubEventProto hubEvent) {
        log.debug("Processing hub event: hubId={}, type={}", hubEvent.getHubId(), getMessageType());

        if (!hubEvent.getPayloadCase().equals(getMessageType())) {
            log.error("Incorrect hub event type: expected={}, actual={}", getMessageType(), hubEvent.getPayloadCase());
            throw new IllegalArgumentException(
                String.format("Expected %s but got %s", getMessageType(), hubEvent.getPayloadCase()));
        }

        log.debug("Mapping hub event to Avro format: hubId={}", hubEvent.getHubId());
        T event = mapToAvro(hubEvent);

        Instant timestamp = Instant.ofEpochSecond(
            hubEvent.getTimestamp().getSeconds(),
            hubEvent.getTimestamp().getNanos()
        );

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
            .setHubId(hubEvent.getHubId())
            .setTimestamp(timestamp)
            .setPayload(event)
            .build();

        String topicName = kafkaProducerConfig.getTopics().get("hubs-events");
        log.debug("Sending hub event to Kafka topic {}: hubId={}", topicName, hubEvent.getHubId());

        Future<RecordMetadata> future = kafkaClient.getProducer().send(new ProducerRecord<>(
            topicName,
            null,
            timestamp.toEpochMilli(),
            hubEventAvro.getHubId(),
            hubEventAvro));
    }
}
