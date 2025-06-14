package ru.practicum.kafka.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;
import ru.practicum.kafka.model.sensor.impl.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {
    public LightSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    public LightSensorAvro mapToAvro(SensorEvent sensorEvent) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;

        return LightSensorAvro.newBuilder()
            .setLinkQuality(lightSensorEvent.getLinkQuality())
            .setLuminosity(lightSensorEvent.getLuminosity())
            .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
