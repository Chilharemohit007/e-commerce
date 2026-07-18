package com.e_commerce.shambhu.payment.entity;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_order", columnList = "order_id"),
                @Index(name = "idx_payment_reference", columnList = "payment_reference"),
                @Index(name = "idx_payment_status", columnList = "payment_status"),
                @Index(name = "idx_gateway_transaction", columnList = "gateway_transaction_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_reference",
                        columnNames = "payment_reference"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_order")
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_user")
    )
    private User user;

    @Column(
            name = "payment_reference",
            nullable = false,
            unique = true,
            length = 50
    )
    private String paymentReference;

    @Column(
            name = "gateway_transaction_id",
            length = 150
    )
    private String gatewayTransactionId;

    @Column(
            name = "gateway_order_id",
            length = 150
    )
    private String gatewayOrderId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentGateway paymentGateway;

    @Lob
    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(length = 500)
    private String failureReason;

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    @Column(length = 500)
    private String remarks;


}
