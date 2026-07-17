package com.e_commerce.shambhu.shoppingCart.cart.repository;

import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.shoppingCart.cart.entity.Cart;
import com.e_commerce.shambhu.shoppingCart.cart.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<CartItem> findByCartAndDeletedFalse(Cart cart);

    @EntityGraph(attributePaths = "product")
    Optional<CartItem> findByCartAndProductAndDeletedFalse(
            Cart cart,
            Product product
    );

    void deleteByCart(Cart cart);

    long countByCartAndDeletedFalse(Cart cart);

    boolean existsByCartAndProductAndDeletedFalse(
            Cart cart,
            Product product
    );

    void deleteByCartAndProduct(
            Cart cart,
            Product product
    );

    List<CartItem> findByProduct(Product product);
}