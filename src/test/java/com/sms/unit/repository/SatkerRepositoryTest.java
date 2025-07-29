package com.sms.unit.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;

@DataJpaTest
public class SatkerRepositoryTest {

    @Autowired
    private SatkerRepository satkerRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    Satker satker;
    Satker satkerPusat;
    Province province;
    Province provincePusat;

    @BeforeEach
    void setUp() {
        province = new Province();
        province.setName("Test Province");
        province.setCode("01");
        provinceRepository.save(province);

        provincePusat = new Province();
        provincePusat.setName("Pusat");
        provincePusat.setCode("00");
        provinceRepository.save(provincePusat);

        satker = new Satker();
        satker.setName("Test Satker");
        satker.setCode("0100");
        satker.setAddress("123 Test Street");
        satker.setNumber("12345");
        satker.setEmail("Test@email.com");
        satker.setProvince(province);
        satker.setIsProvince(true);
        satkerRepository.save(satker);

        satkerPusat = new Satker();
        satkerPusat.setName("Pusat Satker");
        satkerPusat.setCode("0000");
        satkerPusat.setAddress("123 Pusat Street");
        satkerPusat.setNumber("54321");
        satkerPusat.setEmail("pusat@email.com");
        satkerPusat.setProvince(provincePusat);
        satkerPusat.setIsProvince(true);
        satkerRepository.save(satkerPusat);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test if necessary
        satker = null;
        province = null;
        satkerPusat = null;
        provincePusat = null;
        provinceRepository.deleteAll();
        satkerRepository.deleteAll();
    }

    // Test Case Success

    @Test
    public void testFindByCode_Found() {
        Optional<Satker> foundSatker = satkerRepository.findByCode("0100");
        assertTrue(foundSatker.isPresent());
        assertThat(foundSatker.get().getName()).isEqualTo(satker.getName());
        assertThat(foundSatker.get().getCode()).isEqualTo(satker.getCode());
        assertThat(foundSatker.get().getAddress()).isEqualTo(satker.getAddress());
        assertThat(foundSatker.get().getNumber()).isEqualTo(satker.getNumber());
        assertThat(foundSatker.get().getEmail()).isEqualTo(satker.getEmail());
        assertThat(foundSatker.get().getProvince().getName()).isEqualTo(province.getName());
    }

    @Test
    public void testSearchSatker_Found() {
        List<Satker> foundSatkers = satkerRepository.searchSatker("Test");
        assertThat(foundSatkers).isNotEmpty();
        assertThat(foundSatkers.get(0).getId()).isEqualTo(satker.getId());
        assertThat(foundSatkers.get(0).getName()).isEqualTo(satker.getName());
        assertThat(foundSatkers.get(0).getCode()).isEqualTo(satker.getCode());
        assertThat(foundSatkers.get(0).getAddress()).isEqualTo(satker.getAddress());
        assertThat(foundSatkers.get(0).getNumber()).isEqualTo(satker.getNumber());
        assertThat(foundSatkers.get(0).getEmail()).isEqualTo(satker.getEmail());
        assertThat(foundSatkers.get(0).getProvince().getName()).isEqualTo(province.getName());
    }

    @Test
    public void testFindByCodeStartingWith_Found() {
        List<Satker> foundSatkers = satkerRepository.findByCodeStartingWith("01");
        assertThat(foundSatkers).isNotEmpty();
        assertThat(foundSatkers.get(0).getName()).isEqualTo(satker.getName());
        assertThat(foundSatkers.get(0).getCode()).isEqualTo(satker.getCode());
        assertThat(foundSatkers.get(0).getAddress()).isEqualTo(satker.getAddress());
        assertThat(foundSatkers.get(0).getNumber()).isEqualTo(satker.getNumber());
        assertThat(foundSatkers.get(0).getEmail()).isEqualTo(satker.getEmail());
        assertThat(foundSatkers.get(0).getProvince().getName()).isEqualTo(province.getName());
    }

    @Test
    public void testFindByIsProvince_Found() {
        List<Satker> foundSatkers = satkerRepository.findByIsProvince(true);
        assertThat(foundSatkers).isNotEmpty();
        assertThat(foundSatkers.get(0).getName()).isEqualTo(satker.getName());
        assertThat(foundSatkers.get(0).getCode()).isEqualTo(satker.getCode());
        assertThat(foundSatkers.get(0).getAddress()).isEqualTo(satker.getAddress());
        assertThat(foundSatkers.get(0).getNumber()).isEqualTo(satker.getNumber());
        assertThat(foundSatkers.get(0).getEmail()).isEqualTo(satker.getEmail());
        assertThat(foundSatkers.get(0).getProvince().getName()).isEqualTo(province.getName());
    }

    @Test
    public void testFindPusatSatkers_Found() {
        List<Satker> foundSatkers = satkerRepository.findPusatSatkers();
        assertThat(foundSatkers).isNotEmpty();
        assertThat(foundSatkers.get(0).getCode()).isEqualTo(satkerPusat.getCode());
        assertThat(foundSatkers.get(0).getName()).isEqualTo(satkerPusat.getName());
        assertThat(foundSatkers.get(0).getAddress()).isEqualTo(satkerPusat.getAddress());
        assertThat(foundSatkers.get(0).getNumber()).isEqualTo(satkerPusat.getNumber());
        assertThat(foundSatkers.get(0).getEmail()).isEqualTo(satkerPusat.getEmail());
        assertThat(foundSatkers.get(0).getProvince().getName()).isEqualTo(provincePusat.getName());
    }

    @Test
    public void testFindProvinsiSatkersOnly_Found() {
        List<Satker> foundSatkers = satkerRepository.findProvinsiSatkersOnly();
        assertThat(foundSatkers).isNotEmpty();
        assertThat(foundSatkers.get(0).getCode()).isNotEqualTo("0000");
        assertThat(foundSatkers.get(0).getIsProvince()).isTrue();
        assertThat(foundSatkers.get(0).getName()).isEqualTo(satker.getName());
        assertThat(foundSatkers.get(0).getAddress()).isEqualTo(satker.getAddress());
        assertThat(foundSatkers.get(0).getNumber()).isEqualTo(satker.getNumber());
        assertThat(foundSatkers.get(0).getEmail()).isEqualTo(satker.getEmail());
        assertThat(foundSatkers.get(0).getProvince().getName()).isEqualTo(province.getName());
    }

    // Test Case Failure

    @Test
    public void testFindByCode_NotFound() {
        Optional<Satker> foundSatker = satkerRepository.findByCode("9999");
        assertTrue(foundSatker.isEmpty());
    }

    @Test
    public void testSearchSatker_NotFound() {
        List<Satker> foundSatkers = satkerRepository.searchSatker("NonExistent");
        assertThat(foundSatkers).isEmpty();
    }

    @Test
    public void testFindByCodeStartingWith_NotFound() {
        List<Satker> foundSatkers = satkerRepository.findByCodeStartingWith("99");
        assertThat(foundSatkers).isEmpty();
    }

    @Test
    public void testFindByIsProvince_NotFound() {
        List<Satker> foundSatkers = satkerRepository.findByIsProvince(false);
        assertThat(foundSatkers).isEmpty();
    }

}
