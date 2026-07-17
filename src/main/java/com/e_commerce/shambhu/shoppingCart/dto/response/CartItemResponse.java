package com.e_commerce.shambhu.shoppingCart.cart.dto.response;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    /**
     * Cart Item ID
     */
    private Long id;

    /**
     * Product ID
     */
    private Long productId;

    /**
     * Product Name
     */
    private String productName;

    /**
     * Product SKU
     */
    private String productSku;

    /**
     * Product Slug
     */
    private String productSlug;

    /**
     * Primary Product Image
     */
    private String primaryImageUrl;

    /**
     * Quantity
     */
    private Integer quantity;

    /**
     * Unit Price
     */
    private BigDecimal unitPrice;

    /**
     * Discount Price Per Unit
     */
    private BigDecimal discountPrice;

    /**
     * Total Price
     */
    private BigDecimal totalPrice;

}
