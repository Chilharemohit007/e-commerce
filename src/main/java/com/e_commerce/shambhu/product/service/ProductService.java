package com.e_commerce.shambhu.product.service;

import com.e_commerce.shambhu.product.dto.request.CreateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdatePriceRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateStockRequest;
import com.e_commerce.shambhu.product.dto.response.ProductDetailsResponse;
import com.e_commerce.shambhu.product.dto.response.ProductResponse;
import com.e_commerce.shambhu.product.dto.response.ProductSummaryResponse;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    /**
     * Creates a new product.
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Updates an existing product.
     */
    ProductResponse updateProduct(
            Long productId,
            UpdateProductRequest request
    );

    /**
     * Soft deletes a product.
     */
    void deleteProduct(Long productId);

    /**
     * Returns product by id.
     */
    ProductDetailsResponse getProductById(Long productId);

    /**
     * Returns paginated products.
     */
    Page<ProductSummaryResponse> getAllProducts(
            Pageable pageable
    );

    /**
     * Searches products by keyword.
     */
    Page<ProductSummaryResponse> searchProducts(
            String keyword,
            Pageable pageable
    );

    /**
     * Returns products of a category.
     */
    Page<ProductSummaryResponse> getProductsByCategory(
            Long categoryId,
            Pageable pageable
    );

    /**
     * Returns products by status.
     */
    Page<ProductSummaryResponse> getProductsByStatus(
            ProductStatus status,
            Pageable pageable
    );

    /**
     * Updates product status.
     */
    ProductResponse updateProductStatus(
            Long productId,
            ProductStatus status
    );

    /**
     * Updates inventory.
     */
    ProductResponse updateStock(
            Long productId,
            UpdateStockRequest request
    );

    /**
     * Updates pricing.
     */
    ProductResponse updatePrice(
            Long productId,
            UpdatePriceRequest request
    );
}