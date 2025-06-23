package ru.yandex.practicum.aggregator.config;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Setter
@Getter
@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaClientConfig {
    KafkaBaseConfig producerConfig;
    KafkaBaseConfig consumerConfig;

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {
            KafkaProducer<String, SpecificRecordBase> producer;
            KafkaConsumer<String, SpecificRecordBase> consumer;

            @Override
            public KafkaProducer<String, SpecificRecordBase> getProducer() {
                if (Objects.isNull(producer)) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                producer = new KafkaProducer<>(producerConfig.getProperties());
            }

            @Override
            public KafkaConsumer<String, SpecificRecordBase> getConsumer() {
                if (Objects.isNull(consumer)) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                consumer = new KafkaConsumer<>(consumerConfig.getProperties());
            }

            @Override
            @PreDestroy
            public void stopProducer() {
                if (Objects.nonNull(producer)) {
                    producer.flush();
                    producer.close();
                }
            }

            @Override
            @PreDestroy
            public void stopConsumer() {
                if (Objects.nonNull(consumer)) {
                    consumer.close();
                }
            }
        };
    }
}
