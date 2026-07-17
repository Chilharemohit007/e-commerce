package com.e_commerce.shambhu.order.repository;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "user",
            "shippingAddress",
            "billingAddress"
    })
    Optional<Order> findByIdAndDeletedFalse(Long id);

    Optional<Order> findByOrderNumberAndDeletedFalse(
            String orderNumber);

    @EntityGraph(attributePaths = {
            "shippingAddress",
            "billingAddress"
    })
    Page<Order> findByUserAndDeletedFalse(
            User user,
            Pageable pageable);

    Page<Order> findByStatusAndDeletedFalse(
            OrderStatus status,
            Pageable pageable);

    Page<Order> findByPaymentStatusAndDeletedFalse(
            PaymentStatus paymentStatus,
            Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);

    long countByUserAndDeletedFalse(User user);
}
