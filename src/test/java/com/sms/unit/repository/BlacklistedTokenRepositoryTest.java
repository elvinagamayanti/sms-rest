package com.sms.unit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.BlacklistedToken;
import com.sms.repository.BlacklistedTokenRepository;

@DataJpaTest
public class BlacklistedTokenRepositoryTest {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    BlacklistedToken blacklistedToken;

    @BeforeEach
    void setUp() {
        blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken("test-token");
        blacklistedToken.setBlacklistedAt(LocalDateTime.now());
        blacklistedToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        blacklistedTokenRepository.save(blacklistedToken);
    }

    @AfterEach
    void tearDown() {
        blacklistedToken = null;
        blacklistedTokenRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByToken_Found() {
        Optional<BlacklistedToken> foundToken = blacklistedTokenRepository.findByToken("test-token");
        assertThat(foundToken.isPresent()).isTrue();
        assertThat(foundToken.get().getToken()).isEqualTo(blacklistedToken.getToken());
    }

    @Test
    public void testExistsByToken_True() {
        boolean exists = blacklistedTokenRepository.existsByToken("test-token");
        assertThat(exists).isTrue();
    }

    @Test
    public void testDeleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(now);
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testCountActiveBlacklistedTokens() {
        LocalDateTime now = LocalDateTime.now();
        long count = blacklistedTokenRepository.countActiveBlacklistedTokens(now);
        assertThat(count).isEqualTo(1);
    }

    // Test Failure
    @Test
    public void testFindByToken_NotFound() {
        Optional<BlacklistedToken> foundToken = blacklistedTokenRepository.findByToken("non-existent-token");
        assertThat(foundToken.isPresent()).isFalse();
    }

    @Test
    public void testExistsByToken_False() {
        boolean exists = blacklistedTokenRepository.existsByToken("non-existent-token");
        assertThat(exists).isFalse();
    }

    @Test
    public void testDeleteExpiredTokens_NoExpiredTokens() {
        LocalDateTime now = LocalDateTime.now(); // Set future time
        int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(now);
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testCountActiveBlacklistedTokens_NoActiveTokens() {
        LocalDateTime now = LocalDateTime.now().plusDays(1); // Set future time
        long count = blacklistedTokenRepository.countActiveBlacklistedTokens(now);
        assertThat(count).isEqualTo(0); // No active tokens should be counted
    }
}
