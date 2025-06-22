package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
            .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
            .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("Received collectSensorEvent request: {}", request);
        try {
            if (!sensorEventHandlers.containsKey(request.getPayloadCase())) {
                throw new IllegalArgumentException("No handler found for payload case: " + request.getPayloadCase());
            }
            sensorEventHandlers.get(request.getPayloadCase()).handle(request);
            log.info("Successfully handled collectSensorEvent request");
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception exception) {
            log.error("Error handling collectSensorEvent request", exception);
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(exception)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("Received collectHubEvent request: {}", request);
        try {
            if (!hubEventHandlers.containsKey(request.getPayloadCase())) {
                throw new IllegalArgumentException("No handler found for payload case: " + request.getPayloadCase());
            }
            hubEventHandlers.get(request.getPayloadCase()).handle(request);
            log.info("Successfully handled collectHubEvent request");
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error handling collectHubEvent request", e);
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
