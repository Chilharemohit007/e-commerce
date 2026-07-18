package com.e_commerce.shambhu.payment.dto.response;

import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {

    private Long id;

    private String paymentReference;

    private Long orderId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
}
