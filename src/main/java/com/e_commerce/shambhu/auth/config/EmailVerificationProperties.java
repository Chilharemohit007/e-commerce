package com.e_commerce.shambhu.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "email-verification")
public class EmailVerificationProperties {

    /**
     * Verification token expiration time in milliseconds.
     */
    private long expiration;

    /**
     * Frontend verification URL.
     */
    private String verificationUrl;

    public EmailVerificationProperties() {
    }
}