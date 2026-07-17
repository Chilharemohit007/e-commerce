package com.e_commerce.shambhu.order.repository;

import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.entity.OrderItem;
import com.e_commerce.shambhu.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = {
            "product"
    })
    List<OrderItem> findByOrderAndDeletedFalse(
            Order order);

    @EntityGraph(attributePaths = {
            "order"
    })
    Page<OrderItem> findByProductAndDeletedFalse(
            Product product,
            Pageable pageable);

    long countByOrder(Order order);

    void deleteAllByOrder(Order order);
}
