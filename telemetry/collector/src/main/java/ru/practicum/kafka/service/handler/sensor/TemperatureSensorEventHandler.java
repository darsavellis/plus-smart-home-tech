package ru.practicum.kafka.service.handler.sensor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.sensor.SensorEvent;
import ru.practicum.kafka.model.sensor.SensorEventType;
import ru.practicum.kafka.model.sensor.impl.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {
    public TemperatureSensorEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEvent sensorEvent) {
        TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
        return TemperatureSensorAvro.newBuilder()
            .setTemperatureC(temperatureSensorEvent.getTemperatureC())
            .setTemperatureF(temperatureSensorEvent.getTemperatureF())
            .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
