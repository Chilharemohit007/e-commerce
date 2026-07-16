package com.e_commerce.shambhu.product.entity;

import com.e_commerce.shambhu.category.entity.Category;
import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_slug", columnList = "slug"),
                @Index(name = "idx_product_sku", columnList = "sku"),
                @Index(name = "idx_product_category", columnList = "category_id"),
                @Index(name = "idx_product_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_slug", columnNames = "slug"),
                @UniqueConstraint(name = "uk_product_sku", columnNames = "sku")
        }
)
@Builder
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ==========================================================
                        BASIC INFORMATION
       ========================================================== */

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 180)
    private String slug;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(length = 500)
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    /* ==========================================================
                          CATEGORY
       ========================================================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category")
    )
    private Category category;

    /* ==========================================================
                          BRAND
       ========================================================== */

    @Column(length = 100)
    private String brand;

    /* ==========================================================
                           PRICING
       ========================================================== */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal costPrice;

    /* ==========================================================
                           INVENTORY
       ========================================================== */

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Integer minimumStockLevel = 0;

    /* ==========================================================
                        PRODUCT DETAILS
       ========================================================== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    /* ==========================================================
                    PHYSICAL DIMENSIONS
       ========================================================== */

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(precision = 10, scale = 2)
    private BigDecimal length;

    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    @Column(precision = 10, scale = 2)
    private BigDecimal height;

    public Product() {
    }
}