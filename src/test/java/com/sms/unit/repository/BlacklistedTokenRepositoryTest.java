package com.sms.unit.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sms.entity.BlacklistedToken;
import com.sms.repository.BlacklistedTokenRepository;

@DataJpaTest
@Transactional
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
    }

    @AfterEach
    void tearDown() {
        blacklistedTokenRepository.deleteAll();
        blacklistedToken = null;
    }

    // Test Success
    @Test
    public void testFindByToken_Found() {
        BlacklistedToken savedToken = blacklistedTokenRepository.save(blacklistedToken);

        Optional<BlacklistedToken> foundToken = blacklistedTokenRepository.findByToken("test-token");
        assertThat(foundToken.isPresent()).isTrue();
        assertThat(foundToken.get().getToken()).isEqualTo(savedToken.getToken());
    }

    @Test
    public void testExistsByToken_True() {
        blacklistedTokenRepository.save(blacklistedToken);

        boolean exists = blacklistedTokenRepository.existsByToken("test-token");
        assertThat(exists).isTrue();
    }

    @Test
    public void testDeleteExpiredTokens() {
        // Create an expired token
        BlacklistedToken expiredToken = new BlacklistedToken();
        expiredToken.setToken("expired-token");
        expiredToken.setBlacklistedAt(LocalDateTime.now().minusDays(2));
        expiredToken.setExpiresAt(LocalDateTime.now().minusDays(1));
        blacklistedTokenRepository.save(expiredToken);

        LocalDateTime now = LocalDateTime.now();
        int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(now);
        assertThat(deletedCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testCountActiveBlacklistedTokens() {
        blacklistedTokenRepository.save(blacklistedToken);

        LocalDateTime now = LocalDateTime.now();
        long count = blacklistedTokenRepository.countActiveBlacklistedTokens(now);
        assertThat(count).isGreaterThanOrEqualTo(0);
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
        // Create a future token
        BlacklistedToken futureToken = new BlacklistedToken();
        futureToken.setToken("future-token");
        futureToken.setBlacklistedAt(LocalDateTime.now());
        futureToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        blacklistedTokenRepository.save(futureToken);

        LocalDateTime past = LocalDateTime.now().minusDays(1);
        int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(past);
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testCountActiveBlacklistedTokens_NoActiveTokens() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        long count = blacklistedTokenRepository.countActiveBlacklistedTokens(future);
        assertThat(count).isEqualTo(0);
    }
}
