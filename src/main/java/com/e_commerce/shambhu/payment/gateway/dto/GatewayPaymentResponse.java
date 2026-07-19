package com.e_commerce.shambhu.payment.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayPaymentResponse {

    private boolean success;

    /**
     * Gateway order id
     * Example:
     * Razorpay -> order_xxxxx
     * Stripe -> pi_xxxxx
     */
    private String gatewayOrderId;

    /**
     * Gateway transaction/payment id
     */
    private String gatewayTransactionId;

    /**
     * URL where frontend redirects user.
     * Null for COD.
     */
    private String redirectUrl;

    /**
     * Gateway specific response (JSON/XML)
     */
    private String rawResponse;

    /**
     * Failure reason if any.
     */
    private String errorMessage;
}