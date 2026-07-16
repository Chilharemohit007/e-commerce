package com.e_commerce.shambhu.inventory.repository;

import com.e_commerce.shambhu.inventory.entity.Inventory;
import com.e_commerce.shambhu.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    @EntityGraph(attributePaths = "product")
    Optional<Inventory> findByProductAndDeletedFalse(Product product);

    @EntityGraph(attributePaths = "product")
    Optional<Inventory> findByProductIdAndDeletedFalse(Long productId);

    boolean existsByProduct(Product product);

    Page<Inventory> findByDeletedFalse(Pageable pageable);

    List<Inventory> findByAvailableQuantityLessThanAndDeletedFalse(
            Integer quantity
    );

    List<Inventory> findByAvailableQuantityGreaterThanAndDeletedFalse(
            Integer quantity
    );

    List<Inventory> findByReorderLevelGreaterThanEqualAndDeletedFalse(
            Integer availableQuantity
    );

}
