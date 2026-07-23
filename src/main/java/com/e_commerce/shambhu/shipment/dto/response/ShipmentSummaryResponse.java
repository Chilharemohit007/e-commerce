package com.e_commerce.shambhu.shipment.dto.response;

import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentSummaryResponse {

    /**
     * Shipment Information
     */
    private Long shipmentId;

    private String trackingNumber;

    private ShipmentStatus shipmentStatus;

    /**
     * Order Information
     */
    private Long orderId;

    private String orderNumber;

    /**
     * Customer Information
     */
    private Long customerId;

    private String customerName;

    /**
     * Delivery Partner Information
     */
    private DeliveryPartner deliveryPartner;

    /**
     * Charges
     */
    private BigDecimal shippingCharge;

    /**
     * Delivery Information
     */
    private LocalDate estimatedDeliveryDate;

    private LocalDate actualDeliveryDate;

}
