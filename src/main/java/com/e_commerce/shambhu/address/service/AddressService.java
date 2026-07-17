package com.e_commerce.shambhu.address.service;

import com.e_commerce.shambhu.address.dto.request.CreateAddressRequest;
import com.e_commerce.shambhu.address.dto.request.UpdateAddressRequest;
import com.e_commerce.shambhu.address.dto.response.AddressResponse;
import com.e_commerce.shambhu.address.dto.response.AddressSummaryResponse;

import java.util.List;

public interface AddressService {

    /**
     * Creates a new address for the authenticated user.
     *
     * @param request address details
     * @return created address
     */
    AddressResponse createAddress(
            CreateAddressRequest request
    );

    /**
     * Updates an existing address.
     *
     * @param addressId address identifier
     * @param request updated address details
     * @return updated address
     */
    AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request
    );

    /**
     * Soft deletes an address.
     *
     * @param addressId address identifier
     */
    void deleteAddress(
            Long addressId
    );

    /**
     * Retrieves an address by its identifier.
     *
     * @param addressId address identifier
     * @return address details
     */
    AddressResponse getAddressById(
            Long addressId
    );

    /**
     * Retrieves all active addresses of the authenticated user.
     *
     * @return list of addresses
     */
    List<AddressSummaryResponse> getUserAddresses();

    /**
     * Retrieves the default address of the authenticated user.
     *
     * @return default address
     */
    AddressResponse getDefaultAddress();

    /**
     * Marks an address as the default address.
     *
     * @param addressId address identifier
     * @return updated default address
     */
    AddressResponse setDefaultAddress(
            Long addressId
    );
}