package com.e_commerce.shambhu.product.repository;

import com.e_commerce.shambhu.category.entity.Category;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    /**
     * Find product by id (excluding soft deleted).
     */
    Optional<Product> findByIdAndDeletedFalse(Long id);

    /**
     * Find by slug.
     */
    Optional<Product> findBySlugAndDeletedFalse(String slug);

    /**
     * Find by SKU.
     */
    Optional<Product> findBySkuAndDeletedFalse(String sku);

    /**
     * Check duplicate slug.
     */
    boolean existsBySlugAndDeletedFalse(String slug);

    /**
     * Check duplicate SKU.
     */
    boolean existsBySkuAndDeletedFalse(String sku);

    /**
     * Check duplicate name.
     */
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    /**
     * Find products by category.
     */
    Page<Product> findByCategoryAndDeletedFalse(
            Category category,
            Pageable pageable
    );

    /**
     * Find products by status.
     */
    Page<Product> findByStatusAndDeletedFalse(
            ProductStatus status,
            Pageable pageable
    );

    /**
     * Find featured products.
     */
    Page<Product> findByFeaturedTrueAndDeletedFalse(
            Pageable pageable
    );

    /**
     * Find active products.
     */
    Page<Product> findByActiveTrueAndDeletedFalse(
            Pageable pageable
    );

    /**
     * Keyword search.
     */
    Page<Product> findByNameContainingIgnoreCaseAndDeletedFalse(
            String keyword,
            Pageable pageable
    );

    /**
     * Fetch category eagerly to avoid N+1.
     */
    @EntityGraph(attributePaths = "category")
    Optional<Product> findWithCategoryByIdAndDeletedFalse(Long id);

    /**
     * Fetch all active products.
     */
    @EntityGraph(attributePaths = "category")
    Page<Product> findByDeletedFalse(Pageable pageable);

    /**
     * Low stock products.
     */
    List<Product> findByStockQuantityLessThanEqualAndDeletedFalse(
            Integer quantity
    );

    /**
     * Out of stock products.
     */
    List<Product> findByStockQuantityAndDeletedFalse(
            Integer quantity
    );
}