package com.e_commerce.shambhu.address.controller;

import com.e_commerce.shambhu.address.dto.request.CreateAddressRequest;
import com.e_commerce.shambhu.address.dto.request.UpdateAddressRequest;
import com.e_commerce.shambhu.address.dto.response.AddressResponse;
import com.e_commerce.shambhu.address.dto.response.AddressSummaryResponse;
import com.e_commerce.shambhu.address.service.AddressService;
import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    /**
     * Create Address
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody CreateAddressRequest request) {

        log.info("REST request to create address.");

        AddressResponse response =
                addressService.createAddress(request);

        return ResponseBuilder.created(
                "Address created successfully.",
                response
        );
    }

    /**
     * Update Address
     */
    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {

        log.info("REST request to update address. addressId={}", addressId);

        AddressResponse response =
                addressService.updateAddress(addressId, request);

        return ResponseBuilder.ok(
                "Address updated successfully.",
                response
        );
    }

    /**
     * Delete Address (Soft Delete)
     */
    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long addressId) {

        log.info("REST request to delete address. addressId={}", addressId);

        addressService.deleteAddress(addressId);

        return ResponseBuilder.ok(
                "Address deleted successfully.",
                null
        );
    }

    /**
     * Get Address By Id
     */
    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable Long addressId) {

        log.info("REST request to retrieve address. addressId={}", addressId);

        AddressResponse response =
                addressService.getAddressById(addressId);

        return ResponseBuilder.ok(
                "Address retrieved successfully.",
                response
        );
    }

    /**
     * Get All Addresses
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<AddressSummaryResponse>>> getUserAddresses() {

        log.info("REST request to retrieve user addresses.");

        List<AddressSummaryResponse> response =
                addressService.getUserAddresses();

        return ResponseBuilder.ok(
                "Addresses retrieved successfully.",
                response
        );
    }

    /**
     * Get Default Address
     */
    @GetMapping("/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress() {

        log.info("REST request to retrieve default address.");

        AddressResponse response =
                addressService.getDefaultAddress();

        return ResponseBuilder.ok(
                "Default address retrieved successfully.",
                response
        );
    }

    /**
     * Set Default Address
     */
    @PatchMapping("/{addressId}/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long addressId) {

        log.info("REST request to set default address. addressId={}", addressId);

        AddressResponse response =
                addressService.setDefaultAddress(addressId);

        return ResponseBuilder.ok(
                "Default address updated successfully.",
                response
        );
    }
}