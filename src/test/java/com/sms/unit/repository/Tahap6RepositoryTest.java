package com.sms.unit.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sms.entity.Kegiatan;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.entity.Tahap6;
import com.sms.repository.Tahap6Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap6RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap6Repository tahap6Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap6 tahap6_kegiatan1;
    private Tahap6 tahap6_kegiatan2;
    private Program program;
    private Output output;

    @BeforeEach
    void setUp() {
        // Setup Program
        program = Program.builder()
                .name("Test Program")
                .code("PROG001")
                .year("2025")
                .build();
        entityManager.persistAndFlush(program);

        // Setup Output
        output = Output.builder()
                .name("Test Output")
                .code("OUT001")
                .year("2025")
                .program(program)
                .build();
        entityManager.persistAndFlush(output);

        // Setup Kegiatan entities
        kegiatan1 = new Kegiatan();
        kegiatan1.setName("Test Kegiatan 1");
        kegiatan1.setCode("KEG001");
        kegiatan1.setStartDate(Date.valueOf(LocalDate.now()));
        kegiatan1.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        kegiatan1.setProgram(program);
        kegiatan1.setOutput(output);
        entityManager.persistAndFlush(kegiatan1);

        kegiatan2 = new Kegiatan();
        kegiatan2.setName("Test Kegiatan 2");
        kegiatan2.setCode("KEG002");
        kegiatan2.setStartDate(Date.valueOf(LocalDate.now()));
        kegiatan2.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        kegiatan2.setProgram(program);
        kegiatan2.setOutput(output);
        entityManager.persistAndFlush(kegiatan2);

        // Setup Tahap6 for kegiatan1 with partial completion (3 out of 5 subtahap)
        tahap6_kegiatan1 = Tahap6.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(3))
                .build();
        entityManager.persistAndFlush(tahap6_kegiatan1);

        // Setup Tahap6 for kegiatan2 with full completion
        tahap6_kegiatan2 = Tahap6.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_4(true)
                .subtahap_5(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap6_kegiatan2);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    // ===============================================
    // Test Cases for findByKegiatanId()
    // ===============================================

    @Test
    void testFindByKegiatanId_Found() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isFalse();
        assertThat(result.get().isSubtahap_3()).isTrue();
        assertThat(result.get().isSubtahap_4()).isFalse();
        assertThat(result.get().isSubtahap_5()).isTrue();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap6 tahap6 = result.get();
        assertThat(tahap6.isSubtahap_1()).isTrue();
        assertThat(tahap6.isSubtahap_2()).isTrue();
        assertThat(tahap6.isSubtahap_3()).isTrue();
        assertThat(tahap6.isSubtahap_4()).isTrue();
        assertThat(tahap6.isSubtahap_5()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap6() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap6 newTahap6 = Tahap6.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(true)
                .subtahap_5(false)
                .subtahap_2_tanggal_perencanaan(LocalDate.now())
                .subtahap_2_tanggal_realisasi(LocalDate.now())
                .subtahap_4_tanggal_perencanaan(LocalDate.now())
                .subtahap_4_tanggal_realisasi(LocalDate.now())
                .build();

        Tahap6 savedTahap6 = tahap6Repository.save(newTahap6);

        assertThat(savedTahap6.getId()).isNotNull();
        assertThat(savedTahap6.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap6.isSubtahap_2()).isTrue();
        assertThat(savedTahap6.isSubtahap_4()).isTrue();
        assertThat(savedTahap6.getCompletionPercentage()).isEqualTo(40); // 2 out of 5 = 40%
    }

    @Test
    void testUpdate_ExistingTahap6() {
        Optional<Tahap6> existing = tahap6Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap6 tahap6 = existing.get();
        tahap6.setSubtahap_2(true);
        tahap6.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap6.setSubtahap_2_tanggal_realisasi(LocalDate.now());

        Tahap6 updatedTahap6 = tahap6Repository.save(tahap6);

        assertThat(updatedTahap6.isSubtahap_2()).isTrue();
        assertThat(updatedTahap6.getCompletionPercentage()).isEqualTo(80); // 4 out of 5 = 80%
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap6> result = tahap6Repository.findById(tahap6_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap6> result = tahap6Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap6> allTahap6 = tahap6Repository.findAll();

        assertThat(allTahap6).hasSize(2);
        assertThat(allTahap6).extracting(tahap6 -> tahap6.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap6_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap6> beforeDeletion = tahap6Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap6Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap6> afterDeletion = tahap6Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap6> remaining = tahap6Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap6 tahap6 = result.get();
        int completionPercentage = tahap6.getCompletionPercentage();

        // 3 out of 5 subtahap completed = 60%
        assertThat(completionPercentage).isEqualTo(60);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap6 tahap6 = result.get();
        int completionPercentage = tahap6.getCompletionPercentage();

        // 5 out of 5 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testTahap6Structure_HasFiveSubtahap() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap6 tahap6 = result.get();

        // Verify Tahap6 has exactly 5 subtahap
        boolean[] subtahapStatus = {
                tahap6.isSubtahap_1(),
                tahap6.isSubtahap_2(),
                tahap6.isSubtahap_3(),
                tahap6.isSubtahap_4(),
                tahap6.isSubtahap_5()
        };

        assertThat(subtahapStatus).hasSize(5);
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithNoCompletion() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("No Completion Test");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap6 tahap6WithNoCompletion = Tahap6.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .build();

        Tahap6 savedTahap6 = tahap6Repository.save(tahap6WithNoCompletion);

        assertThat(savedTahap6.getId()).isNotNull();
        assertThat(savedTahap6.getCompletionPercentage()).isEqualTo(0);
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap6> result = tahap6Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }
}