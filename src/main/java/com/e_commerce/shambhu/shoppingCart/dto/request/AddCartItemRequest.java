package com.e_commerce.shambhu.shoppingCart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartItemRequest {

    /**
     * Product ID
     */
    @NotNull(message = "Product ID is required.")
    private Long productId;

    /**
     * Quantity
     */
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;

}
