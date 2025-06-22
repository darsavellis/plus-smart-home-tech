package ru.yandex.practicum.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface KafkaConsumerClient {
    KafkaConsumer<String, SpecificRecordBase> getConsumer();

    void stopConsumer();
}
