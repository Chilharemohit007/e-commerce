package com.e_commerce.shambhu.payment.service;

import com.e_commerce.shambhu.payment.dto.request.CreatePaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.dto.response.PaymentResponse;
import com.e_commerce.shambhu.payment.dto.response.PaymentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PaymentService {

    /**
     * Initiate payment for an order.
     */
    PaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * Verify payment after gateway callback/webhook.
     */
    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    /**
     * Get payment details.
     */
    PaymentResponse getPayment(Long paymentId);

    /**
     * Logged-in user's payment history.
     */
    Page<PaymentSummaryResponse> getMyPayments(Pageable pageable);

    /**
     * Payment attempts for an order.
     */
    List<PaymentResponse> getOrderPayments(Long orderId);

    @Transactional(readOnly = true)
    Page<PaymentSummaryResponse> getOrderPayments(
            Long orderId,
            Pageable pageable);

    /**
     * Refund payment.
     */
    PaymentResponse refundPayment(RefundPaymentRequest request);
}
