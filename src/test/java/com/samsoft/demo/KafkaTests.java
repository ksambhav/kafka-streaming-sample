package com.samsoft.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
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

    public static final String TOPIC = "test-topic1";
    public static final String BROKERS = "192.168.1.17:29092,192.168.1.17:29093,192.168.1.17:29094,192.168.1.17:29095";
    public static final String INVOICE_TOPIC = "INVOICE_TOPIC_1";

    @SneakyThrows
    @Test
    void testProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 70_000);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 50_000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 500);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            int i = 0;
            while (true) {
                producer.send(new ProducerRecord<>(TOPIC, "test key  " + i, "test value" + i));
                i++;
                Thread.sleep(1000);
            }
        }
    }

    @SneakyThrows
    @Test
    void invoiceProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, InvoiceSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 7_000);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5_000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 5);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        try (KafkaProducer<Integer, Invoice> producer = new KafkaProducer<>(properties)) {
            producer.initTransactions();
            while (true) {
                producer.beginTransaction();
                Invoice invoice = RandomDataFactory.invoice();
                producer.send(new ProducerRecord<>(INVOICE_TOPIC, invoice.getId(), invoice));
                System.out.println("Sent " + invoice);
                producer.commitTransaction();
                Thread.sleep(500);
            }
        } finally {
            log.info("done");
        }
    }

    @Test
    void testConsumer() {
        Properties properties = new Properties();
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(BOOTSTRAP_SERVERS_CONFIG, BROKERS);
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
