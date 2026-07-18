package com.e_commerce.shambhu.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    @NotBlank(message = "Gateway transaction id is required")
    private String gatewayTransactionId;

    @NotBlank(message = "Gateway signature is required")
    private String gatewaySignature;
}
