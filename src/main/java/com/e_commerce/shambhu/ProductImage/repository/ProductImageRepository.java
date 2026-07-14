package com.e_commerce.shambhu.ProductImage.repository;

import com.e_commerce.shambhu.ProductImage.entity.ProductImage;
import com.e_commerce.shambhu.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findByIdAndDeletedFalse(Long id);

    List<ProductImage> findByProductAndDeletedFalseOrderByDisplayOrderAsc(Product product);

    Optional<ProductImage> findByProductAndPrimaryImageTrueAndDeletedFalse(Product product);

    boolean existsByProductAndPrimaryImageTrueAndDeletedFalse(Product product);

    void deleteByProduct(Product product);
}
