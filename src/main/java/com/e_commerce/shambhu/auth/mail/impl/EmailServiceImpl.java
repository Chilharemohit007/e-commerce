package com.e_commerce.shambhu.auth.mail.impl;

import com.e_commerce.shambhu.auth.mail.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject("Reset Your Password");

        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset your password:\n\n" +
                        resetLink +
                        "\n\nThis link will expire in 30 minutes.\n\n" +
                        "If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "Regards,\n" +
                        "E-Commerce Team"
        );

        mailSender.send(message);

        LOGGER.info("Password reset email sent successfully to {}", to);
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify Your Email");

            String html = """
                    <html>
                    <body style="font-family:Arial,sans-serif">
                        <h2>Welcome to Shambhu E-Commerce</h2>

                        <p>Thank you for registering.</p>

                        <p>Please click the button below to verify your email address.</p>

                        <p>
                            <a href="%s"
                               style="
                                    background:#1976d2;
                                    color:white;
                                    padding:12px 24px;
                                    text-decoration:none;
                                    border-radius:5px;">
                                Verify Email
                            </a>
                        </p>

                        <p>If you didn't create this account, you can safely ignore this email.</p>

                        <br>

                        <p>Regards,<br>Shambhu E-Commerce Team</p>
                    </body>
                    </html>
                    """.formatted(verificationLink);

            helper.setText(html, true);

            mailSender.send(mimeMessage);

            LOGGER.info("Verification email sent successfully to {}", to);

        } catch (MessagingException ex) {

            LOGGER.error("Failed to send verification email to {}", to, ex);

            throw new RuntimeException(
                    "Unable to send verification email.", ex);
        }
    }
}
}
