package com.e_commerce.shambhu.payment.dto.response;

import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;

    private String paymentReference;

    private Long orderId;

    private String orderNumber;

    private Long customerId;

    private String customerName;

    private BigDecimal amount;

    private BigDecimal refundedAmount;

    private String currency;

    private PaymentMethod paymentMethod;

    private PaymentGateway paymentGateway;

    private OrderPaymentStatus paymentStatus;

    private String gatewayOrderId;

    private String gatewayTransactionId;

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    private String remarks;
}
