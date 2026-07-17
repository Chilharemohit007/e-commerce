package com.e_commerce.shambhu.address.dto.request;

import com.e_commerce.shambhu.address.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank(message = "Full name is required.")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid mobile number."
    )
    private String mobileNumber;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid alternate mobile number."
    )
    private String alternateMobileNumber;

    @NotBlank(message = "Address Line 1 is required.")
    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 150)
    private String landmark;

    @NotBlank(message = "City is required.")
    @Size(max = 80)
    private String city;

    @NotBlank(message = "State is required.")
    @Size(max = 80)
    private String state;

    @NotBlank(message = "Country is required.")
    @Size(max = 80)
    private String country;

    @NotBlank(message = "Postal code is required.")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Invalid postal code."
    )
    private String postalCode;

    @NotNull(message = "Address type is required.")
    private AddressType addressType;

    @Builder.Default
    private Boolean defaultAddress = Boolean.FALSE;
}