package com.e_commerce.shambhu.category.repository;

import com.e_commerce.shambhu.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by slug.
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check whether slug already exists.
     */
    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndParentCategoryIdAndDeletedFalse(
            String name,
            Long parentCategoryId);

    boolean existsByNameIgnoreCaseAndParentCategoryIdAndIdNotAndDeletedFalse(
            String name,
            Long parentCategoryId,
            Long id);

    /**
     * Check whether category exists by name
     * excluding the current category (Update API).
     */
    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

    /**
     * Active root categories.
     */
    List<Category> findByParentCategoryIsNullAndDeletedFalseOrderByDisplayOrderAscNameAsc();

    /**
     * Child categories.
     */
    List<Category> findByParentCategoryIdAndDeletedFalseOrderByDisplayOrderAscNameAsc(
            Long parentCategoryId
    );

    /**
     * All active categories.
     */
    List<Category> findByDeletedFalseOrderByDisplayOrderAscNameAsc();

    /**
     * Search categories.
     */
    List<Category> findByNameContainingIgnoreCaseAndDeletedFalse(
            String keyword
    );

    /**
     * Active category by id.
     */
    Optional<Category> findByIdAndDeletedFalse(Long id);

    /**
     * Check child categories.
     */
    boolean existsByParentCategoryIdAndDeletedFalse(Long parentId);

}