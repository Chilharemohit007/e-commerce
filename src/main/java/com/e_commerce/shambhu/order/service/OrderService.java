package com.e_commerce.shambhu.order.service;

import com.e_commerce.shambhu.order.dto.request.CancelOrderRequest;
import com.e_commerce.shambhu.order.dto.request.CreateOrderRequest;
import com.e_commerce.shambhu.order.dto.request.UpdateOrderStatusRequest;
import com.e_commerce.shambhu.order.dto.request.UpdatePaymentStatusRequest;
import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.order.dto.response.OrderSummaryResponse;
import com.e_commerce.shambhu.shoppingCart.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /**
     * Creates an order from the authenticated user's cart.
     */
    OrderResponse placeOrder(CreateOrderRequest request);

    /**
     * Internal checkout operation invoked by CartService.
     */
    OrderResponse placeOrder(Cart cart, CreateOrderRequest request);

    /**
     * Retrieve a single order.
     */
    OrderResponse getOrder(Long orderId);

    /**
     * Retrieve authenticated user's orders.
     */
    Page<OrderSummaryResponse> getMyOrders(Pageable pageable);

    /**
     * Retrieve all orders (Admin).
     */
    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    /**
     * Cancel an order.
     */
    OrderResponse cancelOrder(
            Long orderId,
            CancelOrderRequest request);

    /**
     * Update order status (Admin).
     */
    OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request);

    /**
     * Update payment status.
     */
    OrderResponse updatePaymentStatus(
            Long orderId,
            UpdatePaymentStatusRequest request);

}
