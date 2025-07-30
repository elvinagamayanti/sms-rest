package com.sms.unit.service;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.config.JwtUtils;
import com.sms.entity.BlacklistedToken;
import com.sms.repository.BlacklistedTokenRepository;
import com.sms.service.TokenBlacklistService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

public class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    private TokenBlacklistService tokenBlacklistService;

    AutoCloseable autoCloseable;
    BlacklistedToken blacklistedToken;
    String validToken;
    String invalidToken;
    Claims claims;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        tokenBlacklistService = new TokenBlacklistService();

        // Use reflection to set the mocked dependencies
        try {
            java.lang.reflect.Field blacklistedTokenRepositoryField = TokenBlacklistService.class
                    .getDeclaredField("blacklistedTokenRepository");
            blacklistedTokenRepositoryField.setAccessible(true);
            blacklistedTokenRepositoryField.set(tokenBlacklistService, blacklistedTokenRepository);

            java.lang.reflect.Field jwtUtilsField = TokenBlacklistService.class.getDeclaredField("jwtUtils");
            jwtUtilsField.setAccessible(true);
            jwtUtilsField.set(tokenBlacklistService, jwtUtils);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        // Setup test data
        validToken = "valid.jwt.token";
        invalidToken = "invalid.jwt.token";

        claims = new DefaultClaims();
        claims.setExpiration(new Date(System.currentTimeMillis() + 3600000)); // 1 hour from now

        blacklistedToken = new BlacklistedToken();
        blacklistedToken.setId(1L);
        blacklistedToken.setToken(validToken);
        blacklistedToken.setBlacklistedAt(LocalDateTime.now());
        blacklistedToken.setExpiresAt(LocalDateTime.now().plusHours(1));
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testBlacklistToken_Success() {
        mock(BlacklistedTokenRepository.class);
        mock(JwtUtils.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);
        when(jwtUtils.parseClaims(validToken)).thenReturn(claims);
        when(blacklistedTokenRepository.save(Mockito.any(BlacklistedToken.class))).thenReturn(blacklistedToken);

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).existsByToken(validToken);
        verify(jwtUtils).parseClaims(validToken);
        verify(blacklistedTokenRepository).save(Mockito.any(BlacklistedToken.class));
    }

    @Test
    void testBlacklistToken_AlreadyBlacklisted() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(true);

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).existsByToken(validToken);
        verify(blacklistedTokenRepository, never()).save(Mockito.any(BlacklistedToken.class));
        verify(jwtUtils, never()).parseClaims(Mockito.any());
    }

    @Test
    void testBlacklistToken_NullToken() {
        mock(BlacklistedTokenRepository.class);

        tokenBlacklistService.blacklistToken(null);

        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
        verify(blacklistedTokenRepository, never()).save(Mockito.any(BlacklistedToken.class));
    }

    @Test
    void testBlacklistToken_EmptyToken() {
        mock(BlacklistedTokenRepository.class);

        tokenBlacklistService.blacklistToken("");

        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
        verify(blacklistedTokenRepository, never()).save(Mockito.any(BlacklistedToken.class));
    }

    @Test
    void testBlacklistToken_WhitespaceToken() {
        mock(BlacklistedTokenRepository.class);

        tokenBlacklistService.blacklistToken("   ");

        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
        verify(blacklistedTokenRepository, never()).save(Mockito.any(BlacklistedToken.class));
    }

    @Test
    void testBlacklistToken_ExceptionHandling() {
        mock(BlacklistedTokenRepository.class);
        mock(JwtUtils.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);
        when(jwtUtils.parseClaims(validToken)).thenThrow(new RuntimeException("JWT parsing error"));

        // Should not throw exception, just log error
        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).existsByToken(validToken);
        verify(jwtUtils).parseClaims(validToken);
        verify(blacklistedTokenRepository, never()).save(Mockito.any(BlacklistedToken.class));
    }

    @Test
    void testIsTokenBlacklisted_True() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertThat(result).isTrue();
        verify(blacklistedTokenRepository).existsByToken(validToken);
    }

    @Test
    void testIsTokenBlacklisted_False() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertThat(result).isFalse();
        verify(blacklistedTokenRepository).existsByToken(validToken);
    }

    @Test
    void testIsTokenBlacklisted_NullToken() {
        mock(BlacklistedTokenRepository.class);

        boolean result = tokenBlacklistService.isTokenBlacklisted(null);

        assertThat(result).isFalse();
        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
    }

    @Test
    void testIsTokenBlacklisted_EmptyToken() {
        mock(BlacklistedTokenRepository.class);

        boolean result = tokenBlacklistService.isTokenBlacklisted("");

        assertThat(result).isFalse();
        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
    }

    @Test
    void testIsTokenBlacklisted_WhitespaceToken() {
        mock(BlacklistedTokenRepository.class);

        boolean result = tokenBlacklistService.isTokenBlacklisted("   ");

        assertThat(result).isFalse();
        verify(blacklistedTokenRepository, never()).existsByToken(Mockito.any());
    }

    @Test
    void testIsTokenBlacklisted_ExceptionHandling() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.existsByToken(validToken)).thenThrow(new RuntimeException("Database error"));

        // Should return false on exception to avoid blocking all requests
        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertThat(result).isFalse();
        verify(blacklistedTokenRepository).existsByToken(validToken);
    }

    @Test
    void testCleanupExpiredTokens_Success() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.deleteExpiredTokens(Mockito.any(LocalDateTime.class))).thenReturn(5);

        tokenBlacklistService.cleanupExpiredTokens();

        verify(blacklistedTokenRepository).deleteExpiredTokens(Mockito.any(LocalDateTime.class));
    }

    @Test
    void testCleanupExpiredTokens_NoTokensDeleted() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.deleteExpiredTokens(Mockito.any(LocalDateTime.class))).thenReturn(0);

        tokenBlacklistService.cleanupExpiredTokens();

        verify(blacklistedTokenRepository).deleteExpiredTokens(Mockito.any(LocalDateTime.class));
    }

    @Test
    void testCleanupExpiredTokens_ExceptionHandling() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.deleteExpiredTokens(Mockito.any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Should not throw exception, just log error
        tokenBlacklistService.cleanupExpiredTokens();

        verify(blacklistedTokenRepository).deleteExpiredTokens(Mockito.any(LocalDateTime.class));
    }

    @Test
    void testGetBlacklistedTokenCount_Success() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.count()).thenReturn(10L);

        long result = tokenBlacklistService.getBlacklistedTokenCount();

        assertThat(result).isEqualTo(10L);
        verify(blacklistedTokenRepository).count();
    }

    @Test
    void testGetBlacklistedTokenCount_ExceptionHandling() {
        mock(BlacklistedTokenRepository.class);

        when(blacklistedTokenRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Should return 0 on exception
        long result = tokenBlacklistService.getBlacklistedTokenCount();

        assertThat(result).isEqualTo(0L);
        verify(blacklistedTokenRepository).count();
    }
}