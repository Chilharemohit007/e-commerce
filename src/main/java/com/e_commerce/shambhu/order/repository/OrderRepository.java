package com.e_commerce.shambhu.order.repository;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserAndOrderStatus(
            User user,
            OrderStatus orderStatus,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "orderItems"
    })
    Optional<Order> findWithOrderItemsById(Long id);

    @EntityGraph(attributePaths = {
            "user",
            "shippingAddress",
            "billingAddress"
    })
    Optional<Order> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = {
            "user",
            "address",
            "orderItems"
    })
    Optional<Order> findDetailedById(Long id);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByOrderStatus(
            OrderStatus status,
            Pageable pageable
    );

    Page<Order> findByPaymentStatus(
            OrderPaymentStatus paymentStatus,
            Pageable pageable
    );

    List<Order> findByPlacedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Order> findByUserAndPlacedAtBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    //GET /admin/dashboard/revenue
    @Query("""
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM Order o
       WHERE o.orderStatus = 'DELIVERED'
       """)
    BigDecimal getTotalRevenue();

    @Query("""
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM Order o
       WHERE o.orderStatus = 'DELIVERED'
       AND o.placedAt BETWEEN :startDate AND :endDate
       """)
    BigDecimal getRevenueBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    long countByOrderStatus(OrderStatus status);

    long countByUser(User user);

    Page<Order> findAllByOrderByPlacedAtDesc(Pageable pageable);

    @Query("""
    SELECT o
    FROM Order o
    WHERE LOWER(o.orderNumber)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
    SELECT o
    FROM Order o
    WHERE o.user.id = :userId
    AND LOWER(o.orderNumber)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Order> searchCustomerOrders(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

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
            OrderPaymentStatus paymentStatus,
            Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);

    long countByUserAndDeletedFalse(User user);
}
