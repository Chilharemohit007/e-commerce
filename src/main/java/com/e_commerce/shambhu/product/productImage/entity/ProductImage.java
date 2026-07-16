package com.e_commerce.shambhu.product.productImage.entity;

import com.e_commerce.shambhu.common.entity.Auditable;
import com.e_commerce.shambhu.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "product_images",
    indexes = {
        @Index(name = "idx_product_image_product", columnList = "product_id"),
            @Index(name = "idx_product_image_primary", columnList = "primary_image"),
            @Index(name = "idx_product_image_deleted", columnList = "deleted"),
            @Index(name = "idx_product_image_display_order", columnList = "display_order")
    }
)
public class ProductImage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
    * Product to which this image belongs.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Relative or absolute URL of the image.
     */
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    /**
     * Stored file name on disk/cloud.
     */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /**
     * MIME type.
     * Example: image/jpeg
     */
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    /**
     * File size in bytes.
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * Image order displayed on UI.
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    /**
     * Indicates whether this is the primary image.
     */
    @Column(name = "primary_image", nullable = false)
    private Boolean primaryImage = Boolean.FALSE;

    /**
     * Soft delete flag.
     */
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
}
