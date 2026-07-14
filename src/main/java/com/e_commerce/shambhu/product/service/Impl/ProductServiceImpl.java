package com.e_commerce.shambhu.product.service.Impl;

import com.e_commerce.shambhu.category.repository.CategoryRepository;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ConflictException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.common.util.SlugUtil;
import com.e_commerce.shambhu.product.dto.request.CreateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdatePriceRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateProductRequest;
import com.e_commerce.shambhu.product.dto.request.UpdateStockRequest;
import com.e_commerce.shambhu.product.dto.response.ProductDetailsResponse;
import com.e_commerce.shambhu.product.dto.response.ProductResponse;
import com.e_commerce.shambhu.product.dto.response.ProductSummaryResponse;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.enums.ProductStatus;
import com.e_commerce.shambhu.product.repository.ProductRepository;
import com.e_commerce.shambhu.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.e_commerce.shambhu.category.entity.Category;

import java.math.BigDecimal;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final SlugUtil slugUtil;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ModelMapper modelMapper,
            SlugUtil slugUtil) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.slugUtil = slugUtil;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        LOGGER.info("Creating product with SKU: {}", request.getSku());

        validateProduct(request);

        Category category = getValidCategory(request.getCategoryId());

        Product product = mapToEntity(request, category);

        product = productRepository.save(product);

        LOGGER.info(
                "Product created successfully. Product ID: {}, SKU: {}",
                product.getId(),
                product.getSku()
        );

        return mapToResponse(product);
    }

    private void validateProduct(CreateProductRequest request) {

        validateDuplicateName(request.getName());

        validateDuplicateSku(request.getSku());

        validatePricing(
                request.getPrice(),
                request.getDiscountPrice(),
                request.getCostPrice()
        );
    }

    private void validateDuplicateName(String name) {

        if (productRepository.existsByNameIgnoreCaseAndDeletedFalse(name)) {

            LOGGER.warn("Product name already exists: {}", name);

            throw new ConflictException(
                    "Product name already exists."
            );
        }
    }

    private void validateDuplicateSku(String sku) {

        if (productRepository.existsBySkuAndDeletedFalse(sku)) {

            LOGGER.warn("SKU already exists: {}", sku);

            throw new ConflictException(
                    "SKU already exists."
            );
        }
    }

    private Category getValidCategory(Long categoryId) {

        return categoryRepository
                .findByIdAndDeletedFalse(categoryId)
                .filter(Category::getActive)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Invalid category ID: {}",
                            categoryId
                    );

                    return new ResourceNotFoundException(
                            "Category not found or inactive.", null, null
                    );
                });
    }

    private void validatePricing(
            BigDecimal price,
            BigDecimal discountPrice,
            BigDecimal costPrice) {

        if (discountPrice != null &&
                discountPrice.compareTo(price) > 0) {

            throw new BusinessException(
                    "Discount price cannot exceed selling price."
            );
        }

        if (costPrice != null &&
                costPrice.compareTo(price) > 0) {

            throw new BusinessException(
                    "Cost price cannot exceed selling price."
            );
        }
    }

    private Product mapToEntity(
            CreateProductRequest request,
            Category category) {

        Product product = modelMapper.map(request, Product.class);

        product.setCategory(category);

        product.setSlug(
                slugUtil.generateUniqueSlug(request.getName())
        );

        return product;
    }

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response =
                modelMapper.map(product, ProductResponse.class);

        response.setCategoryId(product.getCategory().getId());

        response.setCategoryName(product.getCategory().getName());

        return response;
    }

    @Override
    public ProductResponse updateProduct(
            Long productId,
            UpdateProductRequest request) {

        LOGGER.info("Updating product with ID: {}", productId);

        Product product = getValidProduct(productId);

        validateProductForUpdate(product, request);

        Category category = getValidCategory(request.getCategoryId());

        updateProductFields(product, request, category);

        product = productRepository.save(product);

        LOGGER.info(
                "Product updated successfully. Product ID: {}, SKU: {}",
                product.getId(),
                product.getSku()
        );

        return mapToResponse(product);
    }

    private Product getValidProduct(Long productId) {

        return productRepository
                .findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Product not found. Product ID: {}",
                            productId
                    );

                    return new ResourceNotFoundException(
                            "Product not found.", null, null
                    );
                });
    }

    private void validateProductForUpdate(
            Product product,
            UpdateProductRequest request) {

        validateDuplicateSku(product, request.getSku());

        validateDuplicateName(product, request.getName());

        validatePricing(
                request.getPrice(),
                request.getDiscountPrice(),
                request.getCostPrice()
        );
    }

    private void validateDuplicateSku(
            Product product,
            String sku) {

        productRepository.findBySkuAndDeletedFalse(sku)
                .ifPresent(existing -> {

                    if (!existing.getId().equals(product.getId())) {

                        LOGGER.warn("SKU already exists: {}", sku);

                        throw new ConflictException(
                                "SKU already exists."
                        );
                    }
                });
    }

    private void validateDuplicateName(
            Product product,
            String name) {

        productRepository
                .findByNameIgnoreCaseAndDeletedFalse(name)
                .ifPresent(existing -> {

                    if (!existing.getId().equals(product.getId())) {

                        LOGGER.warn(
                                "Product name already exists: {}",
                                name
                        );

                        throw new ConflictException(
                                "Product name already exists."
                        );
                    }
                });
    }

    private void updateProductFields(
            Product product,
            UpdateProductRequest request,
            Category category) {

        if (!product.getName().equals(request.getName())) {

            product.setName(request.getName());

            product.setSlug(
                    slugUtil.generateUniqueSlug(request.getName())
            );
        }

        product.setSku(request.getSku());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(request.getBrand());

        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setCostPrice(request.getCostPrice());

        product.setStockQuantity(request.getStockQuantity());
        product.setMinimumStockLevel(request.getMinimumStockLevel());

        product.setFeatured(request.getFeatured());
        product.setActive(request.getActive());

        product.setWeight(request.getWeight());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());
        product.setHeight(request.getHeight());
    }

    @Override
    public void deleteProduct(Long productId) {

        LOGGER.info("Deleting product. Product ID: {}", productId);

        Product product = getProductByIdHelper(productId);

        if (Boolean.TRUE.equals(product.getDeleted())) {

            LOGGER.warn("Product is already deleted. Product ID: {}", productId);

            throw new BusinessException("Product has already been deleted.");
        }

        product.setDeleted(Boolean.TRUE);
        product.setActive(Boolean.FALSE);
        product.setStatus(ProductStatus.DISCONTINUED);

        productRepository.save(product);

        LOGGER.info(
                "Product deleted successfully. Product ID: {}, SKU: {}",
                product.getId(),
                product.getSku()
        );
    }

   /* Enterprise Considerations

    Before allowing deletion, many real-world systems also check whether the product:

    Is part of pending orders.
    Exists in active shopping carts.
    Appears in user wishlists.
    Has inventory transactions.
    Is referenced by active promotions.

    If any of these conditions apply, you might block deletion or archive the product instead.*/

    private Product getProductByIdHelper(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() -> {

                    LOGGER.warn("Product not found. Product ID: {}", productId);

                    return new ResourceNotFoundException(
                            "Product not found.", null, null
                    );
                });
    }

    /* Why @Transactional(readOnly = true)?
    This tells Spring and Hibernate that:
    No entity modifications will occur.
    Dirty checking is skipped.
    Performance is improved for read-only operations.
    The intent of the method is clearer.*/

    @Override
    @Transactional(readOnly = true)
    public ProductDetailsResponse getProductById(Long productId) {

        LOGGER.info("Fetching product. Product ID: {}", productId);

        Product product = productRepository
                .findWithCategoryByIdAndDeletedFalse(productId)
                .filter(Product::getActive)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Product not found or inactive. Product ID: {}",
                            productId
                    );

                    return new ResourceNotFoundException(
                            "Product not found.", null, null
                    );
                });

        LOGGER.info(
                "Product fetched successfully. Product ID: {}",
                productId
        );

        return mapToProductDetailsResponse(product);
    }

   /* Why @Transactional(readOnly = true)?
    This tells Spring and Hibernate that:
    No entity modifications will occur.
    Dirty checking is skipped.
    Performance is improved for read-only operations.
    The intent of the method is clearer.*/

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getAllProducts(
            Pageable pageable) {

        LOGGER.info(
                "Fetching products. Page: {}, Size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Product> products =
                productRepository.findByActiveTrueAndDeletedFalse(pageable);

        LOGGER.info(
                "Products fetched successfully. Total Elements: {}",
                products.getTotalElements()
        );

        return products.map(this::mapToProductSummaryResponse);
    }

    private ProductDetailsResponse mapToProductDetailsResponse(
            Product product) {

        ProductDetailsResponse response =
                modelMapper.map(product, ProductDetailsResponse.class);

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        return response;
    }

    private ProductSummaryResponse mapToProductSummaryResponse(
            Product product) {

        ProductSummaryResponse response =
                modelMapper.map(product, ProductSummaryResponse.class);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> searchProducts(
            String keyword,
            Pageable pageable) {

        LOGGER.info("Searching products with keyword: {}", keyword);

        Page<Product> products =
                productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(
                        keyword,
                        pageable
                );

        return products.map(this::mapToProductSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsByCategory(
            Long categoryId,
            Pageable pageable) {

        LOGGER.info("Fetching products for categoryId={}", categoryId);

        Category category = getValidCategory(categoryId);

        Page<Product> products =
                productRepository.findByCategoryAndDeletedFalse(
                        category,
                        pageable
                );

        return products.map(this::mapToProductSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsByStatus(
            ProductStatus status,
            Pageable pageable) {

        LOGGER.info("Fetching products with status={}", status);

        Page<Product> products =
                productRepository.findByStatusAndDeletedFalse(
                        status,
                        pageable
                );

        return products.map(this::mapToProductSummaryResponse);
    }

    @Override
    public ProductResponse updateProductStatus(
            Long productId,
            ProductStatus status) {

        LOGGER.info(
                "Updating product status. ProductId={}, Status={}",
                productId,
                status
        );

        Product product = getValidProduct(productId);

        product.setStatus(status);

        if (status == ProductStatus.INACTIVE
                || status == ProductStatus.DISCONTINUED) {

            product.setActive(false);

        } else if (status == ProductStatus.ACTIVE) {

            product.setActive(true);
        }

        product = productRepository.save(product);

        LOGGER.info(
                "Product status updated successfully. ProductId={}",
                productId
        );

        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateStock(
            Long productId,
            UpdateStockRequest request) {

        LOGGER.info(
                "Updating stock for productId={}",
                productId
        );

        Product product = getValidProduct(productId);

        product.setStockQuantity(request.getStockQuantity());
        product.setMinimumStockLevel(
                request.getMinimumStockLevel()
        );

        if (product.getStockQuantity() == 0) {

            product.setStatus(ProductStatus.OUT_OF_STOCK);

        } else if (product.getStatus() ==
                ProductStatus.OUT_OF_STOCK) {

            product.setStatus(ProductStatus.ACTIVE);
        }

        product = productRepository.save(product);

        LOGGER.info(
                "Stock updated successfully. ProductId={}, Stock={}",
                productId,
                product.getStockQuantity()
        );

        return mapToResponse(product);
    }

    @Override
    public ProductResponse updatePrice(
            Long productId,
            UpdatePriceRequest request) {

        LOGGER.info(
                "Updating price for productId={}",
                productId
        );

        Product product = getValidProduct(productId);

        validatePricing(
                request.getPrice(),
                request.getDiscountPrice(),
                request.getCostPrice()
        );

        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setCostPrice(request.getCostPrice());

        product = productRepository.save(product);

        LOGGER.info(
                "Price updated successfully. ProductId={}",
                productId
        );

        return mapToResponse(product);
    }
}
