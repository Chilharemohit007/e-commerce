package com.e_commerce.shambhu.common.util;

import com.e_commerce.shambhu.category.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class SlugUtil {

    private final CategoryRepository categoryRepository;

    public SlugUtil(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public String generateUniqueSlug(String name) {

        String baseSlug = name
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");

        String slug = baseSlug;
        int counter = 1;

        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }
}