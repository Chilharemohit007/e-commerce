package com.e_commerce.shambhu.shoppingCart.entity;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import com.e_commerce.shambhu.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "carts",
        indexes = {
                @Index(name = "idx_cart_user", columnList = "user_id"),
                @Index(name = "idx_cart_status", columnList = "status"),
                @Index(name = "idx_cart_deleted", columnList = "deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cart Owner
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_user")
    )
    private User user;

    /**
     * Cart Items
     */
    @Builder.Default
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Cart Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    /**
     * Total number of items.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalItems = 0;

    /**
     * Gross amount before discounts.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Total discount.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    /**
     * Final payable amount.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal payableAmount = BigDecimal.ZERO;

    /**
     * Soft Delete
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

}