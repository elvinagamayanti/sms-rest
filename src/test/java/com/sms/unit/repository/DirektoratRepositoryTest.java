package com.sms.unit.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;

@DataJpaTest
public class DirektoratRepositoryTest {

    @Autowired
    private DirektoratRepository direktoratRepository;

    @Autowired
    private DeputiRepository deputiRepository;

    Direktorat direktorat;
    Deputi deputi;

    @BeforeEach
    void setUp() {
        deputi = new Deputi();
        deputi.setName("Test Deputi");
        deputi.setCode("D01");
        deputiRepository.save(deputi);

        direktorat = new Direktorat();
        direktorat.setName("Test Direktorat");
        direktorat.setCode("D0101");
        direktorat.setDeputi(deputi);
        direktoratRepository.save(direktorat);
    }

    @AfterEach
    void tearDown() {
        direktorat = null;
        deputi = null;
        direktoratRepository.deleteAll();
        deputiRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByName_Found() {
        Direktorat foundDirektorat = direktoratRepository.findByName("Test Direktorat");
        assertThat(foundDirektorat).isNotNull();
        assertThat(foundDirektorat.getName()).isEqualTo(direktorat.getName());
        assertThat(foundDirektorat.getCode()).isEqualTo(direktorat.getCode());
        assertThat(foundDirektorat.getDeputi().getName()).isEqualTo(deputi.getName());
    }

    @Test
    public void testFindByCode_Found() {
        Optional<Direktorat> foundDirektorat = direktoratRepository.findByCode("D0101");
        assertThat(foundDirektorat.isPresent()).isTrue();
        assertThat(foundDirektorat.get().getName()).isEqualTo(direktorat.getName());
        assertThat(foundDirektorat.get().getCode()).isEqualTo(direktorat.getCode());
        assertThat(foundDirektorat.get().getDeputi().getName()).isEqualTo(deputi.getName());
    }

    @Test
    public void testExistsByCode_True() {
        boolean exists = direktoratRepository.existsByCode("D0101");
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindByDeputiId_Found() {
        List<Direktorat> foundDirektorats = direktoratRepository.findByDeputiId(deputi.getId());
        assertThat(foundDirektorats).isNotEmpty();
        assertThat(foundDirektorats.size()).isEqualTo(1);
        assertThat(foundDirektorats.get(0).getName()).isEqualTo(direktorat.getName());
    }

    @Test
    public void testFindByDeputiCode_Found() {
        List<Direktorat> foundDirektorats = direktoratRepository.findByDeputiCode("D01");
        assertThat(foundDirektorats).isNotEmpty();
        assertThat(foundDirektorats.size()).isEqualTo(1);
        assertThat(foundDirektorats.get(0).getName()).isEqualTo(direktorat.getName());
    }

    @Test
    public void testSearchDirektorat_Found() {
        List<Direktorat> foundDirektorats = direktoratRepository.searchDirektorat("Test");
        assertThat(foundDirektorats).isNotEmpty();
        assertThat(foundDirektorats.get(0).getName()).isEqualTo(direktorat.getName());
        assertThat(foundDirektorats.get(0).getCode()).isEqualTo(direktorat.getCode());
        assertThat(foundDirektorats.get(0).getDeputi().getName()).isEqualTo(deputi.getName());
    }

    // Test Failure
    @Test
    public void testFindByName_NotFound() {
        Direktorat foundDirektorat = direktoratRepository.findByName("Non Existent Direktorat");
        assertThat(foundDirektorat).isNull();
    }

    @Test
    public void testFindByCode_NotFound() {
        Optional<Direktorat> foundDirektorat = direktoratRepository.findByCode("D0202");
        assertThat(foundDirektorat.isPresent()).isFalse();
    }

    @Test
    public void testExistsByCode_False() {
        boolean exists = direktoratRepository.existsByCode("D0202");
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindByDeputiId_NotFound() {
        List<Direktorat> foundDirektorats = direktoratRepository.findByDeputiId(999L);
        assertThat(foundDirektorats).isEmpty();
    }

    @Test
    public void testFindByDeputiCode_NotFound() {
        List<Direktorat> foundDirektorats = direktoratRepository.findByDeputiCode("D99");
        assertThat(foundDirektorats).isEmpty();
    }

    @Test
    public void testSearchDirektorat_NotFound() {
        List<Direktorat> foundDirektorats = direktoratRepository.searchDirektorat("Non Existent");
        assertThat(foundDirektorats).isEmpty();
    }

    @Test
    public void testSearchDirektorat_EmptyQuery() {
        List<Direktorat> foundDirektorats = direktoratRepository.searchDirektorat("Non Existent");
        assertThat(foundDirektorats).isEmpty();
    }
}
