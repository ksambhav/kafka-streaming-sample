package com.samsoft.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;

public class InvoiceDeserializer implements Deserializer<Invoice> {

    private final ObjectMapper mapper;

    public InvoiceDeserializer() {
        this.mapper = ObjectMapperFactory.getInstance();
    }

    @SneakyThrows
    @Override
    public Invoice deserialize(String topic, byte[] data) {
        return mapper.readValue(data, Invoice.class);
    }
}
