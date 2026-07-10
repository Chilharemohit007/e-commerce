package com.e_commerce.shambhu.auth.repository;

import com.e_commerce.shambhu.auth.entity.RefreshToken;
import com.e_commerce.shambhu.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    /**
     * Find a refresh token.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Check whether a token exists.
     */
    boolean existsByToken(String token);

    /**
     * Find all active sessions for a user.
     */
    List<RefreshToken> findAllByUser(User user);

    /**
     * Delete one refresh token.
     */
    void deleteByToken(String token);

    /**
     * Delete all refresh tokens of a user.
     */
    void deleteAllByUser(User user);

    /*
    Find only active (non-revoked) tokens
    */
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    /*
    Delete expired tokens   Useful for scheduled cleanup jobs.
    */
    void deleteByExpiryDateBefore(LocalDateTime now);

    /*
    Find expired tokens    Useful for monitoring or reporting.
    */
    List<RefreshToken> findByExpiryDateBefore(LocalDateTime now);

    /*
    Find active tokens for a user   Returns only currently usable sessions.
    */
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
}