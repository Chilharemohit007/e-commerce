package com.e_commerce.shambhu.auth.service;

import com.e_commerce.shambhu.auth.dto.LoginResponse;
import com.e_commerce.shambhu.auth.entity.User;

public interface RefreshTokenService {

    /**
     * Creates and stores a refresh token.
     * Called immediately after successful login.
     * @param user authenticated user
     * @return generated refresh token
     */
    String createRefreshToken(User user);

    /**
     * Generates a new access token
     * (and optionally rotates the refresh token).
     */
    LoginResponse refreshAccessToken(String refreshToken);

    /**
     * Logout from current device.
     */
    void revokeRefreshToken(String refreshToken);

    /**
     * Logout from all devices.
     * Useful when:
     *
     * Password changes
     * Account compromise
     * Admin disables account
     * Logout from all devices
     */
    void revokeAllUserTokens(User user);

    /**
     * Removes expired refresh tokens.
     * Called by a scheduled job.
     */
    void deleteExpiredTokens();
}