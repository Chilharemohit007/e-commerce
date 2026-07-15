package com.e_commerce.shambhu.inventory.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private Integer minimumStockLevel;

    private Integer maximumStockLevel;

    private Integer reorderLevel;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
