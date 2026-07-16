package com.e_commerce.shambhu.productImage.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductImageRequest {

    private Boolean primaryImage;

    @PositiveOrZero(message = "Display order cannot be negative.")
    private Integer displayOrder;
}
