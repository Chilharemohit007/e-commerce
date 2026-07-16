package com.e_commerce.shambhu.product.enums;

public enum ProductStatus {

    /**
     * Product is visible and available for purchase.
     */
    ACTIVE,

    /**
     * Product exists but is hidden from customers.
     */
    INACTIVE,

    /**
     * Product is temporarily unavailable because
     * stock quantity has reached zero.
     */
    OUT_OF_STOCK,

    /**
     * Product has been permanently discontinued
     * and will no longer be sold.
     */
    DISCONTINUED
}
