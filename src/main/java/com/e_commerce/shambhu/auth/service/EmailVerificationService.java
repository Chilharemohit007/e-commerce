package com.e_commerce.shambhu.auth.service;

import com.e_commerce.shambhu.auth.entity.User;

public interface EmailVerificationService {

    /**
     * Generates a verification token and sends
     * an email verification link to the user.
     *
     * Called immediately after successful registration.
     *
     * @param user newly registered user
     */
    void sendVerificationEmail(User user);

    /**
     * Verifies the email using the verification token.
     *
     * Steps:
     * 1. Validate token.
     * 2. Enable user account.
     * 3. Mark token as verified.
     * 4. Delete or invalidate remaining verification tokens.
     *
     * @param token verification token
     */
    void verifyEmail(String token);

    /**
     * Generates and sends a new verification email.
     *
     * Typical flow:
     * - Delete existing verification tokens
     * - Generate a new token
     * - Send a fresh verification email
     *
     * @param email registered email address
     */
    void resendVerificationEmail(String email);

    /**
     * Deletes expired verification tokens.
     *
     * Invoked periodically by a scheduled cleanup task.
     */
    void deleteExpiredTokens();

    void sendVerificationEmail(
            String to,
            String verificationLink
    );
}