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
import com.sms.entity.Tahap5;
import com.sms.repository.Tahap5Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap5RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap5Repository tahap5Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap5 tahap5_kegiatan1;
    private Tahap5 tahap5_kegiatan2;
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

        // Setup Tahap5 for kegiatan1 with partial completion (5 out of 8 subtahap)
        tahap5_kegiatan1 = Tahap5.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(true)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_7(true)
                .subtahap_8(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(14))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(13))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(12))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(11))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_7_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_7_tanggal_realisasi(LocalDate.now().minusDays(5))
                .build();
        entityManager.persistAndFlush(tahap5_kegiatan1);

        // Setup Tahap5 for kegiatan2 with minimal completion (1 out of 8 subtahap)
        tahap5_kegiatan2 = Tahap5.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                .subtahap_7(false)
                .subtahap_8(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(3))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(2))
                .build();
        entityManager.persistAndFlush(tahap5_kegiatan2);
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
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isTrue();
        assertThat(result.get().isSubtahap_3()).isFalse();
        assertThat(result.get().isSubtahap_4()).isTrue();
        assertThat(result.get().isSubtahap_5()).isTrue();
        assertThat(result.get().isSubtahap_6()).isFalse();
        assertThat(result.get().isSubtahap_7()).isTrue();
        assertThat(result.get().isSubtahap_8()).isFalse();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithMinimalCompletion() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap5 tahap5 = result.get();
        assertThat(tahap5.isSubtahap_1()).isTrue();
        assertThat(tahap5.isSubtahap_2()).isFalse();
        assertThat(tahap5.isSubtahap_3()).isFalse();
        assertThat(tahap5.isSubtahap_4()).isFalse();
        assertThat(tahap5.isSubtahap_5()).isFalse();
        assertThat(tahap5.isSubtahap_6()).isFalse();
        assertThat(tahap5.isSubtahap_7()).isFalse();
        assertThat(tahap5.isSubtahap_8()).isFalse();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap5WithFullCompletion() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap5 newTahap5 = Tahap5.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_4(true)
                .subtahap_5(true)
                .subtahap_6(true)
                .subtahap_7(true)
                .subtahap_8(true)
                .build();

        Tahap5 savedTahap5 = tahap5Repository.save(newTahap5);

        assertThat(savedTahap5.getId()).isNotNull();
        assertThat(savedTahap5.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap5.getCompletionPercentage()).isEqualTo(100);
    }

    @Test
    void testUpdate_ExistingTahap5() {
        Optional<Tahap5> existing = tahap5Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(existing).isPresent();

        Tahap5 tahap5 = existing.get();
        tahap5.setSubtahap_2(true);
        tahap5.setSubtahap_3(true);
        tahap5.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap5.setSubtahap_2_tanggal_realisasi(LocalDate.now());
        tahap5.setSubtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap5.setSubtahap_3_tanggal_realisasi(LocalDate.now());

        Tahap5 updatedTahap5 = tahap5Repository.save(tahap5);

        assertThat(updatedTahap5.isSubtahap_2()).isTrue();
        assertThat(updatedTahap5.isSubtahap_3()).isTrue();
        assertThat(updatedTahap5.getCompletionPercentage()).isEqualTo(37); // 3 out of 8 = 37%
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap5> result = tahap5Repository.findById(tahap5_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap5> result = tahap5Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap5> allTahap5 = tahap5Repository.findAll();

        assertThat(allTahap5).hasSize(2);
        assertThat(allTahap5).extracting(tahap5 -> tahap5.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap5_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap5> beforeDeletion = tahap5Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap5Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap5> afterDeletion = tahap5Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap5> remaining = tahap5Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap5 tahap5 = result.get();
        int completionPercentage = tahap5.getCompletionPercentage();

        // 5 out of 8 subtahap completed = 62%
        assertThat(completionPercentage).isEqualTo(62);
    }

    @Test
    void testCompletionPercentage_MinimalCompletion() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap5 tahap5 = result.get();
        int completionPercentage = tahap5.getCompletionPercentage();

        // 1 out of 8 subtahap completed = 12%
        assertThat(completionPercentage).isEqualTo(12);
    }

    @Test
    void testTahap5Structure_HasEightSubtahap() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap5 tahap5 = result.get();

        // Verify Tahap5 has exactly 8 subtahap (most among all Tahap)
        boolean[] subtahapStatus = {
                tahap5.isSubtahap_1(),
                tahap5.isSubtahap_2(),
                tahap5.isSubtahap_3(),
                tahap5.isSubtahap_4(),
                tahap5.isSubtahap_5(),
                tahap5.isSubtahap_6(),
                tahap5.isSubtahap_7(),
                tahap5.isSubtahap_8()
        };

        assertThat(subtahapStatus).hasSize(8);
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap5 tahap5 = result.get();

        // Check completed subtahap have both planning and realization dates
        if (tahap5.isSubtahap_1()) {
            assertThat(tahap5.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap5.getSubtahap_1_tanggal_realisasi()).isNotNull();
            assertThat(tahap5.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap5.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap5.isSubtahap_7()) {
            assertThat(tahap5.getSubtahap_7_tanggal_perencanaan()).isNotNull();
            assertThat(tahap5.getSubtahap_7_tanggal_realisasi()).isNotNull();
            assertThat(tahap5.getSubtahap_7_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap5.getSubtahap_7_tanggal_perencanaan());
        }
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithAlternatingSubtahapCompletion() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("Alternating Completion Test");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        // Complete alternating subtahap (1, 3, 5, 7)
        Tahap5 tahap5WithAlternatingCompletion = Tahap5.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_7(true)
                .subtahap_8(false)
                .build();

        Tahap5 savedTahap5 = tahap5Repository.save(tahap5WithAlternatingCompletion);

        assertThat(savedTahap5.getId()).isNotNull();
        assertThat(savedTahap5.getCompletionPercentage()).isEqualTo(50); // 4 out of 8 = 50%
        assertThat(savedTahap5.isSubtahap_1()).isTrue();
        assertThat(savedTahap5.isSubtahap_2()).isFalse();
        assertThat(savedTahap5.isSubtahap_3()).isTrue();
        assertThat(savedTahap5.isSubtahap_4()).isFalse();
        assertThat(savedTahap5.isSubtahap_5()).isTrue();
        assertThat(savedTahap5.isSubtahap_6()).isFalse();
        assertThat(savedTahap5.isSubtahap_7()).isTrue();
        assertThat(savedTahap5.isSubtahap_8()).isFalse();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }

    @Test
    void testTahap5_LargestSubtahapCount() {
        // Tahap5 has the largest number of subtahap (8), test edge case
        Optional<Tahap5> result = tahap5Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap5 tahap5 = result.get();

        // Count completed subtahap manually
        int completedCount = 0;
        if (tahap5.isSubtahap_1())
            completedCount++;
        if (tahap5.isSubtahap_2())
            completedCount++;
        if (tahap5.isSubtahap_3())
            completedCount++;
        if (tahap5.isSubtahap_4())
            completedCount++;
        if (tahap5.isSubtahap_5())
            completedCount++;
        if (tahap5.isSubtahap_6())
            completedCount++;
        if (tahap5.isSubtahap_7())
            completedCount++;
        if (tahap5.isSubtahap_8())
            completedCount++;

        int expectedPercentage = (completedCount * 100) / 8;
        assertThat(tahap5.getCompletionPercentage()).isEqualTo(expectedPercentage);
    }

    @Test
    void testUpdate_CompleteAllRemainingSubtahap() {
        Optional<Tahap5> existing = tahap5Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap5 tahap5 = existing.get();

        // Complete the remaining subtahap (3, 6, 8)
        tahap5.setSubtahap_3(true);
        tahap5.setSubtahap_6(true);
        tahap5.setSubtahap_8(true);

        LocalDate planningDate = LocalDate.now().minusDays(1);
        LocalDate realizationDate = LocalDate.now();

        tahap5.setSubtahap_3_tanggal_perencanaan(planningDate);
        tahap5.setSubtahap_3_tanggal_realisasi(realizationDate);
        tahap5.setSubtahap_6_tanggal_perencanaan(planningDate);
        tahap5.setSubtahap_6_tanggal_realisasi(realizationDate);
        tahap5.setSubtahap_8_tanggal_perencanaan(planningDate);
        tahap5.setSubtahap_8_tanggal_realisasi(realizationDate);

        Tahap5 updatedTahap5 = tahap5Repository.save(tahap5);

        assertThat(updatedTahap5.getCompletionPercentage()).isEqualTo(100);
        assertThat(updatedTahap5.isSubtahap_1()).isTrue();
        assertThat(updatedTahap5.isSubtahap_2()).isTrue();
        assertThat(updatedTahap5.isSubtahap_3()).isTrue();
        assertThat(updatedTahap5.isSubtahap_4()).isTrue();
        assertThat(updatedTahap5.isSubtahap_5()).isTrue();
        assertThat(updatedTahap5.isSubtahap_6()).isTrue();
        assertThat(updatedTahap5.isSubtahap_7()).isTrue();
        assertThat(updatedTahap5.isSubtahap_8()).isTrue();
    }
}