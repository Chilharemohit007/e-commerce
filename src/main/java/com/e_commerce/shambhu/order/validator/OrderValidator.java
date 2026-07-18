package com.e_commerce.shambhu.order.validator;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import com.e_commerce.shambhu.shoppingCart.entity.Cart;
import com.e_commerce.shambhu.shoppingCart.entity.CartItem;
import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import com.e_commerce.shambhu.shoppingCart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.e_commerce.shambhu.order.service.impl.OrderServiceImpl.LOGGER;

@RequiredArgsConstructor
@Component
public class OrderValidator {

    private final CartItemRepository cartItemRepository;

    public void validateCancellation(Order order) {

        if (order.getOrderStatus() == OrderStatus.SHIPPED
                || order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getOrderStatus() == OrderStatus.DELIVERED) {

            throw new BusinessException(
                    "Order cannot be cancelled.");
        }
    }

    public void validateStatusTransition(
            OrderStatus current,
            OrderStatus next) {

        switch (current) {

            case PENDING -> {
                if (next != OrderStatus.CONFIRMED
                        && next != OrderStatus.CANCELLED) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            case CONFIRMED -> {
                if (next != OrderStatus.PROCESSING) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            case PROCESSING -> {
                if (next != OrderStatus.PACKED) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            case PACKED -> {
                if (next != OrderStatus.SHIPPED) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            case SHIPPED -> {
                if (next != OrderStatus.OUT_FOR_DELIVERY) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            case OUT_FOR_DELIVERY -> {
                if (next != OrderStatus.DELIVERED) {
                    throw new BusinessException(
                            "Invalid order status transition.");
                }
            }

            default ->
                    throw new BusinessException(
                            "Order status cannot be updated.");
        }
    }

    public void validatePaymentTransition(
            PaymentStatus current,
            PaymentStatus next) {

        if (current == PaymentStatus.REFUNDED) {
            throw new BusinessException(
                    "Payment already refunded.");
        }
    }

    public void validateCart(Cart cart) {

        if (cart == null) {
            throw new ResourceNotFoundException(
                    "Shopping cart not found.", null, null
            );
        }

        if (Boolean.TRUE.equals(cart.getDeleted())) {
            throw new BusinessException(
                    "Shopping cart has been deleted."
            );
        }

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new BusinessException(
                    "Only active carts can be checked out."
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    "Shopping cart is empty."
            );
        }

        LOGGER.info(
                "Cart validation completed successfully. CartId={}, ItemCount={}",
                cart.getId(),
                cartItems.size()
        );
    }
}
