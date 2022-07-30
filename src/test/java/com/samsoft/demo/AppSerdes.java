package com.samsoft.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

@Slf4j
public class AppSerdes extends Serdes {

    public static Serde<Invoice> Invoice() {
        return new WrapperSerde<>(new InvoiceSerializer(), new InvoiceDeserializer());
    }
}
