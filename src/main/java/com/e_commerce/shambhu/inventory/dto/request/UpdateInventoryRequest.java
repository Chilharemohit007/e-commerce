package com.e_commerce.shambhu.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInventoryRequest {

    @NotNull(message = "Minimum stock level is required.")
    @PositiveOrZero(message = "Minimum stock level cannot be negative.")
    private Integer minimumStockLevel;

    @NotNull(message = "Maximum stock level is required.")
    @Positive(message = "Maximum stock level must be greater than zero.")
    private Integer maximumStockLevel;

    @NotNull(message = "Reorder level is required.")
    @PositiveOrZero(message = "Reorder level cannot be negative.")
    private Integer reorderLevel;

    @NotNull(message = "Inventory status is required.")
    private Boolean active;
}
