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
import com.sms.entity.Tahap1;
import com.sms.repository.Tahap1Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap1RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap1Repository tahap1Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap1 tahap1_kegiatan1;
    private Tahap1 tahap1_kegiatan2;
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
        kegiatan1.setStartDate(java.sql.Date.valueOf(LocalDate.now()));
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

        // Setup Tahap1 for kegiatan1 with mixed completion status
        tahap1_kegiatan1 = Tahap1.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(8))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(5))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap1_kegiatan1);

        // Setup Tahap1 for kegiatan2 with all subtahap completed
        tahap1_kegiatan2 = Tahap1.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_4(true)
                .subtahap_5(true)
                .subtahap_6(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(12))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(11))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_4_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_4_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_6_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_6_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap1_kegiatan2);
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
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isFalse();
        assertThat(result.get().isSubtahap_3()).isTrue();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap1 tahap1 = result.get();
        assertThat(tahap1.isSubtahap_1()).isTrue();
        assertThat(tahap1.isSubtahap_2()).isTrue();
        assertThat(tahap1.isSubtahap_3()).isTrue();
        assertThat(tahap1.isSubtahap_4()).isTrue();
        assertThat(tahap1.isSubtahap_5()).isTrue();
        assertThat(tahap1.isSubtahap_6()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap1() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(java.sql.Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusDays(30)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap1 newTahap1 = Tahap1.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                .build();

        Tahap1 savedTahap1 = tahap1Repository.save(newTahap1);

        assertThat(savedTahap1.getId()).isNotNull();
        assertThat(savedTahap1.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap1.isSubtahap_1()).isFalse();
    }

    @Test
    void testUpdate_ExistingTahap1() {
        Optional<Tahap1> existing = tahap1Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap1 tahap1 = existing.get();
        tahap1.setSubtahap_2(true);
        tahap1.setSubtahap_2_tanggal_perencanaan(LocalDate.now());
        tahap1.setSubtahap_2_tanggal_realisasi(LocalDate.now());

        Tahap1 updatedTahap1 = tahap1Repository.save(tahap1);

        assertThat(updatedTahap1.isSubtahap_2()).isTrue();
        assertThat(updatedTahap1.getSubtahap_2_tanggal_perencanaan()).isEqualTo(LocalDate.now());
        assertThat(updatedTahap1.getSubtahap_2_tanggal_realisasi()).isEqualTo(LocalDate.now());
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap1> result = tahap1Repository.findById(tahap1_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap1> result = tahap1Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap1> allTahap1 = tahap1Repository.findAll();

        assertThat(allTahap1).hasSize(2);
        assertThat(allTahap1).extracting(tahap1 -> tahap1.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap1_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap1> beforeDeletion = tahap1Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap1Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap1> afterDeletion = tahap1Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap1> remaining = tahap1Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap1 tahap1 = result.get();
        int completionPercentage = tahap1.getCompletionPercentage();

        // 3 out of 6 subtahap completed = 50%
        assertThat(completionPercentage).isEqualTo(50);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap1 tahap1 = result.get();
        int completionPercentage = tahap1.getCompletionPercentage();

        // 6 out of 6 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap1 tahap1 = result.get();

        // Completed subtahap should have both planning and realization dates
        if (tahap1.isSubtahap_1()) {
            assertThat(tahap1.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap1.getSubtahap_1_tanggal_realisasi()).isNotNull();
            // Realization date should be after or equal to planning date
            assertThat(tahap1.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap1.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap1.isSubtahap_3()) {
            assertThat(tahap1.getSubtahap_3_tanggal_perencanaan()).isNotNull();
            assertThat(tahap1.getSubtahap_3_tanggal_realisasi()).isNotNull();
            assertThat(tahap1.getSubtahap_3_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap1.getSubtahap_3_tanggal_perencanaan());
        }
    }

    @Test
    void testTahap1Structure_HasSixSubtahap() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap1 tahap1 = result.get();

        // Verify Tahap1 has exactly 6 subtahap
        boolean[] subtahapStatus = {
                tahap1.isSubtahap_1(),
                tahap1.isSubtahap_2(),
                tahap1.isSubtahap_3(),
                tahap1.isSubtahap_4(),
                tahap1.isSubtahap_5(),
                tahap1.isSubtahap_6()
        };

        assertThat(subtahapStatus).hasSize(6);
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithNullSubtahapDates() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("Null Dates Test Kegiatan");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap1 tahap1WithNullDates = Tahap1.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                // All dates remain null
                .build();

        Tahap1 savedTahap1 = tahap1Repository.save(tahap1WithNullDates);

        assertThat(savedTahap1.getId()).isNotNull();
        assertThat(savedTahap1.isSubtahap_1()).isTrue();
        assertThat(savedTahap1.getSubtahap_1_tanggal_perencanaan()).isNull();
        assertThat(savedTahap1.getSubtahap_1_tanggal_realisasi()).isNull();
    }

    @Test
    void testUpdate_ToggleSubtahapStatus() {
        Optional<Tahap1> existing = tahap1Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap1 tahap1 = existing.get();
        boolean originalStatus = tahap1.isSubtahap_4();

        // Toggle the status
        tahap1.setSubtahap_4(!originalStatus);

        Tahap1 updatedTahap1 = tahap1Repository.save(tahap1);

        assertThat(updatedTahap1.isSubtahap_4()).isEqualTo(!originalStatus);
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap1> result = tahap1Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }
}