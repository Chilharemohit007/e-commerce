package com.e_commerce.shambhu.order.entity;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.customer.entity.Customer;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order",
            indexes = {
                    @Index(name = "", columnList = ""),
                    @Index(name = "", columnList = ""),
                    @Index(name = "", columnList = "")
            },
            uniqueConstraints = {
                    @UniqueConstraint(name = "", columnNames = ""),
                    @UniqueConstraint(name = "", columnNames = "")
            }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_number")
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 30)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "shipping_address", nullable = false)
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "billing_address", nullable = false)
    private Address billingAddress;

    private BigDecimal totalAmount;

    private BigDecimal totalDiscount;

    private BigDecimal shippingCharges;

    private BigDecimal taxAmount;

    private BigDecimal payableAmount;
}
