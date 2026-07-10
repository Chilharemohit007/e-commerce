package com.e_commerce.shambhu.auth.mail.service;

public interface EmailService {

    /**
     * Sends a password reset email.
     *
     * @param to recipient email
     * @param resetLink password reset URL
     */
    void sendPasswordResetEmail(String to, String resetLink);

    void sendVerificationEmail(String to, String verificationLink);

}
