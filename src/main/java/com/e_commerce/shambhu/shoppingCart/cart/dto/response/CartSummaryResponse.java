package com.e_commerce.shambhu.shoppingCart.cart.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSummaryResponse {

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

}