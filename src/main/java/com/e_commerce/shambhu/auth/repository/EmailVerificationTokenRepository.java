package com.e_commerce.shambhu.auth.repository;

import com.e_commerce.shambhu.auth.entity.EmailVerificationToken;
import com.e_commerce.shambhu.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    /**
     * Find a verification token.
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Find all verification tokens of a user.
     */
    List<EmailVerificationToken> findAllByUser(User user);

    /**
     * Delete all verification tokens of a user.
     */
    void deleteAllByUser(User user);

    /**
     * Delete a verification token.
     */
    void deleteByToken(String token);

    /**
     * Check whether a token exists.
     */
    boolean existsByToken(String token);

    /**
     * Delete expired verification tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime now);

    /**
     * Find expired verification tokens.
     * Useful for logging before cleanup.
     */
    List<EmailVerificationToken> findByExpiryDateBefore(LocalDateTime now);

    /**
     * Find all verified tokens.
     * Optional cleanup/reporting method.
     */
    List<EmailVerificationToken> findByVerifiedTrue();

    /**
     * Delete verified tokens.
     * Optional cleanup after successful verification.
     */
    void deleteByVerifiedTrue();
}