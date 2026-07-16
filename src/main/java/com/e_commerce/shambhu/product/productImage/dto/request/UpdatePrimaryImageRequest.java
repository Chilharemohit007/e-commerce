package com.e_commerce.shambhu.product.productImage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePrimaryImageRequest {

    @NotNull(message = "Primary image flag is required.")
    private Boolean primaryImage;
}