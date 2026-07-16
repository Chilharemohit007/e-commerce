package com.e_commerce.shambhu.auth.service.impl;

import com.e_commerce.shambhu.auth.config.JwtProperties;
import com.e_commerce.shambhu.auth.dto.LoginResponse;
import com.e_commerce.shambhu.auth.entity.RefreshToken;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repository.RefreshTokenRepository;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.auth.security.CustomUserDetails;
import com.e_commerce.shambhu.auth.security.JwtService;
import com.e_commerce.shambhu.auth.service.RefreshTokenService;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        RefreshToken storedToken = verifyRefreshToken(refreshToken);
        User user = storedToken.getUser();
        UserDetails userDetails = new CustomUserDetails(user);
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());

        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .toList()
        );
        String accessToken =
                jwtService.generateAccessToken(
                        claims,
                        userDetails
                );
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newRefreshToken = createRefreshToken(user);
        LoginResponse response = new LoginResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtProperties.getAccessTokenExpiration());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().toString())
                        .toList()
        );
        LOGGER.info(
                "Access token refreshed successfully for userId={}",
                user.getId()
        );
        return response;
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token not found.", null, null));

        if (Boolean.TRUE.equals(token.getRevoked())) {
            LOGGER.warn("Refresh token already revoked.");
            return;
        }

        token.setRevoked(true);

        refreshTokenRepository.save(token);

        LOGGER.info(
                "Refresh token revoked successfully for userId={}",
                token.getUser().getId()
        );
    }

    @Override
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        if (tokens.isEmpty()) {
            LOGGER.info("No active refresh tokens found for userId={}", user.getId());
            return;
        }
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
        LOGGER.info("Revoked {} refresh token(s) for userId={}", tokens.size(), user.getId());
    }

    @Override
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        LOGGER.info("Expired refresh tokens deleted successfully.");
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