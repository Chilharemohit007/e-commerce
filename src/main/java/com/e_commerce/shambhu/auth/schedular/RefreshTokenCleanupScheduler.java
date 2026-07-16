package com.e_commerce.shambhu.auth.schedular;

import com.e_commerce.shambhu.auth.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenCleanupScheduler(
            RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        refreshTokenService.deleteExpiredTokens();
    }
}