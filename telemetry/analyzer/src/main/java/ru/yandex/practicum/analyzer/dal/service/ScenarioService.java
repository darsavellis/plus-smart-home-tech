package ru.yandex.practicum.analyzer.dal.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.dal.model.Action;
import ru.yandex.practicum.analyzer.dal.model.Condition;
import ru.yandex.practicum.analyzer.dal.model.ConditionOperation;
import ru.yandex.practicum.analyzer.dal.model.Scenario;
import ru.yandex.practicum.analyzer.dal.repository.ActionRepository;
import ru.yandex.practicum.analyzer.dal.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.dal.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.dal.repository.SensorRepository;
import ru.yandex.practicum.analyzer.exception.ScenarioNotFoundException;
import ru.yandex.practicum.analyzer.exception.UnknownDeviceException;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioService {
    final ScenarioRepository scenarioRepository;
    final ConditionRepository conditionRepository;
    final ActionRepository actionRepository;
    final SensorRepository sensorRepository;

    @Transactional
    public void processScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        log.info("Received request to add new scenario {} for hub {}", event.getName(), hubId);
        validateSensorsExist(hubId, collectSensorIds(event));

        Scenario scenario = getOrCreateScenario(hubId, event.getName());
        clearExistingScenarioData(scenario);

        updateScenarioConditions(scenario, event.getConditions());
        updateScenarioActions(scenario, event.getActions());

        saveScenarioData(scenario);
    }

    Set<String> collectSensorIds(ScenarioAddedEventAvro event) {
        Set<String> sensors = new HashSet<>();
        event.getConditions().stream()
            .map(ScenarioConditionAvro::getSensorId)
            .forEach(sensors::add);
        event.getActions().stream()
            .map(DeviceActionAvro::getSensorId)
            .forEach(sensors::add);
        return sensors;
    }

    void validateSensorsExist(String hubId, Set<String> sensors) {
        if (!sensorRepository.existsByIdInAndHubId(sensors, hubId)) {
            throw new UnknownDeviceException(hubId);
        }
    }

    Scenario getOrCreateScenario(String hubId, String name) {
        return scenarioRepository.findByHubIdAndName(hubId, name)
            .orElseGet(() -> Scenario.builder()
                .name(name)
                .hubId(hubId)
                .conditions(new HashMap<>())
                .actions(new HashMap<>())
                .build()
            );
    }

    void clearExistingScenarioData(Scenario scenario) {
        if (!scenario.getConditions().isEmpty()) {
            conditionRepository.deleteAll(scenario.getConditions().values());
            scenario.getConditions().clear();
        }
        if (!scenario.getActions().isEmpty()) {
            actionRepository.deleteAll(scenario.getActions().values());
            scenario.getActions().clear();
        }
    }

    void updateScenarioConditions(Scenario scenario, List<ScenarioConditionAvro> eventConditions) {
        for (ScenarioConditionAvro eventCondition : eventConditions) {
            Condition condition = Condition.builder()
                .type(eventCondition.getType())
                .operation(ConditionOperation.from(eventCondition.getOperation()))
                .value(mapValue(eventCondition.getValue()))
                .build();
            scenario.addCondition(eventCondition.getSensorId(), condition);
        }
    }

    void updateScenarioActions(Scenario scenario, List<DeviceActionAvro> eventActions) {
        for (DeviceActionAvro eventAction : eventActions) {
            Action action = Action.builder()
                .type(eventAction.getType())
                .value(eventAction.getType().equals(ActionTypeAvro.SET_VALUE) ? mapValue(eventAction.getValue()) : null)
                .build();
            scenario.addAction(eventAction.getSensorId(), action);
        }
    }

    void saveScenarioData(Scenario scenario) {
        conditionRepository.saveAll(scenario.getConditions().values());
        actionRepository.saveAll(scenario.getActions().values());
        scenarioRepository.save(scenario);
    }

    @Transactional
    public void processScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        log.info("Received request to delete scenario {} from hub {}", event.getName(), hubId);
        delete(event.getName(), hubId);
    }

    @Transactional
    public void delete(String name, String hubId) {
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(scenario -> {
            conditionRepository.deleteAll(scenario.getConditions().values());
            actionRepository.deleteAll(scenario.getActions().values());
            scenarioRepository.delete(scenario);
        });
    }

    @Transactional(readOnly = true)
    public Scenario findByHubIdAndName(String hubId, String name) {
        return scenarioRepository.findByHubIdAndName(hubId, name)
            .orElseThrow(() -> new ScenarioNotFoundException(name, hubId));
    }

    @Transactional(readOnly = true)
    public List<Scenario> findAllByHubId(String hubId) {
        return scenarioRepository.findAllByHubId(hubId);
    }

    Integer mapValue(Object value) {
        if (value != null) {
            if (value instanceof Integer i) return i;
            if (value instanceof Boolean b) return b ? 1 : 0;
        }
        return null;
    }
}
