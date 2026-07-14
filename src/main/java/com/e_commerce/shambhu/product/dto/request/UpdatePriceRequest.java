package com.e_commerce.shambhu.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePriceRequest {

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount price cannot be negative.")
    private BigDecimal discountPrice;

    @DecimalMin(value = "0.0", message = "Cost price cannot be negative.")
    private BigDecimal costPrice;

}