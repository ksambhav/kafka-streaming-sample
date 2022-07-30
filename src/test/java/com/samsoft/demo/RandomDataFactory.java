package com.samsoft.demo;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Random;

import static java.math.BigDecimal.valueOf;

public class RandomDataFactory {

    static Faker faker = new Faker();
    static Random random = new Random();

    public static Invoice invoice() {
        return Invoice.builder()
                .id(random.nextInt()).name(faker.funnyName().name())
                .invoiceDate(LocalDate.now().minusDays(random.nextInt(366)))
                .amount(valueOf(random.nextDouble(99999))).build();
    }
}
