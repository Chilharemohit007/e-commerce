package com.e_commerce.shambhu.product.productImage.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String imageUrl;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private Integer displayOrder;

    private Boolean primaryImage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
