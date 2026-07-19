package com.e_commerce.shambhu.payment.mapper;

import com.e_commerce.shambhu.payment.dto.request.CreatePaymentRequest;
import com.e_commerce.shambhu.payment.dto.response.PaymentResponse;
import com.e_commerce.shambhu.payment.dto.response.PaymentSummaryResponse;
import com.e_commerce.shambhu.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final ModelMapper modelMapper;

    public Payment toEntity(CreatePaymentRequest request) {

        if (request == null) {
            return null;
        }

        Payment payment = new Payment();

        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentGateway(request.getPaymentGateway());

        return payment;
    }

    public PaymentResponse toResponse(Payment payment) {

        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())

                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())

                .customerId(payment.getUser().getId())
                .customerName(payment.getUser().getFirstName()+" "+payment.getUser().getLastName())

                .amount(payment.getAmount())
                .refundedAmount(payment.getRefundedAmount())
                .currency(payment.getCurrency())

                .paymentMethod(payment.getPaymentMethod())
                .paymentGateway(payment.getPaymentGateway())
                .paymentStatus(payment.getPaymentStatus())

                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayTransactionId(payment.getGatewayTransactionId())

                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())

                .remarks(payment.getRemarks())

                .build();
    }

    public PaymentSummaryResponse toSummary(Payment payment) {

        if (payment == null) {
            return null;
        }

        return PaymentSummaryResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Page<PaymentSummaryResponse> toSummaryPage(Page<Payment> payments) {

        if (payments == null) {
            return Page.empty();
        }

        return payments.map(this::toSummary);
    }

    /*public RefundResponse toRefundResponse(Refund refund){}

    public InvoiceResponse toInvoiceResponse(Invoice invoice){}

    public PaymentAuditResponse toAuditResponse(Payment payment){}*/
}
