package ru.practicum.kafka.service.handler.sensor;

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
import ru.practicum.kafka.service.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    final KafkaClient kafkaClient;
    final KafkaProducerConfig kafkaProducerConfig;

    public abstract T mapToAvro(SensorEventProto sensorEvent);

    @Override
    public void handle(SensorEventProto sensorEvent) {
        log.debug("Processing sensor event: id={}, type={}", sensorEvent.getId(), getMessageType());

        if (!sensorEvent.getPayloadCase().equals(getMessageType())) {
            log.error("Incorrect sensor event type: expected={}, actual={}", getMessageType(), sensorEvent.getPayloadCase());
            throw new IllegalArgumentException(
                String.format("Expected %s but got %s", getMessageType(), sensorEvent.getPayloadCase()));
        }

        log.debug("Mapping sensor event to Avro format: id={}", sensorEvent.getId());
        T event = mapToAvro(sensorEvent);

        Instant timestamp = Instant.ofEpochSecond(
            sensorEvent.getTimestamp().getSeconds(),
            sensorEvent.getTimestamp().getNanos()
        );

        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
            .setId(sensorEvent.getId())
            .setHubId(sensorEvent.getHubId())
            .setTimestamp(timestamp)
            .setPayload(event)
            .build();

        String topicName = kafkaProducerConfig.getTopics().get("sensors-events");
        log.debug("Sending sensor event to Kafka topic {}: id={}, hubId={}", topicName, sensorEvent.getId(), sensorEvent.getHubId());

        Future<RecordMetadata> future = kafkaClient.getProducer().send(new ProducerRecord<>(
            topicName,
            null,
            timestamp.toEpochMilli(),
            sensorEventAvro.getHubId(),
            sensorEventAvro));
    }
}
