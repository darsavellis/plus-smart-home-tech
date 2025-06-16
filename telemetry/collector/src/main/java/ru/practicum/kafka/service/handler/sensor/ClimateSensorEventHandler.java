package ru.practicum.kafka.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;
import ru.practicum.kafka.model.sensor.impl.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorAvro> {
    public ClimateSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public ClimateSensorAvro mapToAvro(SensorEvent sensorEvent) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
        return ClimateSensorAvro.newBuilder()
            .setCo2Level(climateSensorEvent.getCo2Level())
            .setHumidity(climateSensorEvent.getHumidity())
            .setTemperatureC(climateSensorEvent.getTemperatureC())
            .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
