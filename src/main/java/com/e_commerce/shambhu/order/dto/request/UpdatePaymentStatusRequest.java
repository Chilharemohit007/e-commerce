package com.e_commerce.shambhu.order.dto.request;

import com.e_commerce.shambhu.order.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Payment status is required.")
    private PaymentStatus paymentStatus;

    private String transactionReference;
}
