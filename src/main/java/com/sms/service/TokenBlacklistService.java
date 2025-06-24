package com.sms.service;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sms.config.JwtUtils;
import com.sms.entity.BlacklistedToken;
import com.sms.repository.BlacklistedTokenRepository;

import io.jsonwebtoken.Claims;

@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public void blacklistToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Attempted to blacklist null or empty token");
            return;
        }

        try {
            // Cek apakah token sudah ada di blacklist
            if (blacklistedTokenRepository.existsByToken(token)) {
                logger.info("Token already blacklisted");
                return;
            }

            // Parse token untuk mendapatkan expiration time
            Claims claims = jwtUtils.parseClaims(token);
            Date expiration = claims.getExpiration();
            LocalDateTime expiresAt = expiration.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiresAt);
            blacklistedTokenRepository.save(blacklistedToken);

            logger.info("Token successfully blacklisted, expires at: {}", expiresAt);

        } catch (Exception e) {
            logger.error("Error blacklisting token: {}", e.getMessage(), e);
            // Jangan throw exception, biarkan logout tetap berjalan
        }
    }

    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            return blacklistedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            logger.error("Error checking if token is blacklisted: {}", e.getMessage(), e);
            // Jika ada error, anggap token tidak di-blacklist untuk menghindari blocking
            // semua request
            return false;
        }
    }

    // Scheduled task untuk membersihkan token yang sudah expired setiap hari
    @Scheduled(fixedRate = 86400000) // 24 hours
    public void cleanupExpiredTokens() {
        try {
            int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            if (deletedCount > 0) {
                logger.info("Cleaned up {} expired blacklisted tokens", deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }

    // Method untuk mendapatkan jumlah total token di blacklist (untuk monitoring)
    public long getBlacklistedTokenCount() {
        try {
            return blacklistedTokenRepository.count();
        } catch (Exception e) {
            logger.error("Error getting blacklisted token count: {}", e.getMessage(), e);
            return 0;
        }
    }
}