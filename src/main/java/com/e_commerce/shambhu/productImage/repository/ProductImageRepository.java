package com.e_commerce.shambhu.productImage.repository;

import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.productImage.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @EntityGraph(attributePaths = "product")
    Optional<ProductImage> findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = "product")
    List<ProductImage> findByProductAndDeletedFalseOrderByDisplayOrderAsc(
            Product product
    );

    @EntityGraph(attributePaths = "product")
    Optional<ProductImage> findByProductAndPrimaryImageTrueAndDeletedFalse(
            Product product
    );

    boolean existsByProductAndPrimaryImageTrueAndDeletedFalse(
            Product product
    );

    @EntityGraph(attributePaths = "product")
    Page<ProductImage> findByDeletedFalse(Pageable pageable);

    void deleteAllByProduct(Product product);

    long countByProductAndDeletedFalse(Product product);

    boolean existsByProductAndDeletedFalse(Product product);

    Optional<ProductImage> findByProductAndDisplayOrderAndDeletedFalse(
            Product product,
            Integer displayOrder
    );
}

/*
Repository Methods Explained
Method	                                                Purpose
findByIdAndDeletedFalse()	                            Fetch an active image by ID
findByProductAndDeletedFalseOrderByDisplayOrderAsc()	Retrieve all images for a product in display order
findByProductAndPrimaryImageTrueAndDeletedFalse()	    Retrieve the current primary image
existsByProductAndPrimaryImageTrueAndDeletedFalse()	    Check whether a primary image already exists
findByDeletedFalse()	                                Retrieve all active images with pagination
deleteAllByProduct()	                                Remove all image records for a product (typically when permanently deleting a product)
countByProductAndDeletedFalse()	                        Count active images for a product
existsByProductAndDeletedFalse()	                    Check if a product has any images
findByProductAndDisplayOrderAndDeletedFalse()	        Validate display-order uniqueness*/
