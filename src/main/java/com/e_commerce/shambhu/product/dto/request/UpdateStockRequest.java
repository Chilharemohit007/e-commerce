package com.e_commerce.shambhu.product.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockRequest {

    @PositiveOrZero(message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Minimum stock level cannot be negative.")
    private Integer minimumStockLevel;

}