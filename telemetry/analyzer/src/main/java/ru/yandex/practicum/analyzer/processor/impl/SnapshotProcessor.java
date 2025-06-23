package ru.yandex.practicum.analyzer.processor.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaConfig;
import ru.yandex.practicum.analyzer.dal.service.SnapshotService;
import ru.yandex.practicum.analyzer.processor.Processor;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SnapshotProcessor implements Processor<SensorsSnapshotAvro> {
    final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    final KafkaConfig.ConsumerConfig consumerConfig;
    final SnapshotService snapshotService;

    public SnapshotProcessor(KafkaConfig kafkaConfig, SnapshotService snapshotService) {
        this.consumerConfig = kafkaConfig.getConsumers().get(getClass().getSimpleName());
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.snapshotService = snapshotService;

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    public void start() {
        log.info("Subscribing to topics: {}", consumerConfig.getTopics());
        consumer.subscribe(consumerConfig.getTopics());
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> consumerRecords = consumer.poll(consumerConfig.getPollTimeout());
                log.debug("Polled {} records", consumerRecords.count());
                if (!consumerRecords.isEmpty()) {
                    processRecords(consumerRecords);
                    consumer.commitAsync();
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
    public void processRecords(ConsumerRecords<String, SensorsSnapshotAvro> consumerRecords) {
        int count = 0;
        log.info("Starting to process batch of {} records", consumerRecords.count());
        for (ConsumerRecord<String, SensorsSnapshotAvro> record : consumerRecords) {
            log.info("Processing snapshot with key: {}, value: {}, partition: {}, offset: {}",
                record.key(), record.value(), record.partition(), record.offset());
            log.info("Processing record: topic={}, partition={}, offset={}, key={}, timestamp={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.timestamp());
            manageOffsets(record, count++);
            processRecord(record.value());
        }
        log.info("Finished processing batch of {} records", consumerRecords.count());
    }

    @Override
    public void processRecord(SensorsSnapshotAvro record) {
        snapshotService.process(record);
    }

    @Override
    public void stop() {
        try {
            consumer.commitSync();
            consumer.close();
            log.info("Consumer closed");
        } catch (Exception exception) {
            log.error("Error closing consumer", exception);
        }
    }

    void manageOffsets(ConsumerRecord<?, ?> record, int count) {
        currentOffsets.put(
            new TopicPartition(record.topic(), record.partition()),
            new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % consumerConfig.getCommitInterval() == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.error("Failed to commit offsets {}", offsets, exception);
                    throw new RuntimeException("Offset commit failed", exception);
                } else {
                    log.debug("Offsets committed: {}", offsets);
                }
            });
        }
    }
}
