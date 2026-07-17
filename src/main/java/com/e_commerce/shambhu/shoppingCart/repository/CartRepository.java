package com.e_commerce.shambhu.shoppingCart.repository;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.shoppingCart.entity.Cart;
import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = "cartItems")
    Optional<Cart> findByUserAndStatusAndDeletedFalse(
            User user,
            CartStatus status
    );

    @EntityGraph(attributePaths = "cartItems")
    Optional<Cart> findByIdAndDeletedFalse(Long id);

    boolean existsByUserAndStatusAndDeletedFalse(
            User user,
            CartStatus status
    );

    Optional<Cart> findByUserAndDeletedFalse(User user);

    List<Cart> findByStatus(CartStatus status);

    List<Cart> findByDeletedFalse();

    long countByStatus(CartStatus status);



    /*If you want the complete cart with items and products in one query, you can use:

        @EntityGraph(attributePaths = {
        "cartItems",
        "cartItems.product"
        })
        Optional<Cart> findByUserAndStatusAndDeletedFalse(
        User user,
        CartStatus status
        );

        This is generally the best choice for GET /cart.*/

}
