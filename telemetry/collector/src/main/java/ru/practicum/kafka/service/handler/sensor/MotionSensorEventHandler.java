package ru.practicum.kafka.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;
import ru.practicum.kafka.model.sensor.impl.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEvent sensorEvent) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;

        return MotionSensorAvro.newBuilder()
            .setMotion(motionSensorEvent.isMotion())
            .setLinkQuality(motionSensorEvent.getLinkQuality())
            .setVoltage(motionSensorEvent.getVoltage())
            .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
