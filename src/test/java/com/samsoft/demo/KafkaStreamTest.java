package com.samsoft.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.samsoft.demo.AppSerdes.String;
import static com.samsoft.demo.KafkaTests.BROKERS;

@Slf4j
public class KafkaStreamTest {

    @Test
    void helloStream() {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "hello-9");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, String().getClass());
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> kstream = streamsBuilder.stream(KafkaTests.TOPIC);
        kstream.foreach((k, v) -> System.out.println(v));
        Topology topology = streamsBuilder.build();
        log.info("{}", topology.describe());
        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            kafkaStreams.setStateListener((newState, oldState) -> System.out.println("Start changed from " + oldState + " to " + newState));
            log.info("Starting now");
            System.out.println("starting");
            kafkaStreams.start();
            log.info("After start");
            try {
                Thread.sleep(30_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after start");
        }
        log.info("done with test method");
    }
}
