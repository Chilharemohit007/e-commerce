package com.e_commerce.shambhu.payment.controller;

import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import com.e_commerce.shambhu.payment.dto.request.CreatePaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.dto.response.PaymentResponse;
import com.e_commerce.shambhu.payment.dto.response.PaymentSummaryResponse;
import com.e_commerce.shambhu.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        log.info("REST request to create payment.");

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseBuilder.buildSuccess(
                "Payment created successfully.", HttpStatus.CREATED, response);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        log.info("REST request to verify payment.");

        PaymentResponse response = paymentService.verifyPayment(request);

        return ResponseBuilder.buildSuccess(
                "Payment verified successfully.", HttpStatus.OK, response);
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Valid @RequestBody RefundPaymentRequest request) {

        log.info("REST request to refund payment.");

        PaymentResponse response = paymentService.refundPayment(request);

        return ResponseBuilder.buildSuccess("Refund processed successfully.", HttpStatus.OK, response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long paymentId) {

        log.info("REST request to fetch payment. paymentId={}", paymentId);

        PaymentResponse response = paymentService.getPayment(paymentId);

        return ResponseBuilder.buildSuccess("Payment fetched successfully.", HttpStatus.OK, response);
    }

    @GetMapping("/my-payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<PaymentSummaryResponse>>> getMyPayments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        log.info("REST request to fetch authenticated user's payment history.");

        Page<PaymentSummaryResponse> response =
                paymentService.getMyPayments(pageable);

        return ResponseBuilder.buildSuccess("Payment history fetched successfully.", HttpStatus.OK, response);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<ApiResponse<Page<PaymentSummaryResponse>>> getOrderPayments(
            @PathVariable Long orderId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        log.info("REST request to fetch payments for orderId={}", orderId);

        Page<PaymentSummaryResponse> response =
                paymentService.getOrderPayments(orderId, pageable);

        return ResponseBuilder.buildSuccess("Order payment history fetched successfully.", HttpStatus.OK, response);
    }
}
