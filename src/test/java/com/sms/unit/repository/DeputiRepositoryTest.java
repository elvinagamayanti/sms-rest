package com.sms.unit.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Deputi;
import com.sms.repository.DeputiRepository;

@DataJpaTest
public class DeputiRepositoryTest {

    @Autowired
    private DeputiRepository deputiRepository;

    Deputi deputi;

    @BeforeEach
    void setUp() {
        deputi = new Deputi();
        deputi.setName("Test Deputi");
        deputi.setCode("D01");
        deputiRepository.save(deputi);
    }

    @AfterEach
    void tearDown() {
        deputi = null;
        deputiRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByName_Found() {
        Deputi foundDeputi = deputiRepository.findByName("Test Deputi");
        assertThat(foundDeputi).isNotNull();
        assertThat(foundDeputi.getName()).isEqualTo(deputi.getName());
        assertThat(foundDeputi.getCode()).isEqualTo(deputi.getCode());
    }

    @Test
    public void testFindByCode_Found() {
        Optional<Deputi> foundDeputi = deputiRepository.findByCode("D01");
        assertThat(foundDeputi.isPresent()).isTrue();
        assertThat(foundDeputi.get().getName()).isEqualTo(deputi.getName());
        assertThat(foundDeputi.get().getCode()).isEqualTo(deputi.getCode());
    }

    @Test
    public void testExistsByCode_True() {
        boolean exists = deputiRepository.existsByCode("D01");
        assertThat(exists).isTrue();
    }

    // Test Failure
    @Test
    public void testFindByName_NotFound() {
        Deputi foundDeputi = deputiRepository.findByName("Non Existent Deputi");
        assertThat(foundDeputi).isNull();
    }

    @Test
    public void testFindByCode_NotFound() {
        Optional<Deputi> foundDeputi = deputiRepository.findByCode("D02");
        assertThat(foundDeputi.isPresent()).isFalse();
    }

    @Test
    public void testExistsByCode_False() {
        boolean exists = deputiRepository.existsByCode("D02");
        assertThat(exists).isFalse();
    }
}
