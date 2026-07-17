package com.e_commerce.shambhu.product.productImage.service.impl;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.common.util.StoredFile;
import com.e_commerce.shambhu.config.FileStorageProperties;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.productImage.repository.ProductImageRepository;
import com.e_commerce.shambhu.product.repository.ProductRepository;
import com.e_commerce.shambhu.product.productImage.dto.request.UpdateImageOrderRequest;
import com.e_commerce.shambhu.product.productImage.dto.request.UpdateProductImageRequest;
import com.e_commerce.shambhu.product.productImage.dto.response.ProductImageResponse;
import com.e_commerce.shambhu.product.productImage.entity.ProductImage;
import com.e_commerce.shambhu.product.productImage.service.FileStorageService;
import com.e_commerce.shambhu.product.productImage.service.ProductImageService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final ModelMapper modelMapper;

    private final FileStorageService fileStorageService;

    private final FileStorageProperties fileStorageProperties;

    public ProductImageServiceImpl(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            ModelMapper modelMapper,
            FileStorageService fileStorageService,
            FileStorageProperties fileStorageProperties) {

        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.modelMapper = modelMapper;
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file,
            Boolean primaryImage,
            Integer displayOrder) {

        LOGGER.info(
                "Uploading image for productId={}",
                productId
        );

        Product product = getValidProduct(productId);

        validateImage(file);

        /*String imageUrl =
                fileStorageService.upload(file);*/
        StoredFile storedFile = fileStorageService.upload(file);

        ProductImage productImage = buildProductImage(
                product,
                storedFile,
                primaryImage,
                displayOrder
        );

        productImage =
                productImageRepository.save(productImage);

        LOGGER.info(
                "Image uploaded successfully. ImageId={}, ProductId={}",
                productImage.getId(),
                productId
        );

        return mapToResponse(productImage);
    }

    private Product getValidProduct(Long productId) {

        return productRepository
                .findByIdAndDeletedFalse(productId)
                .filter(Product::getActive)
                .orElseThrow(() -> {

                    LOGGER.warn(
                            "Product not found. ProductId={}",
                            productId
                    );

                    return new ResourceNotFoundException(
                            "Product not found.", null, null
                    );
                });
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new BusinessException(
                    "Image file is required."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !fileStorageProperties
                        .getAllowedContentTypes()
                        .contains(contentType)) {

            throw new BusinessException(
                    "Unsupported image type."
            );
        }

        if (file.getSize() >
                fileStorageProperties.getMaxFileSize()) {

            throw new BusinessException(
                    "Image exceeds maximum allowed size."
            );
        }
    }

    private ProductImage buildProductImage(
            Product product,
            StoredFile storedFile,
            Boolean primaryImage,
            Integer displayOrder) {

        ProductImage image = new ProductImage();

        image.setProduct(product);

        image.setImageUrl(storedFile.getFileUrl());

        image.setFileName(storedFile.getFileName());

        image.setContentType(storedFile.getContentType());

        image.setFileSize(storedFile.getFileSize());

        image.setDisplayOrder(displayOrder);

        image.setPrimaryImage(
                shouldBePrimary(product, primaryImage)
        );

        image.setDeleted(false);

        return image;
    }

    private String extractFileName(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        int lastSlashIndex = imageUrl.lastIndexOf('/');

        if (lastSlashIndex == -1) {
            return imageUrl;
        }

        return imageUrl.substring(lastSlashIndex + 1);
    }

    private Boolean shouldBePrimary(
            Product product,
            Boolean requestedPrimary) {

        boolean alreadyExists =
                productImageRepository
                        .existsByProductAndPrimaryImageTrueAndDeletedFalse(
                                product
                        );

        if (!alreadyExists) {
            return true;
        }

        return Boolean.TRUE.equals(requestedPrimary);
    }

    private ProductImageResponse mapToResponse(
            ProductImage image) {

        ProductImageResponse response =
                modelMapper.map(
                        image,
                        ProductImageResponse.class
                );

        response.setProductId(
                image.getProduct().getId()
        );

        return response;
    }

    @Override
    public ProductImageResponse updateImage(Long imageId, UpdateProductImageRequest request) {
        return null;
    }

    @Override
    public void deleteImage(Long imageId) {

    }

    @Override
    public List<ProductImageResponse> getProductImages(Long productId) {
        return List.of();
    }

    @Override
    public ProductImageResponse setPrimaryImage(Long imageId) {
        return null;
    }

    @Override
    public void reorderImages(Long productId, UpdateImageOrderRequest request) {

    }

    @Override
    public String getPrimaryImageUrl(Long productId) {

        return productImageRepository
                .findByProductIdAndPrimaryImageTrueAndDeletedFalse(productId)
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}
