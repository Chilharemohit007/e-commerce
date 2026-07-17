package com.e_commerce.shambhu.address.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetDefaultAddressRequest {

    @NotNull(message = "Default address flag is required.")
    private Boolean defaultAddress;
}
