package com.e_commerce.shambhu.order.dto.request;

import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Payment status is required.")
    private OrderPaymentStatus paymentStatus;

    private String transactionReference;
}
