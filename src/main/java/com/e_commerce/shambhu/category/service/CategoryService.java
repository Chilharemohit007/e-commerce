package com.e_commerce.shambhu.category.service;

import com.e_commerce.shambhu.category.dto.request.CreateCategoryRequest;
import com.e_commerce.shambhu.category.dto.request.UpdateCategoryRequest;
import com.e_commerce.shambhu.category.dto.response.CategoryResponse;
import com.e_commerce.shambhu.category.dto.response.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request category details
     * @return created category
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Updates an existing category.
     *
     * @param id category id
     * @param request updated details
     * @return updated category
     */
    CategoryResponse updateCategory(
            Long id,
            UpdateCategoryRequest request
    );

    /**
     * Soft deletes a category.
     *
     * @param id category id
     */
    void deleteCategory(Long id);

    /**
     * Returns a category by id.
     *
     * @param id category id
     * @return category details
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Returns all active categories.
     *
     * @return category list
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Returns categories in hierarchical structure.
     *
     * @return category tree
     */
    List<CategoryTreeResponse> getCategoryTree();

    /**
     * Searches categories by keyword.
     *
     * @param keyword category name
     * @return matching categories
     */
    List<CategoryResponse> searchCategories(String keyword);

    /**
     * Activates or deactivates a category.
     *
     * @param id category id
     * @param active new status
     * @return updated category
     */
    CategoryResponse updateCategoryStatus(
            Long id,
            Boolean active
    );
}