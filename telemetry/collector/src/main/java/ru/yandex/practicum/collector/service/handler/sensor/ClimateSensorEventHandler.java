package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaClient;
import ru.yandex.practicum.collector.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorAvro> {
    public ClimateSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public ClimateSensorAvro mapToAvro(SensorEventProto sensorEvent) {
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(sensorEvent.getClimateSensorEvent().getCo2Level())
                .setHumidity(sensorEvent.getClimateSensorEvent().getHumidity())
                .setTemperatureC(sensorEvent.getClimateSensorEvent().getTemperatureC())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}
