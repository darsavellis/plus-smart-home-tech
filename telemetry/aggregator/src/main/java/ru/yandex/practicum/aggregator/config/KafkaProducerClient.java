package ru.yandex.practicum.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;

public interface KafkaProducerClient {
    KafkaProducer<String, SpecificRecordBase> getProducer();

    void stopProducer();
}
