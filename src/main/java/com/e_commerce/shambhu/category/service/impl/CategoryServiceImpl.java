package com.e_commerce.shambhu.category.service.impl;

import com.e_commerce.shambhu.category.dto.request.CreateCategoryRequest;
import com.e_commerce.shambhu.category.dto.request.UpdateCategoryRequest;
import com.e_commerce.shambhu.category.dto.response.CategoryResponse;
import com.e_commerce.shambhu.category.dto.response.CategoryTreeResponse;
import com.e_commerce.shambhu.category.entity.Category;
import com.e_commerce.shambhu.category.repository.CategoryRepository;
import com.e_commerce.shambhu.category.service.CategoryService;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.common.util.SlugUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final SlugUtil slugUtil;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            ModelMapper modelMapper,
            SlugUtil slugUtil) {

        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.slugUtil = slugUtil;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        LOGGER.info("Creating category with name={}", request.getName());

        // Validate duplicate category name
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {

            LOGGER.warn(
                    "Category already exists with name={}",
                    request.getName()
            );

            throw new BusinessException(
                    "Category already exists with name: " + request.getName()
            );
        }

        // Generate slug
        String slug = slugUtil.generateUniqueSlug(request.getName());

        Category category = new Category();

        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(request.getActive());

        // Validate parent category
        if (request.getParentCategoryId() != null) {

            Category parentCategory = categoryRepository
                    .findByIdAndDeletedFalse(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: "
                                    + request.getParentCategoryId(), null, null));

            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);

        LOGGER.info(
                "Category created successfully. id={}, slug={}",
                savedCategory.getId(),
                savedCategory.getSlug()
        );

        CategoryResponse response =
                modelMapper.map(savedCategory, CategoryResponse.class);

        if (savedCategory.getParentCategory() != null) {
            response.setParentCategoryId(
                    savedCategory.getParentCategory().getId()
            );
        }

        return response;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            UpdateCategoryRequest request) {

        LOGGER.info("Updating category. id={}", id);

        Category category = categoryRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id, null, null));

        // Duplicate name validation
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(
                request.getName(),
                id)) {

            throw new BusinessException(
                    "Category already exists with name: "
                            + request.getName());
        }

        // Update slug only if name changed
        if (!category.getName().equalsIgnoreCase(request.getName())) {

            category.setSlug(
                    slugUtil.generateUniqueSlug(request.getName()));
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(request.getActive());

        validateParentCategory(category, request.getParentCategoryId());

        Category updatedCategory =
                categoryRepository.save(category);

        LOGGER.info(
                "Category updated successfully. id={}",
                updatedCategory.getId());

        CategoryResponse response =
                modelMapper.map(updatedCategory,
                        CategoryResponse.class);

        if (updatedCategory.getParentCategory() != null) {

            response.setParentCategoryId(
                    updatedCategory.getParentCategory().getId());
        }

        return response;
    }

    private void validateParentCategory(
            Category category,
            Long parentCategoryId) {

        if (parentCategoryId == null) {

            category.setParentCategory(null);

            return;
        }

        if (category.getId().equals(parentCategoryId)) {

            throw new BusinessException(
                    "Category cannot be its own parent.");
        }

        Category parentCategory =
                categoryRepository
                        .findByIdAndDeletedFalse(parentCategoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Parent category not found.", null, null));

        if (isCircularHierarchy(category, parentCategory)) {

            throw new BusinessException(
                    "Circular category hierarchy detected.");
        }

        category.setParentCategory(parentCategory);
    }

    private boolean isCircularHierarchy(
            Category category,
            Category parentCategory) {

        Category current = parentCategory;

        while (current != null) {

            if (current.getId().equals(category.getId())) {

                return true;
            }

            current = current.getParentCategory();
        }

        return false;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        LOGGER.info("Deleting category. id={}", id);

        Category category = categoryRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id, null, null));

        // Prevent deletion if active child categories exist
        if (categoryRepository.existsByParentCategoryIdAndDeletedFalse(id)) {

            LOGGER.warn(
                    "Cannot delete category. Active child categories exist. id={}",
                    id);

            throw new BusinessException(
                    "Cannot delete category because it contains child categories.");
        }

        category.setDeleted(true);
        category.setActive(false);

        categoryRepository.save(category);

        LOGGER.info(
                "Category soft deleted successfully. id={}",
                id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        LOGGER.info("Fetching category. id={}", id);

        Category category = categoryRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id, null, null));

        CategoryResponse response =
                modelMapper.map(category, CategoryResponse.class);

        if (category.getParentCategory() != null) {
            response.setParentCategoryId(
                    category.getParentCategory().getId()
            );
        }

        LOGGER.info(
                "Category fetched successfully. id={}",
                id
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        LOGGER.info("Fetching all active categories.");

        List<Category> categories =
                categoryRepository
                        .findByDeletedFalseOrderByDisplayOrderAscNameAsc();

        List<CategoryResponse> responses = categories
                .stream()
                .map(category -> {

                    CategoryResponse response =
                            modelMapper.map(category, CategoryResponse.class);

                    if (category.getParentCategory() != null) {
                        response.setParentCategoryId(
                                category.getParentCategory().getId()
                        );
                    }

                    return response;
                })
                .toList();

        LOGGER.info(
                "Fetched {} categories successfully.",
                responses.size()
        );

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoryTree() {

        LOGGER.info("Fetching category tree.");

        List<Category> rootCategories = categoryRepository
                .findByParentCategoryIsNullAndDeletedFalseOrderByDisplayOrderAscNameAsc();

        List<CategoryTreeResponse> response = rootCategories
                .stream()
                .map(this::mapToCategoryTree)
                .toList();

        LOGGER.info(
                "Category tree fetched successfully. Root categories={}",
                response.size()
        );

        return response;
    }

    private CategoryTreeResponse mapToCategoryTree(Category category) {

        CategoryTreeResponse response = new CategoryTreeResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setImageUrl(category.getImageUrl());
        response.setDisplayOrder(category.getDisplayOrder());

        List<CategoryTreeResponse> children =
                category.getChildCategories()
                        .stream()
                        .filter(child -> !Boolean.TRUE.equals(child.getDeleted()))
                        .sorted(Comparator
                                .comparing(Category::getDisplayOrder)
                                .thenComparing(Category::getName))
                        .map(this::mapToCategoryTree)
                        .toList();

        response.setChildren(children);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String keyword) {

        LOGGER.info("Searching categories. keyword={}", keyword);

        List<Category> categories =
                categoryRepository
                        .findByNameContainingIgnoreCaseAndDeletedFalse(keyword);

        List<CategoryResponse> response = categories
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();

        LOGGER.info(
                "Search completed. keyword={}, results={}",
                keyword,
                response.size()
        );

        return response;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategoryStatus(
            Long id,
            Boolean active) {

        LOGGER.info(
                "Updating category status. id={}, active={}",
                id,
                active
        );

        Category category = categoryRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id, null, null));

        category.setActive(active);

        Category updatedCategory =
                categoryRepository.save(category);

        LOGGER.info(
                "Category status updated successfully. id={}, active={}",
                updatedCategory.getId(),
                updatedCategory.getActive()
        );

        return mapToCategoryResponse(updatedCategory);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {

        CategoryResponse response =
                modelMapper.map(category, CategoryResponse.class);

        if (category.getParentCategory() != null) {
            response.setParentCategoryId(
                    category.getParentCategory().getId()
            );
        }

        return response;
    }
}