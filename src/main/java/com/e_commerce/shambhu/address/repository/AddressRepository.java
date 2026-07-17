package com.e_commerce.shambhu.address.repository;

import com.e_commerce.shambhu.address.entity.Address;
import com.e_commerce.shambhu.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Find active address by id.
     */
    Optional<Address> findByIdAndDeletedFalse(Long id);

    /**
     * Get all active addresses of a user.
     */
    @EntityGraph(attributePaths = "user")
    List<Address> findByUserAndDeletedFalse(User user);

    /**
     * Get paginated active addresses of a user.
     */
    @EntityGraph(attributePaths = "user")
    Page<Address> findByUserAndDeletedFalse(
            User user,
            Pageable pageable
    );

    /**
     * Get user's default address.
     */
    Optional<Address> findByUserAndDefaultAddressTrueAndDeletedFalse(
            User user
    );

    /**
     * Check whether a default address exists.
     */
    boolean existsByUserAndDefaultAddressTrueAndDeletedFalse(
            User user
    );

    /**
     * Count active addresses.
     */
    long countByUserAndDeletedFalse(User user);

    /**
     * Check ownership.
     */
    boolean existsByIdAndUserAndDeletedFalse(
            Long id,
            User user
    );

    /**
     * Check duplicate address.
     */
    boolean existsByUserAndAddressLine1AndCityAndStateAndPostalCodeAndDeletedFalse(
            User user,
            String addressLine1,
            String city,
            String state,
            String postalCode
    );

    /**
     * Get all default addresses (mainly for admin/reporting).
     */
    List<Address> findByDefaultAddressTrueAndDeletedFalse();

}