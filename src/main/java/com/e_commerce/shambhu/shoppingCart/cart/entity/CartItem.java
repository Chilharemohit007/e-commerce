package com.e_commerce.shambhu.shoppingCart.cart.entity;

import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "cart_items",
        indexes = {
                @Index(name = "idx_cart_item_cart", columnList = "cart_id"),
                @Index(name = "idx_cart_item_product", columnList = "product_id"),
                @Index(name = "idx_cart_item_deleted", columnList = "deleted")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_product",
                        columnNames = {"cart_id", "product_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent Cart
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cart_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_item_cart")
    )
    private Cart cart;

    /**
     * Product
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_item_product")
    )
    private Product product;

    /**
     * Quantity
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Unit Price
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Discount Price Per Unit
     */
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal discountPrice = BigDecimal.ZERO;

    /**
     * Total Price
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Product Name Snapshot
     */
    @Column(nullable = false, length = 200)
    private String productName;

    /**
     * Product SKU Snapshot
     */
    @Column(nullable = false, length = 100)
    private String productSku;

    /**
     * Primary Image Snapshot
     */
    @Column(length = 500)
    private String primaryImageUrl;

    /**
     * Soft Delete
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

}