package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaClient;
import ru.yandex.practicum.collector.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEventProto sensorEvent) {
        return MotionSensorAvro.newBuilder()
                .setMotion(sensorEvent.getMotionSensorEvent().getMotion())
                .setLinkQuality(sensorEvent.getMotionSensorEvent().getLinkQuality())
                .setVoltage(sensorEvent.getMotionSensorEvent().getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
