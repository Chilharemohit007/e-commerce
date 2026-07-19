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
public class GatewayRefundResponse {

    private boolean success;

    private String refundReference;

    private String gatewayResponse;

    private String errorMessage;
}