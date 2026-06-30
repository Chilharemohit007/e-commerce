package com.e_commerce.shambhu.auth.service.impl;

import com.e_commerce.shambhu.auth.config.JwtProperties;
import com.e_commerce.shambhu.auth.dto.LoginResponse;
import com.e_commerce.shambhu.auth.entity.RefreshToken;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repo.RefreshTokenRepository;
import com.e_commerce.shambhu.auth.repo.UserRepository;
import com.e_commerce.shambhu.auth.security.JwtService;
import com.e_commerce.shambhu.auth.service.RefreshTokenService;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenServiceImpl(UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            JwtProperties jwtProperties) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String createRefreshToken(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        String token = generateSecureToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plus(java.time.Duration.ofMillis(
                                jwtProperties.getRefreshTokenExpiration()))
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        LOGGER.info(
                "Refresh token created successfully for userId={}",
                user.getId()
        );

        return token;
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    @Override
    public void revokeAllUserTokens(User user) {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    @Override
    @Scheduled(cron = "0 0 2 * * *")
    public void deleteExpiredTokens() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Validates the refresh token.
     */
    private RefreshToken verifyRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            throw new BusinessException("Refresh token is required.");
        }

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token not found.", "", null));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new UnauthorizedException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            // Optional: mark as revoked so it cannot be reused
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new UnauthorizedException("Refresh token has expired.");
        }

        LOGGER.debug(
                "Refresh token verified successfully for userId={}",
                refreshToken.getUser().getId()
        );

        return refreshToken;
    }

    /**
     * Generates a cryptographically secure random refresh token.
     */
    /**
     * Generates a cryptographically secure random refresh token.
     */
    private String generateSecureToken() {

        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}