package com.e_commerce.shambhu.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveStockRequest {

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;

    private Long referenceId;

    @Size(max = 500)
    private String remarks;
}