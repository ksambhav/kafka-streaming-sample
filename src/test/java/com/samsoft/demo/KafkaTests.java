package com.samsoft.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static java.util.Collections.singleton;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Slf4j
public class KafkaTests {

    public static final String TOPIC = "test-topic";

    @Test
    void testProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.17:29092");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 70_000);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 50_000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 5);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.initTransactions();
            producer.beginTransaction();
            for (int i = 0; i < 100_000; i++) {
                producer.send(new ProducerRecord<>(TOPIC, "test key  " + i, "test value" + i));
            }
            producer.commitTransaction();
        }
        log.info("done producing");
    }

    @Test
    void testConsumer() {
        Properties properties = new Properties();
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "192.168.1.17:29092");
        properties.put(GROUP_ID_CONFIG, "group-a1");
        properties.put(MAX_POLL_RECORDS_CONFIG, 1000);
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(singleton(TOPIC));
        consumer.assignment().forEach(it -> log.info("assignment {},{}", it.partition(), it.topic()));
        while (true) {
            ConsumerRecords<String, String> polled = consumer.poll(Duration.ofSeconds(10));
            if (polled.count() > 0) {
                polled.forEach(i -> log.info("{}", i.value()));
            } else {
                consumer.close();
                break;
            }
        }
    }
}
