package com.e_commerce.shambhu.inventory.entity;

import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.inventory.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventory_movements",
        indexes = {
                @Index(
                        name = "idx_inventory_movement_inventory",
                        columnList = "inventory_id"
                ),
                @Index(
                        name = "idx_inventory_movement_type",
                        columnList = "movement_type"
                ),
                @Index(
                        name = "idx_inventory_movement_reference",
                        columnList = "reference_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Inventory whose stock changed.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "inventory_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_inventory_movement_inventory"
            )
    )
    private Inventory inventory;

    /**
     * Type of inventory movement.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "movement_type",
            nullable = false,
            length = 30
    )
    private MovementType movementType;

    /**
     * Quantity involved in the movement.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Quantity before the movement.
     */
    @Column(nullable = false)
    private Integer previousQuantity;

    /**
     * Quantity after the movement.
     */
    @Column(nullable = false)
    private Integer currentQuantity;

    /**
     * Related entity ID (Order ID, Purchase ID, Return ID, etc.).
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Additional remarks.
     */
    @Column(length = 500)
    private String remarks;
}
