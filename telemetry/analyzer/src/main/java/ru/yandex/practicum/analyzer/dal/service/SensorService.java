package ru.yandex.practicum.analyzer.dal.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.dal.model.Sensor;
import ru.yandex.practicum.analyzer.dal.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensorService {
    final SensorRepository sensorRepository;

    public void processDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        Optional<Sensor> maybeAdded = sensorRepository.findByIdAndHubId(event.getId(), hubId);
        if (maybeAdded.isPresent()) {
            log.info("Device with id [{}] is already registered in hub [{}]", event.getId(), hubId);
            return;
        }

        Sensor sensor = Sensor.builder()
                .hubId(hubId)
                .id(event.getId())
                .build();

        log.debug("New sensor registered in hub [{}]: [{}]", hubId, event.getId());
        sensorRepository.save(sensor);
    }

    public void processDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        log.debug("Removing sensor [{}] from hub [{}]", event.getId(), hubId);
        sensorRepository.findByIdAndHubId(event.getId(), hubId).ifPresent(sensorRepository::delete);
    }
}
