package com.samsoft.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;

public class InvoiceSerializer implements Serializer<Invoice> {

    private final ObjectMapper mapper;

    public InvoiceSerializer() {
        this.mapper = ObjectMapperFactory.getInstance();
    }

    @SneakyThrows
    @Override
    public byte[] serialize(String topic, Invoice data) {
        return mapper.writeValueAsBytes(data);
    }
}
