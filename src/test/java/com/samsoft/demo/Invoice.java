package com.samsoft.demo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Invoice {

    private int id;
    private String name;
    private LocalDate invoiceDate;
    private BigDecimal amount;
}
