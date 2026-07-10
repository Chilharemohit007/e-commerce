package com.e_commerce.shambhu.auth.service.impl;

import com.e_commerce.shambhu.auth.config.PasswordResetProperties;
import com.e_commerce.shambhu.auth.dto.ResetPasswordRequest;
import com.e_commerce.shambhu.auth.entity.PasswordResetToken;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repository.PasswordResetTokenRepository;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.auth.mail.service.EmailService;
import com.e_commerce.shambhu.auth.service.PasswordResetService;
import com.e_commerce.shambhu.auth.service.RefreshTokenService;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final PasswordResetProperties properties;

    public PasswordResetServiceImpl(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            EmailService emailService,
            PasswordResetProperties properties) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.properties = properties;
    }

    @Override
    public void forgotPassword(String email) {

        LOGGER.info("Password reset requested for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No user found with email: " + email, null, null));

        // Allow only one active reset token per user
        passwordResetTokenRepository.deleteAllByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setToken(token);

        passwordResetToken.setUser(user);

        passwordResetToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(properties.getExpiration() / 1000)
        );

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink =
                properties.getResetUrl() + token;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );

        LOGGER.info(
                "Password reset token generated successfully for userId={}",
                user.getId()
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

    }

    @Override
    public PasswordResetToken verifyResetToken(String token) {
        return null;
    }

    @Override
    public void deleteExpiredTokens() {

    }

    // methods...
}