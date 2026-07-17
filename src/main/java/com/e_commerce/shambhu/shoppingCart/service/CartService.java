package com.e_commerce.shambhu.shoppingCart.service;

import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.shoppingCart.dto.request.AddCartItemRequest;
import com.e_commerce.shambhu.shoppingCart.dto.request.UpdateCartItemRequest;
import com.e_commerce.shambhu.shoppingCart.dto.response.CartResponse;
import com.e_commerce.shambhu.shoppingCart.dto.response.CartSummaryResponse;
import org.springframework.transaction.annotation.Transactional;

public interface CartService {

    /**
     * Returns the active cart of the authenticated customer.
     *
     * @return active shopping cart
     */
    CartResponse getActiveCart();

    /**
     * Adds a product to the customer's cart.
     * If the product already exists, its quantity is increased.
     *
     * @param request add item request
     * @return updated cart
     */
    CartResponse addItem(AddCartItemRequest request);

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param itemId cart item id
     * @param request quantity update request
     * @return updated cart
     */
    CartResponse updateItem(
            Long itemId,
            UpdateCartItemRequest request
    );

    /**
     * Removes a single item from the cart.
     *
     * @param itemId cart item id
     */
    void removeItem(Long itemId);

    /**
     * Removes all items from the customer's cart.
     */
    void clearCart();

    /**
     * Returns a lightweight summary of the cart.
     *
     * @return cart summary
     */
    CartSummaryResponse getCartSummary();

    @Transactional(rollbackFor = Exception.class)
    OrderResponse checkout();

    /**
     * Converts the current cart into an order.
     *
     * @return created order
     */
    //OrderResponse checkout();

}
