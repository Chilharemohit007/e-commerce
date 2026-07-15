package com.e_commerce.shambhu.productImage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadProductImageRequest {

    @NotNull(message = "Image file is required.")
    private MultipartFile file;

    private Boolean primaryImage = Boolean.FALSE;

    private Integer displayOrder = 0;
}