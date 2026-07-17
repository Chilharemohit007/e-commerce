package com.e_commerce.shambhu.shoppingCart.dto.response;

import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    /**
     * Cart ID
     */
    private Long id;

    /**
     * Customer ID
     */
    private Long userId;

    /**
     * Cart Status
     */
    private CartStatus status;

    /**
     * Total Items
     */
    private Integer totalItems;

    /**
     * Gross Amount
     */
    private BigDecimal totalAmount;

    /**
     * Total Discount
     */
    private BigDecimal totalDiscount;

    /**
     * Final Payable Amount
     */
    private BigDecimal payableAmount;

    /**
     * Cart Items
     */
    private List<CartItemResponse> items;

}
