package com.e_commerce.shambhu.order.common;

import com.e_commerce.shambhu.order.dto.request.CreateOrderRequest;
import com.e_commerce.shambhu.order.dto.response.OrderItemResponse;
import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.order.dto.response.OrderSummaryResponse;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ModelMapper modelMapper;

    public Order toEntity(CreateOrderRequest request) {

        if (request == null) {
            return null;
        }

        Order order = new Order();

        order.setCustomerNote(request.getCustomerNote());

        return order;
    }

    public OrderResponse toResponse(Order order) {

        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())

                .customerId(order.getUser().getId())
                .customerName(order.getUser().getFirstName()+" "+order.getUser().getLastName())

                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())

                .totalAmount(order.getTotalAmount())
                .totalDiscount(order.getDiscountAmount())
                .shippingCharge(order.getShippingCharge())
                .taxAmount(order.getTaxAmount())
                .payableAmount(order.getPayableAmount())
                .totalItems(order.getTotalItems())

                .orderedAt(order.getCreatedAt())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())

                .items(toOrderItemResponses(order.getOrderItems()))

                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {

        if (item == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .sku(item.getSku())
                .primaryImage(item.getPrimaryImage())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountAmount(item.getDiscountAmount())
                .taxAmount(item.getTaxAmount())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public List<OrderItemResponse> toOrderItemResponses(
            List<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    public OrderResponse toOrderResponse(Order order) {

        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())

                .customerId(order.getUser().getId())
                .customerName(order.getUser().getFirstName()+" "+order.getUser().getLastName())

                //.addressId(order.getAddress().getId())

                //.subtotal(order.getSubtotal())
                //.discountAmount(order.getDiscountAmount())
                .shippingCharge(order.getShippingCharge())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())

                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())

                //.placedAt(order.getPlacedAt())
                //.deliveredAt(order.getDeliveredAt())
               // .cancelledAt(order.getCancelledAt())

               // .notes(order.getNotes())

                //.items(toOrderItemResponses(order.getOrderItems()))
//
                .build();
    }

    public OrderSummaryResponse toSummary(Order order) {

        if (order == null) {
            return null;
        }

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .payableAmount(order.getPayableAmount())
                .totalItems(order.getTotalItems())
                .orderDate(order.getCreatedAt())
                .build();
    }

    public Page<OrderSummaryResponse> toSummaryPage(
            Page<Order> orders) {

        return orders.map(this::toSummary);
    }
}
