package ru.yandex.practicum.analyzer.dal.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.dal.model.Action;
import ru.yandex.practicum.analyzer.exception.ActionExecutionException;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionService {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private static final Map<ActionTypeAvro, ActionTypeProto> ACTION_TYPE_MAP = Stream.of(ActionTypeProto.values()).filter(proto -> proto != ActionTypeProto.UNRECOGNIZED).collect(Collectors.toMap(proto -> ActionTypeAvro.valueOf(proto.name()), proto -> proto));

    public void executeAction(String hubId, String scenarioName, String sensorId, Action action, Timestamp timestamp) {
        try {
            DeviceActionProto actionProto = createActionProto(sensorId, action);
            sendActionToHub(hubId, scenarioName, actionProto, timestamp);
            logActionSuccess(action, hubId, sensorId);
        } catch (Exception exception) {
            logActionError(action, hubId, sensorId, exception);
            throw new ActionExecutionException("Action execution error", exception);
        }
    }

    private DeviceActionProto createActionProto(String sensorId, Action action) {
        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder().setSensorId(sensorId).setType(ACTION_TYPE_MAP.get(action.getType()));

        if (action.getType().equals(ActionTypeAvro.SET_VALUE)) {
            actionBuilder.setValue(action.getValue());
        }

        return actionBuilder.build();
    }

    private void sendActionToHub(String hubId, String scenarioName, DeviceActionProto actionProto, Timestamp timestamp) {
        DeviceActionRequest request = DeviceActionRequest.newBuilder().setHubId(hubId).setScenarioName(scenarioName).setAction(actionProto).setTimestamp(timestamp).build();

        hubRouterClient.handleDeviceAction(request);
    }

    private void logActionSuccess(Action action, String hubId, String sensorId) {
        log.debug("Action [{}] successfully executed for hub [{}], device [{}]", action.getType().name(), hubId, sensorId);
    }

    private void logActionError(Action action, String hubId, String sensorId, Exception e) {
        log.error("Error executing action [{}] for hub [{}] for device [{}]", action.getType().name(), hubId, sensorId, e);
    }
}
