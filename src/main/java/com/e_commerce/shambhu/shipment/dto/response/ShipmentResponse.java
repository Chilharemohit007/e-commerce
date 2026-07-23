package com.e_commerce.shambhu.shipment.dto.response;

import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {

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

    private String courierTrackingNumber;

    private String courierReferenceId;

    /**
     * Charges
     */
    private BigDecimal shippingCharge;

    /**
     * Delivery Dates
     */
    private LocalDate estimatedDeliveryDate;

    private LocalDate actualDeliveryDate;

    /**
     * Shipment Timeline
     */
    private LocalDateTime dispatchedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime returnedAt;

    /**
     * Additional Information
     */
    private String remarks;

    private String failureReason;

    /**
     * Audit Information
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
