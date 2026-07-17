package com.e_commerce.shambhu.product.productImage.service;

import com.e_commerce.shambhu.product.productImage.dto.response.ProductImageResponse;
import com.e_commerce.shambhu.product.productImage.dto.request.UpdateImageOrderRequest;
import com.e_commerce.shambhu.product.productImage.dto.request.UpdateProductImageRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductImageService {

    /**
     * Uploads an image for a product.
     */
    ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file,
            Boolean primaryImage,
            Integer displayOrder
    );

    /**
     * Updates image metadata.
     */
    ProductImageResponse updateImage(
            Long imageId,
            UpdateProductImageRequest request
    );

    /**
     * Soft deletes an image.
     */
    void deleteImage(Long imageId);

    /**
     * Returns all active images of a product.
     */
    List<ProductImageResponse> getProductImages(
            Long productId
    );

    /**
     * Marks an image as the primary image.
     */
    ProductImageResponse setPrimaryImage(
            Long imageId
    );

    /**
     * Reorders product images.
     */
    void reorderImages(
            Long productId,
            UpdateImageOrderRequest request
    );

    String getPrimaryImageUrl(Long productId);
}
/*| Method               | Responsibility                                              |
| -------------------- | ----------------------------------------------------------- |
| `uploadImage()`      | Validate and upload a new image                             |
| `updateImage()`      | Update image metadata such as primary flag or display order |
| `deleteImage()`      | Soft delete an image                                        |
| `getProductImages()` | Retrieve all active images for a product                    |
| `setPrimaryImage()`  | Set one image as the primary image                          |
| `reorderImages()`    | Update the display order of product images                  |
*/


/*Why Pass MultipartFile Instead of UploadProductImageRequest?

Although UploadProductImageRequest can hold a MultipartFile, in Spring Boot it's more common to keep the service signature simple:

uploadImage(
        Long productId,
        MultipartFile file,
        Boolean primaryImage,
        Integer displayOrder
)

This aligns naturally with multipart/form-data requests and avoids unnecessary wrapping.*/