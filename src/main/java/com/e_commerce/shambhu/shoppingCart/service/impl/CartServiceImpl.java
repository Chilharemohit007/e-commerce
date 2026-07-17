package com.e_commerce.shambhu.shoppingCart.service.impl;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.inventory.dto.request.ReserveStockRequest;
import com.e_commerce.shambhu.inventory.service.InventoryService;
import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import com.e_commerce.shambhu.product.productImage.entity.ProductImage;
import com.e_commerce.shambhu.product.repository.ProductRepository;
import com.e_commerce.shambhu.shoppingCart.dto.request.AddCartItemRequest;
import com.e_commerce.shambhu.shoppingCart.dto.request.UpdateCartItemRequest;
import com.e_commerce.shambhu.shoppingCart.dto.response.CartResponse;
import com.e_commerce.shambhu.shoppingCart.dto.response.CartSummaryResponse;
import com.e_commerce.shambhu.shoppingCart.entity.Cart;
import com.e_commerce.shambhu.shoppingCart.entity.CartItem;
import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import com.e_commerce.shambhu.shoppingCart.repository.CartItemRepository;
import com.e_commerce.shambhu.shoppingCart.repository.CartRepository;
import com.e_commerce.shambhu.shoppingCart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final InventoryService inventoryService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getActiveCart() {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Fetching active cart for userId={}",
                user.getId()
        );

        Cart cart = cartRepository
                .findByUserAndStatusAndDeletedFalse(
                        user,
                        CartStatus.ACTIVE
                )
                .orElseGet(() -> createNewCart(user));

        recalculateCart(cart);

        LOGGER.info(
                "Active cart retrieved successfully. cartId={}",
                cart.getId()
        );

        return mapToCartResponse(cart);
    }

    private User getAuthenticatedUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.", null, null));
    }

    private Cart createNewCart(User user) {

        Cart cart = Cart.builder()
                .user(user)
                .status(CartStatus.ACTIVE)
                .totalItems(0)
                .totalAmount(BigDecimal.ZERO)
                .totalDiscount(BigDecimal.ZERO)
                .payableAmount(BigDecimal.ZERO)
                .deleted(false)
                .build();

        return cartRepository.save(cart);
    }

    private void recalculateCart(Cart cart) {

        List<CartItem> items =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {

            totalItems += item.getQuantity();

            totalAmount =
                    totalAmount.add(
                            item.getUnitPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    );

            totalDiscount =
                    totalDiscount.add(
                            item.getDiscountPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
        }

        cart.setTotalItems(totalItems);
        cart.setTotalAmount(totalAmount);
        cart.setTotalDiscount(totalDiscount);
        cart.setPayableAmount(totalAmount.subtract(totalDiscount));
    }

    private CartResponse mapToCartResponse(Cart cart) {

        return modelMapper.map(
                cart,
                CartResponse.class
        );
    }

    @Override
    public CartResponse addItem(AddCartItemRequest request) {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Adding product {} to cart of user {}",
                request.getProductId(),
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        Product product = productRepository
                .findByIdAndDeletedFalse(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found.", null, null));

        validateProduct(product);

        validateInventory(
                product.getId(),
                request.getQuantity()
        );

        CartItem cartItem = cartItemRepository
                .findByCartAndProductAndDeletedFalse(cart, product)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity() + request.getQuantity();

            validateInventory(
                    product.getId(),
                    newQuantity
            );

            cartItem.setQuantity(newQuantity);

            updateCartItemPricing(cartItem);

        } else {

            cartItem = buildCartItem(
                    cart,
                    product,
                    request.getQuantity()
            );

            cart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        recalculateCart(cart);

        cartRepository.save(cart);

        LOGGER.info(
                "Product added successfully. cartId={}, productId={}",
                cart.getId(),
                product.getId()
        );

        return mapToCartResponse(cart);
    }

    private Cart getOrCreateActiveCart(User user) {

        return cartRepository
                .findByUserAndStatusAndDeletedFalse(
                        user,
                        CartStatus.ACTIVE
                )
                .orElseGet(() -> createNewCart(user));
    }

    private void validateProduct(Product product) {

        if (Boolean.TRUE.equals(product.getDeleted())) {
            throw new BusinessException("Product is deleted.");
        }

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Product is inactive.");
        }

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException(
                    "Product is not available."
            );
        }
    }

    private void validateInventory(
            Long productId,
            Integer quantity) {

        if (!inventoryService.isStockAvailable(
                productId,
                quantity
        )) {

            throw new BusinessException(
                    "Insufficient stock available."
            );
        }
    }

    private CartItem buildCartItem(
            Cart cart,
            Product product,
            Integer quantity) {

        BigDecimal sellingPrice = product.getPrice();

        BigDecimal discountPrice =
                product.getDiscountPrice() == null
                        ? BigDecimal.ZERO
                        : sellingPrice.subtract(
                        product.getDiscountPrice()
                );

        BigDecimal payablePrice =
                sellingPrice.subtract(discountPrice);

        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .unitPrice(sellingPrice)
                .discountPrice(discountPrice)
                .totalPrice(
                        payablePrice.multiply(
                                BigDecimal.valueOf(quantity)
                        )
                )
                .productName(product.getName())
                .productSku(product.getSku())
                //.productSlug(product.getSlug())
                .primaryImageUrl(
                        getPrimaryImage(product)
                )
                .deleted(false)
                .build();
    }

    private void updateCartItemPricing(
            CartItem item) {

        BigDecimal payablePrice =
                item.getUnitPrice()
                        .subtract(item.getDiscountPrice());

        item.setTotalPrice(
                payablePrice.multiply(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        )
                )
        );
    }

    private String getPrimaryImage(Product product) {

        return product.getImages()
                .stream()
                .filter(ProductImage::getPrimaryImage)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }

    @Override
    public CartResponse updateItem(
            Long itemId,
            UpdateCartItemRequest request) {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Updating cart item. itemId={}, userId={}",
                itemId,
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found.", null, null
                        ));

        validateCartOwnership(cart, cartItem);

        validateInventory(
                cartItem.getProduct().getId(),
                request.getQuantity()
        );

        cartItem.setQuantity(request.getQuantity());

        updateCartItemPricing(cartItem);

        cartItemRepository.save(cartItem);

        recalculateCart(cart);

        cartRepository.save(cart);

        LOGGER.info(
                "Cart item updated successfully. itemId={}, quantity={}",
                itemId,
                request.getQuantity()
        );

        return mapToCartResponse(cart);
    }

    private void validateCartOwnership(
            Cart cart,
            CartItem cartItem) {

        if (!cartItem.getCart().getId().equals(cart.getId())) {

            throw new BusinessException(
                    "Cart item does not belong to the active cart."
            );
        }

        if (Boolean.TRUE.equals(cartItem.getDeleted())) {

            throw new BusinessException(
                    "Cart item has been removed."
            );
        }
    }

    @Override
    public void removeItem(Long itemId) {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Removing cart item. itemId={}, userId={}",
                itemId,
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found.", null, null
                        ));

        validateCartOwnership(cart, cartItem);

        if (Boolean.TRUE.equals(cartItem.getDeleted())) {

            throw new BusinessException(
                    "Cart item has already been removed."
            );
        }

        cartItem.setDeleted(true);

        cartItemRepository.save(cartItem);

        recalculateCart(cart);

        cartRepository.save(cart);

        LOGGER.info(
                "Cart item removed successfully. itemId={}",
                itemId
        );
    }

    @Override
    public void clearCart() {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Clearing cart for userId={}",
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        List<CartItem> cartItems =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        if (cartItems.isEmpty()) {

            LOGGER.info(
                    "Cart is already empty. cartId={}",
                    cart.getId()
            );

            return;
        }

        cartItems.forEach(item -> item.setDeleted(true));

        cartItemRepository.saveAll(cartItems);

        resetCartTotals(cart);

        cartRepository.save(cart);

        LOGGER.info(
                "Cart cleared successfully. cartId={}, itemsRemoved={}",
                cart.getId(),
                cartItems.size()
        );
    }

    private void resetCartTotals(Cart cart) {

        cart.setTotalItems(0);

        cart.setTotalAmount(BigDecimal.ZERO);

        cart.setTotalDiscount(BigDecimal.ZERO);

        cart.setPayableAmount(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary() {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Fetching cart summary for userId={}",
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        recalculateCart(cart);

        CartSummaryResponse response = mapToCartSummaryResponse(cart);

        LOGGER.info(
                "Cart summary retrieved successfully. cartId={}",
                cart.getId()
        );

        return response;
    }

    private CartSummaryResponse mapToCartSummaryResponse(
            Cart cart) {

        return CartSummaryResponse.builder()
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .totalDiscount(cart.getTotalDiscount())
                .payableAmount(cart.getPayableAmount())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse checkout() {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Checkout initiated for userId={}",
                user.getId()
        );

        Cart cart = getOrCreateActiveCart(user);

        validateCartForCheckout(cart);

        List<CartItem> cartItems =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        validateProducts(cartItems);

        reserveInventory(cartItems);

        Order order = createOrder(user, cart, cartItems);

        cart.setStatus(CartStatus.CHECKED_OUT);

        cartRepository.save(cart);

        createNewCart(user);

        LOGGER.info(
                "Checkout completed successfully. orderId={}",
                order.getId()
        );

        return mapToOrderResponse(order);
    }

    private void validateCartForCheckout(Cart cart) {

        List<CartItem> items =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        if (items.isEmpty()) {

            throw new BusinessException(
                    "Shopping cart is empty."
            );
        }
    }

    private void validateProducts(
            List<CartItem> cartItems) {

        for (CartItem item : cartItems) {

            validateProduct(item.getProduct());
        }
    }

    private void reserveInventory(
            List<CartItem> cartItems) {

        for (CartItem item : cartItems) {

            inventoryService.reserveStock(
                    item.getProduct().getId(),
                    new ReserveStockRequest(
                            item.getQuantity()
                    )
            );
        }
    }

    private Order createOrder(User user, Cart cart, List<CartItem> cartItems) {
        throw new UnsupportedOperationException(
                "Order module not implemented yet."
        );
    }

    private OrderResponse mapToOrderResponse(Order order) {
        throw new UnsupportedOperationException(
                "Order module not implemented yet."
        );
    }

}
