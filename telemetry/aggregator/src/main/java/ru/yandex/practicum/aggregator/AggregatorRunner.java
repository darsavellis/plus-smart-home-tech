package ru.yandex.practicum.aggregator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaClient;
import ru.yandex.practicum.aggregator.config.KafkaClientConfig;
import ru.yandex.practicum.aggregator.config.KafkaConsumerClient;
import ru.yandex.practicum.aggregator.service.SensorEventHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatorRunner implements CommandLineRunner {
    static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    final String SENSORS_EVENTS_TOPIC;

    final KafkaConsumerClient consumerClient;
    final SensorEventHandler sensorEventHandler;

    public AggregatorRunner(KafkaClient kafkaClient, SensorEventHandler sensorEventHandler, KafkaClientConfig config) {
        this.consumerClient = (KafkaConsumerClient) kafkaClient;
        this.sensorEventHandler = sensorEventHandler;
        this.SENSORS_EVENTS_TOPIC = config.getConsumerConfig().getTopics().get("sensors-events");
    }

    @Override
    public void run(String... args) throws Exception {
        KafkaConsumer<String, SpecificRecordBase> consumer = consumerClient.getConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(SENSORS_EVENTS_TOPIC));
            log.info("Subscribed to topic: {}", SENSORS_EVENTS_TOPIC);

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> consumerRecords = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SpecificRecordBase> record : consumerRecords) {
                    sensorEventHandler.handleRecord(record);
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer shutdown initiated");
        } catch (Exception e) {
            log.error("Fatal error in consumer loop", e);
        } finally {
            try {
                consumerClient.stopConsumer();
                log.info("Kafka client stopped successfully");
            } catch (Exception e) {
                log.error("Error stopping Kafka client", e);
            }
        }
    }
}
