package com.sms.unit.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Province;
import com.sms.repository.ProvinceRepository;

@DataJpaTest
public class ProvinceRepositoryTest {

    @Autowired
    private ProvinceRepository provinceRepository;

    Province province;

    @BeforeEach
    void setUp() {
        province = new Province();
        province.setName("Test Province");
        province.setCode("01");
        provinceRepository.save(province);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test if necessary
        province = null;
        provinceRepository.deleteAll();
    }

    // Test Success

    @Test
    public void testFindByName_Found() {
        Province foundProvince = provinceRepository.findByName("Test Province");
        assertThat(foundProvince).isNotNull();
        assertThat(foundProvince.getName()).isEqualTo(province.getName());
        assertThat(foundProvince.getCode()).isEqualTo(province.getCode());
    }

    @Test
    public void testFindByCode_Found() {
        Optional<Province> foundProvince = provinceRepository.findByCode("01");
        assertThat(foundProvince.isPresent()).isTrue();
        assertThat(foundProvince.get().getName()).isEqualTo(province.getName());
        assertThat(foundProvince.get().getCode()).isEqualTo(province.getCode());
    }

    // Test Failure

    @Test
    public void testFindByName_NotFound() {
        Province foundProvince = provinceRepository.findByName("Non Existent Province");
        assertThat(foundProvince).isNull();
    }

    @Test
    public void testFindByCode_NotFound() {
        Optional<Province> foundProvince = provinceRepository.findByCode("02");
        assertThat(foundProvince.isPresent()).isFalse();
    }
}
