package com.e_commerce.shambhu.address.dto.response;

import com.e_commerce.shambhu.address.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSummaryResponse {

    private Long id;

    private String fullName;

    private String mobileNumber;

    /**
     * Complete formatted address.
     */
    private String formattedAddress;

    private String city;

    private String state;

    private String postalCode;

    private AddressType addressType;

    private Boolean defaultAddress;
}
