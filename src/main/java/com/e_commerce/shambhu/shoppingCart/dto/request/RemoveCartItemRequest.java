package com.e_commerce.shambhu.shoppingCart.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveCartItemRequest {

    /**
     * Cart Item ID
     */
    @NotNull(message = "Cart item ID is required.")
    private Long cartItemId;

}
