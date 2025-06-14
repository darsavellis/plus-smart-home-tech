package ru.practicum.kafka.service.handler.sensor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.service.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    final KafkaClient kafkaClient;
    final KafkaProducerConfig kafkaProducerConfig;

    public abstract T mapToAvro(SensorEvent sensorEvent);

    @Override
    public void handle(SensorEvent sensorEvent) {
        if (sensorEvent.getType().equals(getMessageType())) {
            throw new IllegalArgumentException();
        }

        T event = mapToAvro(sensorEvent);

        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
            .setId(sensorEvent.getId())
            .setHubId(sensorEvent.getHubId())
            .setTimestamp(sensorEvent.getTimestamp())
            .setPayload(event)
            .build();

        kafkaClient.getProducer().send(new ProducerRecord<>(
            kafkaProducerConfig.getTopics().get("sensors-events"),
            null,
            sensorEvent.getTimestamp().toEpochMilli(),
            sensorEventAvro.getHubId(),
            sensorEventAvro));
    }
}
