package com.e_commerce.shambhu.payment.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PaymentReferenceGenerator {

    private static final String PREFIX = "PAY";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE;

    private final AtomicLong sequence = new AtomicLong(1);

    private LocalDate currentDate = LocalDate.now();

    public synchronized String generateReference() {

        LocalDate today = LocalDate.now();

        if (!today.equals(currentDate)) {
            currentDate = today;
            sequence.set(1);
        }

        return PREFIX
                + today.format(DATE_FORMAT)
                + String.format("%04d", sequence.getAndIncrement());
    }
}
