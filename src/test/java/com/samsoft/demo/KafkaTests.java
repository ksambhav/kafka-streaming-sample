package com.samsoft.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class KafkaTests {

    @Test
    void testProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 7_000);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5_000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 5);
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            for (int i = 0; i < 100; i++) {
                Future<RecordMetadata> send = producer.send(new ProducerRecord<>("test-topic", "test", "test"));
                RecordMetadata metadata = send.get();
                log.info("Sent record {},{},{}", metadata.offset(), (metadata.partition()), metadata.topic());
//                Thread.sleep(500);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while sending", e);
        }
    }
}
