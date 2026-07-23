package com.e_commerce.shambhu.shipment.util;

import com.e_commerce.shambhu.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TrackingNumberGenerator {

    private static final String PREFIX = "SHP";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private final ShipmentRepository shipmentRepository;

    /**
     * Generates a unique shipment tracking number.
     *
     * Example:
     * SHP20260723012345
     */
    public String generateTrackingNumber() {

        String trackingNumber;

        do {

            trackingNumber = PREFIX
                    + LocalDate.now().format(DATE_FORMAT)
                    + String.format("%06d",
                    ThreadLocalRandom.current().nextInt(0, 999999));

        } while (shipmentRepository.findByTrackingNumber(trackingNumber).isPresent());

        return trackingNumber;
    }

}
