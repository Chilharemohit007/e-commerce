package com.e_commerce.shambhu.product.controller;

import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import com.e_commerce.shambhu.product.dto.request.CreateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdatePriceRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateStockRequest;
import com.e_commerce.shambhu.product.dto.response.ProductDetailsResponse;
import com.e_commerce.shambhu.product.dto.response.ProductResponse;
import com.e_commerce.shambhu.product.dto.response.ProductSummaryResponse;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import com.e_commerce.shambhu.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseBuilder.created(
                "Product created successfully.",
                response
        );
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response =
                productService.updateProduct(productId, request);

        return ResponseBuilder.ok(
                "Product updated successfully.",
                response
        );
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId) {

        productService.deleteProduct(productId);

        return ResponseBuilder.ok(
                "Product deleted successfully.",
                null
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailsResponse>> getProductById(
            @PathVariable Long productId) {

        ProductDetailsResponse response =
                productService.getProductById(productId);

        return ResponseBuilder.ok(
                "Product retrieved successfully.",
                response
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getAllProducts(
            Pageable pageable) {

        Page<ProductSummaryResponse> response =
                productService.getAllProducts(pageable);

        return ResponseBuilder.ok(
                "Products retrieved successfully.",
                response
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {

        Page<ProductSummaryResponse> response =
                productService.searchProducts(
                        keyword,
                        pageable
                );

        return ResponseBuilder.ok(
                "Products retrieved successfully.",
                response
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {

        Page<ProductSummaryResponse> response =
                productService.getProductsByCategory(
                        categoryId,
                        pageable
                );

        return ResponseBuilder.ok(
                "Products retrieved successfully.",
                response
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductsByStatus(
            @PathVariable ProductStatus status,
            Pageable pageable) {

        Page<ProductSummaryResponse> response =
                productService.getProductsByStatus(
                        status,
                        pageable
                );

        return ResponseBuilder.ok(
                "Products retrieved successfully.",
                response
        );
    }

    @PatchMapping("/{productId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam ProductStatus status) {

        ProductResponse response =
                productService.updateProductStatus(
                        productId,
                        status
                );

        return ResponseBuilder.ok(
                "Product status updated successfully.",
                response
        );
    }

    @PatchMapping("/{productId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockRequest request) {

        ProductResponse response =
                productService.updateStock(
                        productId,
                        request
                );

        return ResponseBuilder.ok(
                "Product stock updated successfully.",
                response
        );
    }

    @PatchMapping("/{productId}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updatePrice(
            @PathVariable Long productId,
            @Valid @RequestBody UpdatePriceRequest request) {

        ProductResponse response =
                productService.updatePrice(
                        productId,
                        request
                );

        return ResponseBuilder.ok(
                "Product price updated successfully.",
                response
        );
    }

}
