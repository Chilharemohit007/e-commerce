package com.e_commerce.shambhu.payment.gateway;

import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.entity.Payment;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayPaymentResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayRefundResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class MockGatewayService implements PaymentGatewayService {

    @Override
    public PaymentGateway getGateway() {
        return PaymentGateway.MOCK;
    }

    @Override
    public GatewayPaymentResponse initiatePayment(Payment payment) {

        log.info("Initiating MOCK payment for paymentReference={}",
                payment.getPaymentReference());

        String gatewayOrderId = "MOCK_ORDER_" +
                UUID.randomUUID().toString().replace("-", "");

        String gatewayTransactionId = "MOCK_TXN_" +
                UUID.randomUUID().toString().replace("-", "");

        return GatewayPaymentResponse.builder()
                .success(true)
                .gatewayOrderId(gatewayOrderId)
                .gatewayTransactionId(gatewayTransactionId)
                .redirectUrl(
                        "http://localhost:8080/mock-payment?reference="
                                + payment.getPaymentReference())
                .rawResponse("""
                    {
                      "status":"CREATED",
                      "gateway":"MOCK"
                    }
                    """)
                .build();
    }

    @Override
    public GatewayVerificationResponse verifyPayment(
            VerifyPaymentRequest request) {

        log.info("Verifying MOCK payment {}",
                request.getPaymentReference());

        return GatewayVerificationResponse.builder()
                .verified(true)
                .gatewayTransactionId(
                        request.getGatewayTransactionId())
                .gatewayResponse("""
                    {
                       "status":"SUCCESS"
                    }
                    """)
                .build();
    }

    @Override
    public GatewayRefundResponse refundPayment(
            Payment payment,
            RefundPaymentRequest request) {

        log.info("Refunding MOCK payment {}",
                payment.getPaymentReference());

        return GatewayRefundResponse.builder()
                .success(true)
                .refundReference(
                        "MOCK_REFUND_" +
                                UUID.randomUUID()
                                        .toString()
                                        .replace("-", ""))
                .gatewayResponse("""
                    {
                       "refundStatus":"SUCCESS"
                    }
                    """)
                .build();
    }
}
