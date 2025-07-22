package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaClient;
import ru.yandex.practicum.collector.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {
    public TemperatureSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEventProto sensorEvent) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(sensorEvent.getTemperatureSensorEvent().getTemperatureC())
                .setTemperatureF(sensorEvent.getTemperatureSensorEvent().getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}
