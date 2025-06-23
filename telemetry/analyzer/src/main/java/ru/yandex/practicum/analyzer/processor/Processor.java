package ru.yandex.practicum.analyzer.processor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface Processor<T extends SpecificRecordBase> {
    void processRecords(ConsumerRecords<String, T> consumerRecords);

    void processRecord(T record);

    void start();

    void stop();
}
