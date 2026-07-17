package com.e_commerce.shambhu.order.dto.response;

import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private Long id;

    private String orderNumber;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private Integer totalItems;

    private BigDecimal payableAmount;

    private LocalDateTime orderDate;
}
