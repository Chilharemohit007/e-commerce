package com.e_commerce.shambhu.shipment.dto.request;

import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {

    @NotNull(message = "Order id is required.")
    private Long orderId;

    @NotNull(message = "Delivery partner is required.")
    private DeliveryPartner deliveryPartner;

    @NotNull(message = "Estimated delivery date is required.")
    @FutureOrPresent(message = "Estimated delivery date cannot be in the past.")
    private LocalDate estimatedDeliveryDate;

    @Builder.Default
    @NotNull(message = "Shipping charge is required.")
    @DecimalMin(value = "0.00", inclusive = true,
            message = "Shipping charge cannot be negative.")
    private BigDecimal shippingCharge = BigDecimal.ZERO;

    @Size(max = 500,
            message = "Remarks cannot exceed 500 characters.")
    private String remarks;
}
