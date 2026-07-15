package com.e_commerce.shambhu.inventory.dto.response;

import com.e_commerce.shambhu.inventory.enums.MovementType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementResponse {

    private Long id;

    private Long inventoryId;

    private MovementType movementType;

    private Integer quantity;

    private Integer previousQuantity;

    private Integer currentQuantity;

    private Long referenceId;

    private String remarks;

    private LocalDateTime createdAt;

    private String createdBy;
}
