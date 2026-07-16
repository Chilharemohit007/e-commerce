package com.e_commerce.shambhu.inventory.entity;

import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_inventory_product",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_inventory_deleted",
                        columnList = "deleted"
                ),
                @Index(
                        name = "idx_inventory_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One inventory per product.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inventory_product")
    )
    private Product product;

    /**
     * Quantity currently available for sale.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    /**
     * Quantity reserved during checkout.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    /**
     * Alert threshold.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer minimumStockLevel = 0;

    /**
     * Maximum stock allowed.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer maximumStockLevel = 1000;

    /**
     * Reorder threshold.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer reorderLevel = 10;

    /**
     * Used by JPA for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * Inventory active flag.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Soft delete flag.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}