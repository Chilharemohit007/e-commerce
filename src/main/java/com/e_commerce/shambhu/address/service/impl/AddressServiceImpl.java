package com.e_commerce.shambhu.address.service.impl;

import com.e_commerce.shambhu.address.dto.request.CreateAddressRequest;
import com.e_commerce.shambhu.address.dto.request.UpdateAddressRequest;
import com.e_commerce.shambhu.address.dto.response.AddressResponse;
import com.e_commerce.shambhu.address.dto.response.AddressSummaryResponse;
import com.e_commerce.shambhu.address.entity.Address;
import com.e_commerce.shambhu.address.repository.AddressRepository;
import com.e_commerce.shambhu.address.service.AddressService;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.common.exception.ConflictException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public AddressResponse createAddress(
            CreateAddressRequest request) {

        User user = getAuthenticatedUser();

        log.info(
                "Creating address for userId={}",
                user.getId()
        );

        validateDuplicateAddress(user, request);

        boolean firstAddress =
                addressRepository.countByUserAndDeletedFalse(user) == 0;

        if (Boolean.TRUE.equals(request.getDefaultAddress())) {

            clearDefaultAddress(user);

        } else if (firstAddress) {

            request.setDefaultAddress(Boolean.TRUE);
        }

        Address address =
                mapToEntity(request, user);

        Address savedAddress =
                addressRepository.save(address);

        log.info(
                "Address created successfully. addressId={}, userId={}",
                savedAddress.getId(),
                user.getId()
        );

        return mapToResponse(savedAddress);
    }

    private void validateDuplicateAddress(User user, CreateAddressRequest request) {
    }

    private User getAuthenticatedUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmailAndDeletedFalse(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found.", null, null
                        ));
    }

    private void clearDefaultAddress(
            User user) {

        addressRepository
                .findByUserAndDefaultAddressTrueAndDeletedFalse(user)
                .ifPresent(address -> {

                    address.setDefaultAddress(Boolean.FALSE);

                    addressRepository.save(address);
                });
    }

    private Address mapToEntity(
            CreateAddressRequest request,
            User user) {

        Address address =
                modelMapper.map(request, Address.class);

        address.setUser(user);

        address.setDeleted(Boolean.FALSE);

        return address;
    }

    private AddressResponse mapToResponse(
            Address address) {

        return modelMapper.map(
                address,
                AddressResponse.class
        );
    }

    @Override
    public AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request) {

        User user = getAuthenticatedUser();

        log.info(
                "Updating address. addressId={}, userId={}",
                addressId,
                user.getId()
        );

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found.", null, null
                ));

        validateAddressOwnership(address, user);

        validateDuplicateAddress(
                address,
                user,
                request
        );

        handleDefaultAddress(
                address,
                user,
                request.getDefaultAddress()
        );

        updateAddressFields(
                address,
                request
        );

        Address updatedAddress =
                addressRepository.save(address);

        log.info(
                "Address updated successfully. addressId={}, userId={}",
                updatedAddress.getId(),
                user.getId()
        );

        return mapToResponse(updatedAddress);
    }

    private void validateAddressOwnership(
            Address address,
            User user) {

        if (!address.getUser().getId().equals(user.getId())) {

            throw new ForbiddenException(
                    "You are not authorized to access this address."
            );
        }
    }

    private void validateDuplicateAddress(
            Address existingAddress,
            User user,
            UpdateAddressRequest request) {

        List<Address> addresses =
                addressRepository.findByUserAndDeletedFalse(user);

        boolean duplicate = addresses.stream()
                .filter(address ->
                        !address.getId().equals(existingAddress.getId()))
                .anyMatch(address ->
                        Objects.equals(address.getAddressLine1(), request.getAddressLine1())
                                && Objects.equals(address.getCity(), request.getCity())
                                && Objects.equals(address.getState(), request.getState())
                                && Objects.equals(address.getPostalCode(), request.getPostalCode()));

        if (duplicate) {

            throw new ConflictException(
                    "Address already exists."
            );
        }
    }

    private void handleDefaultAddress(
            Address address,
            User user,
            Boolean defaultAddress) {

        if (!Boolean.TRUE.equals(defaultAddress)) {
            return;
        }

        addressRepository
                .findByUserAndDefaultAddressTrueAndDeletedFalse(user)
                .ifPresent(existing -> {

                    if (!existing.getId().equals(address.getId())) {

                        existing.setDefaultAddress(Boolean.FALSE);

                        addressRepository.save(existing);
                    }
                });

        address.setDefaultAddress(Boolean.TRUE);
    }

    private void updateAddressFields(
            Address address,
            UpdateAddressRequest request) {

        address.setFullName(request.getFullName());
        address.setMobileNumber(request.getMobileNumber());
        address.setAlternateMobileNumber(request.getAlternateMobileNumber());

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());

        address.setLandmark(request.getLandmark());

        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());

        address.setPostalCode(request.getPostalCode());

        address.setAddressType(request.getAddressType());
    }

    @Override
    public void deleteAddress(Long addressId) {

        User user = getAuthenticatedUser();

        log.info("Deleting address. addressId={}, userId={}",
                addressId,
                user.getId());

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address not found.", null, null));

        validateAddressOwnership(address, user);

        address.setDeleted(Boolean.TRUE);
        address.setDefaultAddress(Boolean.FALSE);

        addressRepository.save(address);

        assignNewDefaultAddress(user);

        log.info("Address deleted successfully. addressId={}",
                addressId);
    }

    private void assignNewDefaultAddress(User user) {

        List<Address> addresses =
                addressRepository.findByUserAndDeletedFalse(user);

        addresses.stream()
                .filter(address -> !Boolean.TRUE.equals(address.getDeleted()))
                .findFirst()
                .ifPresent(address -> {

                    address.setDefaultAddress(Boolean.TRUE);

                    addressRepository.save(address);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {

        User user = getAuthenticatedUser();

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address not found.", null, null));

        validateAddressOwnership(address, user);

        log.info("Retrieved address. addressId={}",
                addressId);

        return mapToResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressSummaryResponse> getUserAddresses() {

        User user = getAuthenticatedUser();

        log.info("Retrieving addresses for userId={}",
                user.getId());

        return addressRepository
                .findByUserAndDeletedFalse(user)
                .stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    private AddressSummaryResponse mapToSummaryResponse(
            Address address) {

        return AddressSummaryResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .mobileNumber(address.getMobileNumber())
                .formattedAddress(buildFormattedAddress(address))
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .addressType(address.getAddressType())
                .defaultAddress(address.getDefaultAddress())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddress() {

        User user = getAuthenticatedUser();

        Address address = addressRepository
                .findByUserAndDefaultAddressTrueAndDeletedFalse(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default address not found.", null, null
                        ));

        log.info("Retrieved default address. addressId={}",
                address.getId());

        return mapToResponse(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId) {

        User user = getAuthenticatedUser();

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found.", null, null
                        ));

        validateAddressOwnership(address, user);

        clearDefaultAddress(user);

        address.setDefaultAddress(Boolean.TRUE);

        Address updated =
                addressRepository.save(address);

        log.info(
                "Default address updated. addressId={}",
                addressId
        );

        return mapToResponse(updated);
    }

    private String buildFormattedAddress(Address address) {

        return String.join((CharSequence) ", ",
                (CharSequence) Stream.of(
                                address.getAddressLine1(),
                                address.getAddressLine2(),
                                address.getLandMark(),
                                address.getCity(),
                                address.getState(),
                                address.getCountry(),
                                address.getPostalCode()
                        )
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isBlank())
                        .toList()
        );
    }

}
