package ru.yandex.practicum.analyzer.processor.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaConfig;
import ru.yandex.practicum.analyzer.dal.service.ScenarioService;
import ru.yandex.practicum.analyzer.dal.service.SensorService;
import ru.yandex.practicum.analyzer.processor.Processor;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HubEventProcessor implements Processor<HubEventAvro>, Runnable {
    final KafkaConsumer<String, HubEventAvro> consumer;
    final KafkaConfig.ConsumerConfig consumerConfig;
    final ScenarioService scenarioService;
    final SensorService sensorService;

    public HubEventProcessor(KafkaConfig kafkaConfig, ScenarioService scenarioService, SensorService sensorService) {
        this.consumerConfig = kafkaConfig.getConsumers().get(getClass().getSimpleName());
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.scenarioService = scenarioService;
        this.sensorService = sensorService;

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    @Override
    public void run() {
        start();
    }


    public void start() {
        log.info("Subscribing to topics: {}", consumerConfig.getTopics());
        consumer.subscribe(consumerConfig.getTopics());
        try {
            while (true) {
                ConsumerRecords<String, HubEventAvro> consumerRecords = consumer.poll(consumerConfig.getPollTimeout());
                log.debug("Polled {} records", consumerRecords.count());
                if (!consumerRecords.isEmpty()) {
                    processRecords(consumerRecords);
                    consumer.commitSync();
                }
            }
        } catch (WakeupException exception) {
            log.info("Consumer wakeup signal received, shutting down");
        } catch (Exception exception) {
            log.error("Unexpected error while consuming records", exception);
        } finally {
            stop();
        }
    }

    @Override
    public void processRecords(ConsumerRecords<String, HubEventAvro> consumerRecords) {
        for (ConsumerRecord<String, HubEventAvro> record : consumerRecords) {
            log.info("Processing record with key: {}, value: {}, partition: {}, offset: {}",
                    record.key(), record.value(), record.partition(), record.offset());
            processRecord(record.value());
        }
    }

    @Override
    public void processRecord(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();
        switch (hubEvent.getPayload()) {
            case DeviceAddedEventAvro dae -> sensorService.processDeviceAdded(hubId, dae);
            case DeviceRemovedEventAvro dre -> sensorService.processDeviceRemoved(hubId, dre);
            case ScenarioAddedEventAvro sae -> scenarioService.processScenarioAdded(hubId, sae);
            case ScenarioRemovedEventAvro sre -> scenarioService.processScenarioRemoved(hubId, sre);
            default -> log.warn("Received unknown event type {}", hubEvent);
        }
    }

    @Override
    public void stop() {
        try {
            consumer.close();
            log.info("Consumer closed");
        } catch (Exception exception) {
            log.error("Error closing consumer", exception);
        }
    }
}
