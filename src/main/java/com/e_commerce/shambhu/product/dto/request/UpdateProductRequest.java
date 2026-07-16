package com.e_commerce.shambhu.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequest {

    /**
     * Product name.
     */
    @NotBlank(message = "Product name is required.")
    @Size(max = 150, message = "Product name cannot exceed 150 characters.")
    private String name;

    /**
     * SKU.
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
     * Category ID.
     */
    @NotNull(message = "Category is required.")
    @Positive(message = "Category ID must be greater than zero.")
    private Long categoryId;

    /**
     * Brand.
     */
    @Size(max = 100, message = "Brand cannot exceed 100 characters.")
    private String brand;

    /**
     * Selling price.
     */
    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private BigDecimal price;

    /**
     * Discount price.
     */
    @DecimalMin(value = "0.0", message = "Discount price cannot be negative.")
    private BigDecimal discountPrice;

    /**
     * Cost price.
     */
    @DecimalMin(value = "0.0", message = "Cost price cannot be negative.")
    private BigDecimal costPrice;

    /**
     * Stock quantity.
     */
    @PositiveOrZero(message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    /**
     * Minimum stock level.
     */
    @PositiveOrZero(message = "Minimum stock level cannot be negative.")
    private Integer minimumStockLevel;

    /**
     * Featured product.
     */
    private Boolean featured;

    /**
     * Product active status.
     */
    private Boolean active;

    /**
     * Weight.
     */
    @PositiveOrZero(message = "Weight cannot be negative.")
    private BigDecimal weight;

    /**
     * Length.
     */
    @PositiveOrZero(message = "Length cannot be negative.")
    private BigDecimal length;

    /**
     * Width.
     */
    @PositiveOrZero(message = "Width cannot be negative.")
    private BigDecimal width;

    /**
     * Height.
     */
    @PositiveOrZero(message = "Height cannot be negative.")
    private BigDecimal height;
}