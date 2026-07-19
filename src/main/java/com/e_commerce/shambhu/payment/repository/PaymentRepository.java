package com.e_commerce.shambhu.payment.repository;

import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import com.e_commerce.shambhu.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    boolean existsByPaymentReference(String paymentReference);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    List<Payment> findByOrder(Order order);

    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @EntityGraph(attributePaths = {
            "order",
            "user"
    })
    Optional<Payment> findDetailedById(Long id);

    @Query("""
       SELECT COALESCE(SUM(p.amount), 0)
       FROM Payment p
       WHERE p.paymentStatus = com.e_commerce.shambhu.payment.enums.PaymentStatus.SUCCESS
       """)
    BigDecimal getTotalRevenue();

    @Query("""
       SELECT COALESCE(SUM(p.amount), 0)
       FROM Payment p
       WHERE p.paymentStatus = com.e_commerce.shambhu.payment.enums.PaymentStatus.SUCCESS
       AND p.createdAt BETWEEN :startDate AND :endDate
       """)
    BigDecimal getRevenueBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    long countByPaymentStatus(OrderPaymentStatus paymentStatus);

    List<Payment> findByPaymentStatus(OrderPaymentStatus paymentStatus);

    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.user.id = :userId
    AND LOWER(p.paymentReference)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Payment> searchCustomerPayments(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE LOWER(p.paymentReference)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Payment> searchPayments(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.paymentStatus =
    com.e_commerce.shambhu.payment.enums.PaymentStatus.SUCCESS
    AND p.refundedAmount < p.amount
    """)
    List<Payment> findRefundablePayments();


}
