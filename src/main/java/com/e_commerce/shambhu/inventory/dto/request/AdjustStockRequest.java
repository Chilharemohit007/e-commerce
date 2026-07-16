package com.e_commerce.shambhu.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustStockRequest {

    @NotNull(message = "Quantity is required.")
    private Integer quantity;

    @NotBlank(message = "Reason is required.")
    @Size(max = 500)
    private String remarks;
}
