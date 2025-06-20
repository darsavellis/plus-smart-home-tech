package ru.practicum.kafka.service.handler.hub;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.config.KafkaClient;
import ru.practicum.kafka.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaClient kafkaClient, KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaClient, kafkaProducerConfig);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto hubEvent) {
        List<ScenarioConditionAvro> scenarioConditionAvroList = hubEvent.getScenarioAdded().getConditionList().stream()
            .map(scenarioCondition -> ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setValue(switch (scenarioCondition.getValueCase()) {
                    case INT_VALUE -> scenarioCondition.getIntValue();
                    case BOOL_VALUE -> scenarioCondition.getBoolValue();
                    case VALUE_NOT_SET -> null;
                })
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().toString()))
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().toString()))
                .build()).toList();

        List<DeviceActionAvro> deviceActionAvroList = hubEvent.getScenarioAdded().getActionList().stream()
            .map(deviceAction -> DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().toString()))
                .setValue(deviceAction.getValue())
                .build()).toList();

        return ScenarioAddedEventAvro.newBuilder()
            .setName(hubEvent.getScenarioAdded().getName())
            .setActions(deviceActionAvroList)
            .setConditions(scenarioConditionAvroList)
            .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
