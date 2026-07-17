package com.e_commerce.shambhu.auth.repository;

import com.e_commerce.shambhu.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

   // <T> ScopedValue<T> findByEmailAndDeletedFalse(String email);

    Optional<User> findByEmailAndDeletedFalse(String email);
}
