package com.e_commerce.shambhu.category.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    /**
     * Null indicates a root category.
     */
    private Long parentCategoryId;

    private Integer displayOrder;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}