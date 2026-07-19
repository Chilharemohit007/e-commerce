package com.e_commerce.shambhu.payment.gateway;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import org.springframework.stereotype.Service;
import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.entity.Payment;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import com.e_commerce.shambhu.payment.gateway.PaymentGatewayService;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayPaymentResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayRefundResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CodGatewayService implements PaymentGatewayService {


    @Override
    public PaymentGateway getGateway() {
        return PaymentGateway.COD;
    }

    @Override
    public GatewayPaymentResponse initiatePayment(Payment payment) {

        log.info("Creating COD payment for {}",
                payment.getPaymentReference());

        return GatewayPaymentResponse.builder()
                .success(true)
                .gatewayOrderId(null)
                .gatewayTransactionId(null)
                .redirectUrl(null)
                .rawResponse("""
                    {
                       "paymentType":"COD",
                       "status":"PENDING"
                    }
                    """)
                .build();
    }

    @Override
    public GatewayVerificationResponse verifyPayment(
            VerifyPaymentRequest request) {

        throw new BusinessException(
                "Cash on Delivery payments cannot be verified through the payment gateway.");
    }

    @Override
    public GatewayRefundResponse refundPayment(
            Payment payment,
            RefundPaymentRequest request) {

        log.info("Initiating COD refund for {}",
                payment.getPaymentReference());

        return GatewayRefundResponse.builder()
                .success(true)
                .refundReference(
                        "COD_REFUND_" + payment.getPaymentReference())
                .gatewayResponse("""
                    {
                       "refundMode":"MANUAL",
                       "status":"INITIATED"
                    }
                    """)
                .build();
    }


}
