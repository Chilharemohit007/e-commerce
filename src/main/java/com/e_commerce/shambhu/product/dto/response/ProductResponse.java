package com.e_commerce.shambhu.product.dto.response;

import com.e_commerce.shambhu.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {
    private Long id;

    private String name;

    private String slug;

    private String sku;

    private String shortDescription;

    private String description;

    private Long categoryId;

    private String categoryName;

    private String brand;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private BigDecimal costPrice;

    private Integer stockQuantity;

    private Integer minimumStockLevel;

    private ProductStatus status;

    private Boolean featured;

    private Boolean active;

    private BigDecimal weight;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
