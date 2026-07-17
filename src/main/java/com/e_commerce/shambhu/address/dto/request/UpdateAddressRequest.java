package com.e_commerce.shambhu.address.dto.request;

import com.e_commerce.shambhu.address.enums.AddressType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String mobileNumber;

    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String alternateMobileNumber;

    @NotBlank
    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 150)
    private String landmark;

    @NotBlank
    @Size(max = 80)
    private String city;

    @NotBlank
    @Size(max = 80)
    private String state;

    @NotBlank
    @Size(max = 80)
    private String country;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")
    private String postalCode;

    @NotNull
    private AddressType addressType;

    private Boolean defaultAddress;
}