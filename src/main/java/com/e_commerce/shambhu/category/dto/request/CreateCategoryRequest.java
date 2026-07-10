package com.e_commerce.shambhu.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required.")
    @Size(
            min = 3,
            max = 150,
            message = "Category name must be between 3 and 150 characters."
    )
    private String name;

    @Size(
            max = 1000,
            message = "Description must not exceed 1000 characters."
    )
    private String description;

    @Size(
            max = 500,
            message = "Image URL must not exceed 500 characters."
    )
    private String imageUrl;

    /**
     * Null indicates a root category.
     */
    private Long parentCategoryId;

    @PositiveOrZero(
            message = "Display order must be zero or greater."
    )
    private Integer displayOrder = 0;

    /**
     * Defaults to true if not provided.
     */
    private Boolean active = true;
}