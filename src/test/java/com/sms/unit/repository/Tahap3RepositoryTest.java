package com.sms.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.sms.entity.Tahap3;
import com.sms.repository.Tahap3Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap3RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap3Repository tahap3Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap3 tahap3_kegiatan1;
    private Tahap3 tahap3_kegiatan2;
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

        // Setup Tahap3 for kegiatan1 with partial completion (4 out of 7 subtahap)
        tahap3_kegiatan1 = Tahap3.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(true)
                .subtahap_5(false)
                .subtahap_6(true)
                .subtahap_7(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_6_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_6_tanggal_realisasi(LocalDate.now().minusDays(3))
                .build();
        entityManager.persistAndFlush(tahap3_kegiatan1);

        // Setup Tahap3 for kegiatan2 with full completion
        tahap3_kegiatan2 = Tahap3.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_4(true)
                .subtahap_5(true)
                .subtahap_6(true)
                .subtahap_7(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(14))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(13))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(12))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(11))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_6_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_6_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_7_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_7_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap3_kegiatan2);
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
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isTrue();
        assertThat(result.get().isSubtahap_3()).isFalse();
        assertThat(result.get().isSubtahap_4()).isTrue();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap3 tahap3 = result.get();
        assertThat(tahap3.isSubtahap_1()).isTrue();
        assertThat(tahap3.isSubtahap_2()).isTrue();
        assertThat(tahap3.isSubtahap_3()).isTrue();
        assertThat(tahap3.isSubtahap_4()).isTrue();
        assertThat(tahap3.isSubtahap_5()).isTrue();
        assertThat(tahap3.isSubtahap_6()).isTrue();
        assertThat(tahap3.isSubtahap_7()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap3() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap3 newTahap3 = Tahap3.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_7(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now())
                .subtahap_1_tanggal_realisasi(LocalDate.now())
                .build();

        Tahap3 savedTahap3 = tahap3Repository.save(newTahap3);

        assertThat(savedTahap3.getId()).isNotNull();
        assertThat(savedTahap3.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap3.isSubtahap_1()).isTrue();
        assertThat(savedTahap3.isSubtahap_3()).isTrue();
        assertThat(savedTahap3.isSubtahap_5()).isTrue();
        assertThat(savedTahap3.isSubtahap_7()).isTrue();
    }

    @Test
    void testUpdate_ExistingTahap3() {
        Optional<Tahap3> existing = tahap3Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap3 tahap3 = existing.get();
        tahap3.setSubtahap_3(true);
        tahap3.setSubtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap3.setSubtahap_3_tanggal_realisasi(LocalDate.now());

        Tahap3 updatedTahap3 = tahap3Repository.save(tahap3);

        assertThat(updatedTahap3.isSubtahap_3()).isTrue();
        assertThat(updatedTahap3.getSubtahap_3_tanggal_perencanaan()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(updatedTahap3.getSubtahap_3_tanggal_realisasi()).isEqualTo(LocalDate.now());
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap3> result = tahap3Repository.findById(tahap3_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap3> result = tahap3Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap3> allTahap3 = tahap3Repository.findAll();

        assertThat(allTahap3).hasSize(2);
        assertThat(allTahap3).extracting(tahap3 -> tahap3.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap3_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap3> beforeDeletion = tahap3Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap3Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap3> afterDeletion = tahap3Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap3> remaining = tahap3Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap3 tahap3 = result.get();
        int completionPercentage = tahap3.getCompletionPercentage();

        // 4 out of 7 subtahap completed = 57%
        assertThat(completionPercentage).isEqualTo(57);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap3 tahap3 = result.get();
        int completionPercentage = tahap3.getCompletionPercentage();

        // 7 out of 7 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testTahap3Structure_HasSevenSubtahap() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap3 tahap3 = result.get();

        // Verify Tahap3 has exactly 7 subtahap
        boolean[] subtahapStatus = {
                tahap3.isSubtahap_1(),
                tahap3.isSubtahap_2(),
                tahap3.isSubtahap_3(),
                tahap3.isSubtahap_4(),
                tahap3.isSubtahap_5(),
                tahap3.isSubtahap_6(),
                tahap3.isSubtahap_7()
        };

        assertThat(subtahapStatus).hasSize(7);
    }

    @Test
    void testSubtahapSequentialCompletion() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap3 tahap3 = result.get();

        // Test that realization dates are sequential (later subtahap completed after
        // earlier ones)
        if (tahap3.isSubtahap_1() && tahap3.isSubtahap_2()) {
            LocalDate realisasi1 = tahap3.getSubtahap_1_tanggal_realisasi();
            LocalDate realisasi2 = tahap3.getSubtahap_2_tanggal_realisasi();

            if (realisasi1 != null && realisasi2 != null) {
                assertThat(realisasi2).isAfterOrEqualTo(realisasi1);
            }
        }
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithMixedSubtahapCompletion() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("Mixed Completion Test");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        // Complete subtahap 1, 3, 5, 7 (odd numbers)
        Tahap3 tahap3WithMixedCompletion = Tahap3.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_7(true)
                .build();

        Tahap3 savedTahap3 = tahap3Repository.save(tahap3WithMixedCompletion);

        assertThat(savedTahap3.getId()).isNotNull();
        assertThat(savedTahap3.getCompletionPercentage()).isEqualTo(57); // 4 out of 7 = 57%
        assertThat(savedTahap3.isSubtahap_1()).isTrue();
        assertThat(savedTahap3.isSubtahap_2()).isFalse();
        assertThat(savedTahap3.isSubtahap_3()).isTrue();
        assertThat(savedTahap3.isSubtahap_4()).isFalse();
        assertThat(savedTahap3.isSubtahap_5()).isTrue();
        assertThat(savedTahap3.isSubtahap_6()).isFalse();
        assertThat(savedTahap3.isSubtahap_7()).isTrue();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap3> result = tahap3Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }
}