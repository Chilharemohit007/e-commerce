package com.e_commerce.shambhu.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    /**
     * Product name.
     */
    @NotBlank(message = "Product name is required.")
    @Size(max = 150, message = "Product name cannot exceed 150 characters.")
    private String name;

    /**
     * Stock Keeping Unit.
     */
    @NotBlank(message = "SKU is required.")
    @Size(max = 100, message = "SKU cannot exceed 100 characters.")
    private String sku;

    /**
     * Short description.
     */
    @Size(max = 500, message = "Short description cannot exceed 500 characters.")
    private String shortDescription;

    /**
     * Detailed description.
     */
    private String description;

    /**
     * Category Id.
     */
    @NotNull(message = "Category is required.")
    @Positive(message = "Category Id must be positive.")
    private Long categoryId;

    /**
     * Brand name.
     */
    @Size(max = 100, message = "Brand cannot exceed 100 characters.")
    private String brand;

    /**
     * Selling price.
     */
    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Price must be greater than zero.")
    private BigDecimal price;

    /**
     * Discount price.
     */
    @DecimalMin(value = "0.0",
            message = "Discount price cannot be negative.")
    private BigDecimal discountPrice;

    /**
     * Cost price.
     */
    @DecimalMin(value = "0.0",
            message = "Cost price cannot be negative.")
    private BigDecimal costPrice;

    /**
     * Available stock.
     */
    @NotNull(message = "Stock quantity is required.")
    @PositiveOrZero(message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    /**
     * Low stock threshold.
     */
    @PositiveOrZero(message = "Minimum stock level cannot be negative.")
    private Integer minimumStockLevel;

    /**
     * Featured product.
     */
    private Boolean featured = Boolean.FALSE;

    /**
     * Active status.
     */
    private Boolean active = Boolean.TRUE;

    /**
     * Product weight.
     */
    @PositiveOrZero(message = "Weight cannot be negative.")
    private BigDecimal weight;

    /**
     * Product length.
     */
    @PositiveOrZero(message = "Length cannot be negative.")
    private BigDecimal length;

    /**
     * Product width.
     */
    @PositiveOrZero(message = "Width cannot be negative.")
    private BigDecimal width;

    /**
     * Product height.
     */
    @PositiveOrZero(message = "Height cannot be negative.")
    private BigDecimal height;
}
