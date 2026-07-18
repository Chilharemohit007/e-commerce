package com.e_commerce.shambhu.payment.dto.request;

import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment gateway is required")
    private PaymentGateway paymentGateway;
}
