package com.e_commerce.shambhu.order.dto.request;

import com.e_commerce.shambhu.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required.")
    private Long shippingAddressId;

    @NotNull(message = "Billing address is required.")
    private Long billingAddressId;

    @NotNull(message = "Payment method is required.")
    private PaymentMethod paymentMethod;

    private String customerNote;
}
