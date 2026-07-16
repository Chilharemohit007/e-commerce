package com.e_commerce.shambhu.auth.service;

import com.e_commerce.shambhu.auth.dto.ResetPasswordRequest;
import com.e_commerce.shambhu.auth.entity.PasswordResetToken;

public interface PasswordResetService {

    /**
     * Initiates the forgot password process.
     *
     * Steps:
     * 1. Validate email.
     * 2. Generate secure reset token.
     * 3. Save token.
     * 4. Send reset email.
     *
     * @param email user's registered email
     */
    void forgotPassword(String email);

    /**
     * Resets the user's password.
     *
     * Steps:
     * 1. Validate token.
     * 2. Validate password.
     * 3. Encode password.
     * 4. Update user.
     * 5. Mark token as used.
     * 6. Revoke all refresh tokens.
     *
     * @param request reset password request
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Validates a password reset token.
     *
     * @param token reset token
     * @return validated PasswordResetToken
     */
    PasswordResetToken verifyResetToken(String token);

    /**
     * Deletes expired password reset tokens.
     * Invoked by a scheduled cleanup job.
     */
    void deleteExpiredTokens();
}