package com.e_commerce.shambhu.order.dto.response;

import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private Long customerId;

    private String customerName;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;

    private BigDecimal totalDiscount;

    private BigDecimal shippingCharge;

    private BigDecimal taxAmount;

    private BigDecimal payableAmount;

    private Integer totalItems;

    private List<OrderItemResponse> items;

    private LocalDateTime orderedAt;

    private LocalDateTime estimatedDeliveryDate;

    // Future
    private String trackingNumber;

    private String courierName;

    private String couponCode;
}
