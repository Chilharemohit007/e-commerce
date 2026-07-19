package com.e_commerce.shambhu.payment.validator;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.payment.entity.Payment;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import com.e_commerce.shambhu.payment.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class PaymentValidator {

    public void validateOrder(Order order) {

        if (order == null) {
            throw new BusinessException("Order does not exist.");
        }
    }

    public void validateOwnership(Order order, User user) {

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(
                    "You are not allowed to make payment for this order.");
        }
    }

    public void validatePaymentEligibility(Order order) {

        OrderStatus status = order.getOrderStatus();

        if (status == OrderStatus.CANCELLED
                || status == OrderStatus.DELIVERED
                || status == OrderStatus.RETURNED) {

            throw new BusinessException(
                    "Payment cannot be initiated for order status: " + status);
        }
    }

    /**
     * Prevent duplicate successful payment.
     */
    public void validateDuplicatePayment(Order order) {

        if (order.getPaymentStatus()
                == OrderPaymentStatus.PAID) {

            throw new BusinessException(
                    "Payment has already been completed for this order.");
        }
    }

    /**
     * Validate selected gateway.
     */
    public void validateGateway(PaymentGateway gateway) {

        if (gateway == null) {
            throw new BusinessException("Payment gateway is required.");
        }
    }

    /**
     * Validate payment status transition.
     */
    public void validatePaymentStatusTransition(
            PaymentStatus current,
            PaymentStatus next) {

        if (current == next) {
            return;
        }

        switch (current) {

            case INITIATED -> {

                if (next != PaymentStatus.PENDING
                        && next != PaymentStatus.CANCELLED
                        && next != PaymentStatus.FAILED) {

                    throw new BusinessException(
                            "Invalid payment status transition.");
                }
            }

            case PENDING -> {

                if (next != PaymentStatus.PROCESSING
                        && next != PaymentStatus.SUCCESS
                        && next != PaymentStatus.FAILED
                        && next != PaymentStatus.CANCELLED) {

                    throw new BusinessException(
                            "Invalid payment status transition.");
                }
            }

            case PROCESSING -> {

                if (next != PaymentStatus.SUCCESS
                        && next != PaymentStatus.FAILED) {

                    throw new BusinessException(
                            "Invalid payment status transition.");
                }
            }

            case SUCCESS -> {

                if (next != PaymentStatus.REFUNDED
                        && next != PaymentStatus.PARTIALLY_REFUNDED) {

                    throw new BusinessException(
                            "Invalid payment status transition.");
                }
            }

            case FAILED,
                 CANCELLED,
                 REFUNDED,
                 PARTIALLY_REFUNDED ->

                    throw new BusinessException(
                            "Payment status cannot be changed.");
        }
    }

    /**
     * Validate refund request.
     */
    public void validateRefund(
            Payment payment,
            BigDecimal refundAmount) {

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {

            throw new BusinessException(
                    "Only successful payments can be refunded.");
        }

        BigDecimal refundedAmount = payment.getRefundedAmount() == null
                ? BigDecimal.ZERO
                : payment.getRefundedAmount();

        BigDecimal availableAmount =
                payment.getAmount().subtract(refundedAmount);

        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Refund amount must be greater than zero.");
        }

        if (refundAmount.compareTo(availableAmount) > 0) {
            throw new BusinessException(
                    "Refund amount exceeds available refundable amount.");
        }
    }

    /**
     * Validate payment amount.
     */
    public void validatePaymentAmount(
            BigDecimal orderAmount,
            BigDecimal paymentAmount) {

        if (orderAmount.compareTo(paymentAmount) != 0) {

            throw new BusinessException(
                    "Payment amount mismatch.");
        }
    }
}