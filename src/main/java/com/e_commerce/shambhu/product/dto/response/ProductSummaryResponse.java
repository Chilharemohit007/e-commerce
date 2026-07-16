package com.e_commerce.shambhu.product.dto.response;

import com.e_commerce.shambhu.product.enums.ProductStatus;

import java.math.BigDecimal;

public class ProductSummaryResponse {
    private Long id;

    private String name;

    private String slug;

    private String brand;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private ProductStatus status;

    private Integer stockQuantity;

    private Boolean featured;
}
