package com.e_commerce.shambhu.auth.repository;

import com.e_commerce.shambhu.auth.entity.PasswordResetToken;
import com.e_commerce.shambhu.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a password reset token.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find all reset tokens for a user.
     */
    List<PasswordResetToken> findAllByUser(User user);

    /**
     * Delete all reset tokens of a user.
     */
    void deleteAllByUser(User user);

    /**
     * Delete a specific token.
     */
    void deleteByToken(String token);

    /**
     * Check if a token exists.
     */
    boolean existsByToken(String token);

    /**
     * Delete expired tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime now);

    /**
     * Delete all tokens that have already been used.
     */
    void deleteByUsedTrue();

    /**
     * Find all expired tokens.
     * Useful for logging or reporting before deletion.
     */
    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime now);
}