package com.e_commerce.shambhu.payment.gateway;

import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.entity.Payment;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayPaymentResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayRefundResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayVerificationResponse;

public interface PaymentGatewayService {

    /**
     * Returns the gateway handled by this implementation.
     */
    PaymentGateway getGateway();

    /**
     * Creates payment/order at payment gateway.
     */
    GatewayPaymentResponse initiatePayment(Payment payment);

    /**
     * Verifies payment received from gateway callback/webhook.
     */
    GatewayVerificationResponse verifyPayment(
            VerifyPaymentRequest request);

    /**
     * Initiates refund.
     */
    GatewayRefundResponse refundPayment(
            Payment payment,
            RefundPaymentRequest request);
}
