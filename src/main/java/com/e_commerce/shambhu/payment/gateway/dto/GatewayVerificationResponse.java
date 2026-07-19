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
public class GatewayVerificationResponse {

    private boolean verified;

    private String gatewayTransactionId;

    private String gatewayResponse;

    private String errorMessage;
}
