package com.e_commerce.shambhu.auth.service.impl;

import com.e_commerce.shambhu.auth.config.EmailVerificationProperties;
import com.e_commerce.shambhu.auth.entity.EmailVerificationToken;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.mail.service.EmailService;
import com.e_commerce.shambhu.auth.repository.EmailVerificationTokenRepository;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.auth.service.EmailVerificationService;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl
        implements EmailVerificationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    EmailVerificationServiceImpl.class
            );

    private final JavaMailSender mailSender;
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerificationProperties properties;

    public EmailVerificationServiceImpl(
            JavaMailSender mailSender, EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            EmailVerificationProperties properties) {
        this.mailSender = mailSender;

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.properties = properties;
    }

    @Override
    public void sendVerificationEmail(User user) {

        LOGGER.info(
                "Generating email verification token for userId={}",
                user.getId()
        );

        // Ensure only one active verification token exists
        tokenRepository.deleteAllByUser(user);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                new EmailVerificationToken();

        verificationToken.setToken(token);

        verificationToken.setUser(user);

        verificationToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(
                                properties.getExpiration() / 1000
                        )
        );

        verificationToken.setVerified(false);

        tokenRepository.save(verificationToken);

        String verificationLink =
                properties.getVerificationUrl() + token;

        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationLink
        );

        LOGGER.info(
                "Verification email sent successfully to userId={}",
                user.getId()
        );
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        LOGGER.info("Email verification initiated.");

        EmailVerificationToken verificationToken = verifyToken(token);

        User user = verificationToken.getUser();

        if (Boolean.TRUE.equals(user.getEnabled())) {

            LOGGER.warn(
                    "User account is already verified. userId={}",
                    user.getId()
            );

            throw new BusinessException(
                    "Email has already been verified.");
        }

        // Enable account
        user.setEnabled(true);

        userRepository.save(user);

        // Mark token as verified
        verificationToken.setVerified(true);

        tokenRepository.save(verificationToken);

        // Remove any remaining verification tokens
        tokenRepository.deleteAllByUser(user);

        LOGGER.info(
                "Email verified successfully for userId={}",
                user.getId()
        );
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        LOGGER.info("Resending verification email for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email, null, null));

        if (Boolean.TRUE.equals(user.getEnabled())) {

            throw new BusinessException(
                    "Email is already verified.");
        }

        tokenRepository.deleteAllByUser(user);

        sendVerificationEmail(user);

        LOGGER.info(
                "Verification email resent successfully for userId={}",
                user.getId()
        );
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteExpiredTokens() {

        LOGGER.info("Starting cleanup of expired email verification tokens.");

        List<EmailVerificationToken> expiredTokens =
                tokenRepository.findByExpiryDateBefore(LocalDateTime.now());

        if (expiredTokens.isEmpty()) {

            LOGGER.info("No expired email verification tokens found.");

            return;
        }

        tokenRepository.deleteAll(expiredTokens);

        LOGGER.info(
                "Deleted {} expired email verification token(s).",
                expiredTokens.size()
        );
    }

    @Override
    public void sendVerificationEmail(
            String to,
            String verificationLink
    ) {

        MimeMessage mimeMessage =
                mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            true,
                            StandardCharsets.UTF_8.name()
                    );

            helper.setTo(to);

            helper.setSubject("Verify Your Email Address");

            String html = """
                <html>
                    <body>
                        <h2>Welcome!</h2>

                        <p>Thank you for registering.</p>

                        <p>Please verify your email by clicking the button below.</p>

                        <p>
                            <a href="%s"
                               style="
                                    background:#0d6efd;
                                    color:white;
                                    padding:12px 24px;
                                    text-decoration:none;
                                    border-radius:6px;">
                                Verify Email
                            </a>
                        </p>

                        <p>This verification link will expire in 24 hours.</p>

                    </body>
                </html>
                """.formatted(verificationLink);

            helper.setText(html, true);

            mailSender.send(mimeMessage);

            LOGGER.info(
                    "Verification email sent successfully to {}",
                    to
            );

        } catch (Exception ex) {

            LOGGER.error(
                    "Failed to send verification email to {}",
                    to,
                    ex
            );

            throw new RuntimeException(
                    "Unable to send verification email.",
                    ex
            );
        }
    }

    private EmailVerificationToken verifyToken(String token) {

        LOGGER.info("Validating email verification token.");

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Email verification token not found.", null, null));

        if (Boolean.TRUE.equals(verificationToken.getVerified())) {

            LOGGER.warn(
                    "Email verification token has already been used."
            );

            throw new BusinessException(
                    "Email has already been verified.");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            LOGGER.warn(
                    "Email verification token has expired."
            );

            tokenRepository.delete(verificationToken);

            throw new BusinessException(
                    "Email verification token has expired.");
        }

        LOGGER.info(
                "Email verification token validated successfully for userId={}",
                verificationToken.getUser().getId()
        );

        return verificationToken;
    }

    // methods...
}