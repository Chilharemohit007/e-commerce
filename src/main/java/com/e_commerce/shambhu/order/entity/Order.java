package com.e_commerce.shambhu.order.entity;

import com.e_commerce.shambhu.address.entity.Address;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import com.e_commerce.shambhu.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_user", columnList = "user_id"),
                @Index(name = "idx_order_status", columnList = "order_status"),
                @Index(name = "idx_order_payment_status", columnList = "payment_status"),
                @Index(name = "idx_order_deleted", columnList = "deleted")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_order_number",
                        columnNames = "order_number"
                )
        }
)
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer who placed the order
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_user")
    )
    private User user;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    /**
     * Unique Order Number
     */
    @Column(
            name = "order_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String orderNumber;

    /**
     * Order Status
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "order_status",
            nullable = false,
            length = 30
    )
    private OrderStatus orderStatus =  OrderStatus.PENDING;

    /**
     * Payment Status
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 30
    )
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * Payment Method
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    /**
     * Shipping Address
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "shipping_address_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_shipping_address")
    )
    private Address shippingAddress;

    /**
     * Billing Address
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "billing_address_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_billing_address")
    )
    private Address billingAddress;

    /**
     * Ordered Items
     */
    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Order Summary
     */
    @Column(name = "total_items", nullable = false)
    @Builder.Default
    private Integer totalItems = 0;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(
            name = "tax_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(
            name = "shipping_charge",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @Builder.Default
    private BigDecimal shippingCharge = BigDecimal.ZERO;

    @Column(
            name = "payable_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @Builder.Default
    private BigDecimal payableAmount = BigDecimal.ZERO;

    /**
     * Customer Instructions
     */
    @Column(name = "customer_note", length = 500)
    private String customerNote;

    /**
     * Soft Delete
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    /**
     * Optimistic Locking
     */
    @Version
    private Long version;
}