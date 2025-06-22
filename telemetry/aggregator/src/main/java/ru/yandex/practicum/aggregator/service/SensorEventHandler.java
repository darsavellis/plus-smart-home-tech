package ru.yandex.practicum.aggregator.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.aggregator.config.KafkaClientConfig;
import ru.yandex.practicum.aggregator.config.KafkaProducerClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorEventHandler {
    final String SNAPSHOTS_EVENTS_TOPIC;
    final KafkaProducer<String, SpecificRecordBase> producer;
    final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();
    final KafkaProducerClient producerClient;

    public SensorEventHandler(KafkaProducerClient producerClient, KafkaClientConfig config) {
        this.producerClient = producerClient;
        this.producer = producerClient.getProducer();
        this.SNAPSHOTS_EVENTS_TOPIC = config.getProducerConfig().getTopics().get("snapshots-events");
    }

    public void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        try {
            SensorEventAvro sensorEventAvro = (SensorEventAvro) record.value();

            log.debug("Processing sensor event: hubId={}, timestamp={}",
                sensorEventAvro.getHubId(), sensorEventAvro.getTimestamp());

            Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = updateState(sensorEventAvro);
            sensorsSnapshotAvro.ifPresent(sensorSnapshotAvro -> sendMessage(sensorSnapshotAvro, sensorEventAvro));
        } catch (ClassCastException e) {
            log.error("Received message of unexpected type: {}", record.value().getClass(), e);
        } catch (Exception e) {
            log.error("Error processing record for hubId: {}, key: {}",
                record.key(), record.value(), e);
        }
    }

    private void sendMessage(SensorsSnapshotAvro sensorsSnapshotAvro, SensorEventAvro sensorEventAvro) {
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
            SNAPSHOTS_EVENTS_TOPIC,
            null,
            sensorEventAvro.getTimestamp().toEpochMilli(),
            sensorEventAvro.getHubId(),
            sensorsSnapshotAvro
        );

        producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                producerClient.stopProducer();
                log.error("Failed to send snapshot for hubId={} error={} ", sensorEventAvro.getHubId(),
                    exception.getMessage(), exception);
            } else {
                log.debug("Snapshot sent: hubId={}, partition={}, offset={}", sensorEventAvro.getHubId(),
                    metadata.partition(), metadata.offset());
            }
        });
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro sensorEventAvro) {
        String hubId = sensorEventAvro.getHubId();
        String sensorId = sensorEventAvro.getId();

        log.debug("updateState called: hubId={}, sensorId={}, timestamp={}", hubId, sensorId, sensorEventAvro.getTimestamp());

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, id -> {
            log.debug("Creating new snapshot for hubId={}, timestamp={}", id, sensorEventAvro.getTimestamp());
            return SensorsSnapshotAvro.newBuilder()
                .setHubId(id)
                .setTimestamp(sensorEventAvro.getTimestamp())
                .setSensorState(new HashMap<>())
                .build();
        });
        SensorStateAvro newState = toSensorState(sensorEventAvro);

        if (isStaleOrUnchanged(snapshot.getSensorState().get(sensorId), newState)) {
            log.debug("Skipping update for hubId={}, sensorId={} (stale or unchanged)", hubId, sensorId);
            return Optional.empty();
        }

        snapshot.getSensorState().put(sensorId, newState);
        log.debug("State updated for hubId={}, sensorId={}, newTimestamp={}", hubId, sensorId, newState.getTimestamp());
        snapshot.setTimestamp(sensorEventAvro.getTimestamp());

        return Optional.of(snapshot);
    }

    private SensorStateAvro toSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
            .setTimestamp(event.getTimestamp())
            .setData(event.getPayload())
            .build();
    }

    private boolean isStaleOrUnchanged(SensorStateAvro oldState, SensorStateAvro newState) {
        return oldState != null && (oldState.getTimestamp().isAfter(newState.getTimestamp()) ||
            oldState.getData().equals(newState.getData()));
    }
}
