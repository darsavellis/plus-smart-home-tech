package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.practicum.kafka.model.hub.HubEvent;
import ru.practicum.kafka.model.hub.HubEventType;
import ru.practicum.kafka.model.hub.impl.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;

        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEvent.getConditions().stream()
            .map(scenarioCondition -> ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setValue(scenarioCondition.getValue())
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().toString()))
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().toString()))
                .build()).toList();

        List<DeviceActionAvro> deviceActionAvroList = scenarioAddedEvent.getActions().stream()
            .map(deviceAction -> DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().toString()))
                .setValue(deviceAction.getValue())
                .build()).toList();

        return ScenarioAddedEventAvro.newBuilder()
            .setName(scenarioAddedEvent.getName())
            .setActions(deviceActionAvroList)
            .setConditions(scenarioConditionAvroList)
            .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
