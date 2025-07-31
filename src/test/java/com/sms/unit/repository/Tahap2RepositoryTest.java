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
import com.sms.entity.Tahap2;
import com.sms.repository.Tahap2Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap2RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap2Repository tahap2Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap2 tahap2_kegiatan1;
    private Tahap2 tahap2_kegiatan2;
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

        // Setup Tahap2 for kegiatan1 with partial completion (2 out of 6 subtahap)
        tahap2_kegiatan1 = Tahap2.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(5))
                .build();
        entityManager.persistAndFlush(tahap2_kegiatan1);

        // Setup Tahap2 for kegiatan2 with no completion
        tahap2_kegiatan2 = Tahap2.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                .build();
        entityManager.persistAndFlush(tahap2_kegiatan2);
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
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isTrue();
        assertThat(result.get().isSubtahap_3()).isFalse();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithNoSubtahapCompleted() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap2 tahap2 = result.get();
        assertThat(tahap2.isSubtahap_1()).isFalse();
        assertThat(tahap2.isSubtahap_2()).isFalse();
        assertThat(tahap2.isSubtahap_3()).isFalse();
        assertThat(tahap2.isSubtahap_4()).isFalse();
        assertThat(tahap2.isSubtahap_5()).isFalse();
        assertThat(tahap2.isSubtahap_6()).isFalse();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap2() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap2 newTahap2 = Tahap2.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_6(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now())
                .subtahap_1_tanggal_realisasi(LocalDate.now())
                .build();

        Tahap2 savedTahap2 = tahap2Repository.save(newTahap2);

        assertThat(savedTahap2.getId()).isNotNull();
        assertThat(savedTahap2.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap2.isSubtahap_1()).isTrue();
        assertThat(savedTahap2.isSubtahap_3()).isTrue();
        assertThat(savedTahap2.isSubtahap_5()).isTrue();
    }

    @Test
    void testUpdate_ExistingTahap2() {
        Optional<Tahap2> existing = tahap2Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(existing).isPresent();

        Tahap2 tahap2 = existing.get();
        tahap2.setSubtahap_1(true);
        tahap2.setSubtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap2.setSubtahap_1_tanggal_realisasi(LocalDate.now());

        Tahap2 updatedTahap2 = tahap2Repository.save(tahap2);

        assertThat(updatedTahap2.isSubtahap_1()).isTrue();
        assertThat(updatedTahap2.getSubtahap_1_tanggal_perencanaan()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(updatedTahap2.getSubtahap_1_tanggal_realisasi()).isEqualTo(LocalDate.now());
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap2> result = tahap2Repository.findById(tahap2_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap2> result = tahap2Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap2> allTahap2 = tahap2Repository.findAll();

        assertThat(allTahap2).hasSize(2);
        assertThat(allTahap2).extracting(tahap2 -> tahap2.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap2_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap2> beforeDeletion = tahap2Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap2Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap2> afterDeletion = tahap2Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap2> remaining = tahap2Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap2 tahap2 = result.get();
        int completionPercentage = tahap2.getCompletionPercentage();

        // 2 out of 6 subtahap completed = 33%
        assertThat(completionPercentage).isEqualTo(33);
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap2 tahap2 = result.get();

        // Completed subtahap should have both planning and realization dates
        if (tahap2.isSubtahap_1()) {
            assertThat(tahap2.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap2.getSubtahap_1_tanggal_realisasi()).isNotNull();
            assertThat(tahap2.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap2.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap2.isSubtahap_2()) {
            assertThat(tahap2.getSubtahap_2_tanggal_perencanaan()).isNotNull();
            assertThat(tahap2.getSubtahap_2_tanggal_realisasi()).isNotNull();
            assertThat(tahap2.getSubtahap_2_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap2.getSubtahap_2_tanggal_perencanaan());
        }
    }

    @Test
    void testTahap2Structure_HasSixSubtahap() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap2 tahap2 = result.get();

        // Verify Tahap2 has exactly 6 subtahap
        boolean[] subtahapStatus = {
                tahap2.isSubtahap_1(),
                tahap2.isSubtahap_2(),
                tahap2.isSubtahap_3(),
                tahap2.isSubtahap_4(),
                tahap2.isSubtahap_5(),
                tahap2.isSubtahap_6()
        };

        assertThat(subtahapStatus).hasSize(6);
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(null);

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

        Tahap2 tahap2WithNullDates = Tahap2.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_6(false)
                // All dates remain null
                .build();

        Tahap2 savedTahap2 = tahap2Repository.save(tahap2WithNullDates);

        assertThat(savedTahap2.getId()).isNotNull();
        assertThat(savedTahap2.isSubtahap_1()).isTrue();
        assertThat(savedTahap2.getSubtahap_1_tanggal_perencanaan()).isNull();
        assertThat(savedTahap2.getSubtahap_1_tanggal_realisasi()).isNull();
    }

    @Test
    void testUpdate_CompleteAllSubtahap() {
        Optional<Tahap2> existing = tahap2Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(existing).isPresent();

        Tahap2 tahap2 = existing.get();

        // Complete all subtahap
        tahap2.setSubtahap_1(true);
        tahap2.setSubtahap_2(true);
        tahap2.setSubtahap_3(true);
        tahap2.setSubtahap_4(true);
        tahap2.setSubtahap_5(true);
        tahap2.setSubtahap_6(true);

        LocalDate planningDate = LocalDate.now().minusDays(1);
        LocalDate realizationDate = LocalDate.now();

        tahap2.setSubtahap_1_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_1_tanggal_realisasi(realizationDate);
        tahap2.setSubtahap_2_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_2_tanggal_realisasi(realizationDate);
        tahap2.setSubtahap_3_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_3_tanggal_realisasi(realizationDate);
        tahap2.setSubtahap_4_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_4_tanggal_realisasi(realizationDate);
        tahap2.setSubtahap_5_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_5_tanggal_realisasi(realizationDate);
        tahap2.setSubtahap_6_tanggal_perencanaan(planningDate);
        tahap2.setSubtahap_6_tanggal_realisasi(realizationDate);

        Tahap2 updatedTahap2 = tahap2Repository.save(tahap2);

        assertThat(updatedTahap2.getCompletionPercentage()).isEqualTo(100);
        assertThat(updatedTahap2.isSubtahap_1()).isTrue();
        assertThat(updatedTahap2.isSubtahap_2()).isTrue();
        assertThat(updatedTahap2.isSubtahap_3()).isTrue();
        assertThat(updatedTahap2.isSubtahap_4()).isTrue();
        assertThat(updatedTahap2.isSubtahap_5()).isTrue();
        assertThat(updatedTahap2.isSubtahap_6()).isTrue();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap2> result = tahap2Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }
}