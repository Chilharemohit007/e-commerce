package com.e_commerce.shambhu.productImage.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateImageOrderRequest {

    @NotEmpty(message = "Image order list cannot be empty.")
    private List<
            @NotNull(message = "Image ID cannot be null.")
                    Long> imageIds;
}

/*{
  "imageIds": [5, 3, 8, 1]
}*/