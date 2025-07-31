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
import com.sms.entity.Tahap4;
import com.sms.repository.Tahap4Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap4RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap4Repository tahap4Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap4 tahap4_kegiatan1;
    private Tahap4 tahap4_kegiatan2;
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

        // Setup Tahap4 for kegiatan1 with partial completion (2 out of 4 subtahap)
        tahap4_kegiatan1 = Tahap4.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap4_kegiatan1);

        // Setup Tahap4 for kegiatan1 with partial completion (2 out of 4 subtahap)
        tahap4_kegiatan2 = Tahap4.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_4(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap4_kegiatan2);
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
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isTrue();
        assertThat(result.get().isSubtahap_3()).isFalse();
        assertThat(result.get().isSubtahap_4()).isFalse();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap4 tahap4 = result.get();
        assertThat(tahap4.isSubtahap_1()).isTrue();
        assertThat(tahap4.isSubtahap_2()).isTrue();
        assertThat(tahap4.isSubtahap_3()).isTrue();
        assertThat(tahap4.isSubtahap_4()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap4() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap4 newTahap4 = Tahap4.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(true)
                .subtahap_2_tanggal_perencanaan(LocalDate.now())
                .subtahap_2_tanggal_realisasi(LocalDate.now())
                .subtahap_4_tanggal_perencanaan(LocalDate.now())
                .subtahap_4_tanggal_realisasi(LocalDate.now())
                .build();

        Tahap4 savedTahap4 = tahap4Repository.save(newTahap4);

        assertThat(savedTahap4.getId()).isNotNull();
        assertThat(savedTahap4.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap4.isSubtahap_1()).isFalse();
        assertThat(savedTahap4.isSubtahap_2()).isTrue();
        assertThat(savedTahap4.isSubtahap_3()).isFalse();
        assertThat(savedTahap4.isSubtahap_4()).isTrue();
    }

    @Test
    void testUpdate_ExistingTahap4() {
        Optional<Tahap4> existing = tahap4Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap4 tahap4 = existing.get();
        tahap4.setSubtahap_2(true);
        tahap4.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap4.setSubtahap_2_tanggal_realisasi(LocalDate.now());

        Tahap4 updatedTahap4 = tahap4Repository.save(tahap4);

        assertThat(updatedTahap4.isSubtahap_2()).isTrue();
        assertThat(updatedTahap4.getSubtahap_2_tanggal_perencanaan()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(updatedTahap4.getSubtahap_2_tanggal_realisasi()).isEqualTo(LocalDate.now());
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap4> result = tahap4Repository.findById(tahap4_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap4> result = tahap4Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap4> allTahap4 = tahap4Repository.findAll();

        assertThat(allTahap4).hasSize(2);
        assertThat(allTahap4).extracting(tahap4 -> tahap4.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap4_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap4> beforeDeletion = tahap4Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap4Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap4> afterDeletion = tahap4Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap4> remaining = tahap4Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap4 tahap4 = result.get();
        int completionPercentage = tahap4.getCompletionPercentage();

        // 2 out of 4 subtahap completed = 50%
        assertThat(completionPercentage).isEqualTo(50);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap4 tahap4 = result.get();
        int completionPercentage = tahap4.getCompletionPercentage();

        // 4 out of 4 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testTahap4Structure_HasFourSubtahap() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap4 tahap4 = result.get();

        // Verify Tahap4 has exactly 4 subtahap
        boolean[] subtahapStatus = {
                tahap4.isSubtahap_1(),
                tahap4.isSubtahap_2(),
                tahap4.isSubtahap_3(),
                tahap4.isSubtahap_4()
        };

        assertThat(subtahapStatus).hasSize(4);
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap4 tahap4 = result.get();

        // Completed subtahap should have both planning and realization dates
        if (tahap4.isSubtahap_1()) {
            assertThat(tahap4.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap4.getSubtahap_1_tanggal_realisasi()).isNotNull();
            assertThat(tahap4.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap4.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap4.isSubtahap_3()) {
            assertThat(tahap4.getSubtahap_3_tanggal_perencanaan()).isNotNull();
            assertThat(tahap4.getSubtahap_3_tanggal_realisasi()).isNotNull();
            assertThat(tahap4.getSubtahap_3_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap4.getSubtahap_3_tanggal_perencanaan());
        }
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithAllSubtahapFalse() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("No Completion Test");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap4 tahap4WithNoCompletion = Tahap4.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .build();

        Tahap4 savedTahap4 = tahap4Repository.save(tahap4WithNoCompletion);

        assertThat(savedTahap4.getId()).isNotNull();
        assertThat(savedTahap4.getCompletionPercentage()).isEqualTo(0);
        assertThat(savedTahap4.isSubtahap_1()).isFalse();
        assertThat(savedTahap4.isSubtahap_2()).isFalse();
        assertThat(savedTahap4.isSubtahap_3()).isFalse();
        assertThat(savedTahap4.isSubtahap_4()).isFalse();
    }

    @Test
    void testUpdate_CompleteRemainingSubtahap() {
        Optional<Tahap4> existing = tahap4Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap4 tahap4 = existing.get();

        // Complete the remaining subtahap (2 and 4)
        tahap4.setSubtahap_3(true);
        tahap4.setSubtahap_4(true);

        LocalDate planningDate = LocalDate.now().minusDays(1);
        LocalDate realizationDate = LocalDate.now();

        tahap4.setSubtahap_2_tanggal_perencanaan(planningDate);
        tahap4.setSubtahap_2_tanggal_realisasi(realizationDate);
        tahap4.setSubtahap_4_tanggal_perencanaan(planningDate);
        tahap4.setSubtahap_4_tanggal_realisasi(realizationDate);

        Tahap4 updatedTahap4 = tahap4Repository.save(tahap4);

        assertThat(updatedTahap4.getCompletionPercentage()).isEqualTo(100);
        assertThat(updatedTahap4.isSubtahap_1()).isTrue();
        assertThat(updatedTahap4.isSubtahap_2()).isTrue();
        assertThat(updatedTahap4.isSubtahap_3()).isTrue();
        assertThat(updatedTahap4.isSubtahap_4()).isTrue();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }

    @Test
    void testTahap4_SmallestSubtahapCount() {
        // Tahap4 has the smallest number of subtahap (4), test edge case
        Optional<Tahap4> result = tahap4Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap4 tahap4 = result.get();

        // Verify that completing all 4 subtahap gives 100%
        assertThat(tahap4.getCompletionPercentage()).isEqualTo(100);

        // Verify each individual subtahap
        assertThat(tahap4.isSubtahap_1()).isTrue();
        assertThat(tahap4.isSubtahap_2()).isTrue();
        assertThat(tahap4.isSubtahap_3()).isTrue();
        assertThat(tahap4.isSubtahap_4()).isTrue();

        // Verify all dates are present for completed subtahap
        assertThat(tahap4.getSubtahap_1_tanggal_perencanaan()).isNotNull();
        assertThat(tahap4.getSubtahap_1_tanggal_realisasi()).isNotNull();
        assertThat(tahap4.getSubtahap_2_tanggal_perencanaan()).isNotNull();
        assertThat(tahap4.getSubtahap_2_tanggal_realisasi()).isNotNull();
        assertThat(tahap4.getSubtahap_3_tanggal_perencanaan()).isNotNull();
        assertThat(tahap4.getSubtahap_3_tanggal_realisasi()).isNotNull();
        assertThat(tahap4.getSubtahap_4_tanggal_perencanaan()).isNotNull();
        assertThat(tahap4.getSubtahap_4_tanggal_realisasi()).isNotNull();
    }
}