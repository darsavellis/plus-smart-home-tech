package ru.yandex.practicum.analyzer.dal.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.dal.model.Condition;
import ru.yandex.practicum.analyzer.dal.model.Scenario;
import ru.yandex.practicum.analyzer.dal.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final ActionService actionService;

    private static final Map<ConditionTypeAvro, Function<Object, Integer>> SENSOR_VALUE_EXTRACTORS = Map.of(
        TEMPERATURE, data -> {
            if (data instanceof ClimateSensorAvro) return ((ClimateSensorAvro) data).getTemperatureC();
            if (data instanceof TemperatureSensorAvro) return ((TemperatureSensorAvro) data).getTemperatureC();
            return null;
        },
        CO2LEVEL, data -> data instanceof ClimateSensorAvro ? ((ClimateSensorAvro) data).getCo2Level() : null,
        HUMIDITY, data -> data instanceof ClimateSensorAvro ? ((ClimateSensorAvro) data).getHumidity() : null,
        LUMINOSITY, data -> data instanceof LightSensorAvro ? ((LightSensorAvro) data).getLuminosity() : null,
        MOTION, data -> data instanceof MotionSensorAvro ? (((MotionSensorAvro) data).getMotion() ? 1 : 0) : null,
        SWITCH, data -> data instanceof SwitchSensorAvro ? (((SwitchSensorAvro) data).getState() ? 1 : 0) : null
    );

    @Transactional(readOnly = true)
    public void process(SensorsSnapshotAvro snapshot) {
        scenarioRepository
            .findByHubId(snapshot.getHubId())
            .stream()
            .filter(scenario -> isConditionsMatchSnapshot(snapshot, scenario.getConditions()))
            .forEach(this::performActions);
    }

    boolean isConditionsMatchSnapshot(SensorsSnapshotAvro snapshot, Map<String, Condition> conditions) {
        return conditions.entrySet().stream()
            .allMatch(conditionEntry ->
                checkCondition(conditionEntry.getKey(), conditionEntry.getValue(), snapshot)
            );
    }

    boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro state = snapshot.getSensorState().get(sensorId);
        if (state == null) {
            return false;
        }
        Object data = state.getData();
        Function<Object, Integer> extractor = SENSOR_VALUE_EXTRACTORS.get(condition.getType());
        if (extractor == null) {
            return false;
        }
        Integer sensorValue = extractor.apply(data);
        return sensorValue != null && condition.check(sensorValue);
    }

    void performActions(Scenario scenario) {
        log.debug("Scenario [{}] triggered for hub [{}]. Executing actions.", scenario.getName(), scenario.getHubId());
        Timestamp timestamp = currentTimestamp();

        scenario.getActions().forEach((sensorId, action) ->
            actionService.executeAction(scenario.getHubId(), scenario.getName(), sensorId, action, timestamp)
        );
    }

    private Timestamp currentTimestamp() {
        Instant ts = Instant.now();
        return Timestamp.newBuilder()
            .setSeconds(ts.getEpochSecond())
            .setNanos(ts.getNano())
            .build();
    }
}
