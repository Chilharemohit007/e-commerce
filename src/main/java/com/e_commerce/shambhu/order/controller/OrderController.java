package com.e_commerce.shambhu.order.controller;

import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import com.e_commerce.shambhu.order.dto.request.CancelOrderRequest;
import com.e_commerce.shambhu.order.dto.request.CreateOrderRequest;
import com.e_commerce.shambhu.order.dto.request.UpdateOrderStatusRequest;
import com.e_commerce.shambhu.order.dto.request.UpdatePaymentStatusRequest;
import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.order.dto.response.OrderSummaryResponse;
import com.e_commerce.shambhu.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        log.info("REST request to place order.");

        OrderResponse response = orderService.placeOrder(request);

        return ResponseBuilder.buildSuccess("Order placed successfully.",
                HttpStatus.OK, response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long orderId) {

        return ResponseBuilder.buildSuccess("Order fetch successfully.",
                HttpStatus.OK, orderService.getOrder(orderId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getMyOrders(
            Pageable pageable) {

        return ResponseBuilder.buildSuccess("My Order fetch successfully.",
                HttpStatus.OK, orderService.getMyOrders(pageable));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getAllOrders(
            Pageable pageable) {

        return ResponseBuilder.buildSuccess("All Order fetch successfully.",
                HttpStatus.OK, orderService.getAllOrders(pageable));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequest request) {

        return ResponseBuilder.buildSuccess("Order cancelled successfully.",
                        HttpStatus.OK, orderService.cancelOrder(orderId, request));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseBuilder.buildSuccess("Order status updated successfully.",
                        HttpStatus.OK, orderService.updateOrderStatus(orderId, request));
    }

    @PutMapping("/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        return ResponseBuilder.buildSuccess(
                        "Payment status updated successfully.", HttpStatus.OK,
                        orderService.updatePaymentStatus(orderId, request));
    }
}
