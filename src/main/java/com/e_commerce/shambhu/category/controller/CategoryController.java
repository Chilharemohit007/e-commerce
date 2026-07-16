package com.e_commerce.shambhu.category.controller;

import com.e_commerce.shambhu.category.dto.request.CreateCategoryRequest;
import com.e_commerce.shambhu.category.dto.request.UpdateCategoryRequest;
import com.e_commerce.shambhu.category.dto.response.CategoryResponse;
import com.e_commerce.shambhu.category.dto.response.CategoryTreeResponse;
import com.e_commerce.shambhu.category.service.CategoryService;
import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    /**
     * Create Category
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseBuilder.buildSuccess(
                "Category created successfully.",
                HttpStatus.CREATED,
                response
        );
    }

    /**
     * Update Category
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        CategoryResponse response =
                categoryService.updateCategory(id, request);

        return ResponseBuilder.buildSuccess(
                "Category updated successfully.",
                HttpStatus.OK,
                response
        );
    }

    /**
     * Delete Category (Soft Delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseBuilder.buildSuccess(
                "Category deleted successfully.",
                HttpStatus.OK,
                null
        );
    }

    /**
     * Get Category By Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id) {

        CategoryResponse response =
                categoryService.getCategoryById(id);

        return ResponseBuilder.buildSuccess(
                "Category fetched successfully.",
                HttpStatus.OK,
                response
        );
    }

    /**
     * Get All Categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        return ResponseBuilder.buildSuccess(
                "Categories fetched successfully.",
                HttpStatus.OK,
                response
        );
    }

    /**
     * Category Tree
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getCategoryTree() {

        List<CategoryTreeResponse> response =
                categoryService.getCategoryTree();

        return ResponseBuilder.buildSuccess(
                "Category tree fetched successfully.",
                HttpStatus.OK,
                response
        );
    }

    /**
     * Search Categories
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(
            @RequestParam String keyword) {

        List<CategoryResponse> response =
                categoryService.searchCategories(keyword);

        return ResponseBuilder.buildSuccess(
                "Search completed successfully.",
                HttpStatus.OK,
                response
        );
    }

    /**
     * Activate / Deactivate Category
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        CategoryResponse response =
                categoryService.updateCategoryStatus(id, active);

        return ResponseBuilder.buildSuccess(
                "Category status updated successfully.",
                HttpStatus.OK,
                response
        );
    }
}