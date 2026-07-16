package com.e_commerce.shambhu.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "password-reset")
public class PasswordResetProperties {

    /**
     * Expiration time in milliseconds.
     */
    private long expiration;

    /**
     * Frontend reset password URL.
     */
    private String resetUrl;

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getResetUrl() {
        return resetUrl;
    }

    public void setResetUrl(String resetUrl) {
        this.resetUrl = resetUrl;
    }
}