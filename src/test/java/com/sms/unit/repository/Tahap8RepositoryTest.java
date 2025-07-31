package com.sms.unit.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.sms.entity.Tahap8;
import com.sms.repository.Tahap8Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap8RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap8Repository tahap8Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap8 tahap8_kegiatan1;
    private Tahap8 tahap8_kegiatan2;
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

        // Setup Tahap8 for kegiatan1 with partial completion and file upload
        tahap8_kegiatan1 = Tahap8.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(3))
                .uploadFileName("final_report.pdf")
                .uploadFilePath("./uploads/kegiatan/1/tahap/8/final_report.pdf")
                .uploadTimestamp(LocalDateTime.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap8_kegiatan1);

        // Setup Tahap8 for kegiatan2 with full completion but no file upload
        tahap8_kegiatan2 = Tahap8.builder()
                .kegiatan(kegiatan2)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(5))
                .subtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(4))
                .subtahap_2_tanggal_realisasi(LocalDate.now().minusDays(3))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(2))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(tahap8_kegiatan2);
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
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
        assertThat(result.get().isSubtahap_1()).isTrue();
        assertThat(result.get().isSubtahap_2()).isFalse();
        assertThat(result.get().isSubtahap_3()).isTrue();
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap8 tahap8 = result.get();
        assertThat(tahap8.isSubtahap_1()).isTrue();
        assertThat(tahap8.isSubtahap_2()).isTrue();
        assertThat(tahap8.isSubtahap_3()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap8WithFileUpload() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap8 newTahap8 = Tahap8.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_2_tanggal_perencanaan(LocalDate.now())
                .subtahap_2_tanggal_realisasi(LocalDate.now())
                .uploadFileName("evaluation_report.docx")
                .uploadFilePath("./uploads/kegiatan/3/tahap/8/evaluation_report.docx")
                .uploadTimestamp(LocalDateTime.now())
                .build();

        Tahap8 savedTahap8 = tahap8Repository.save(newTahap8);

        assertThat(savedTahap8.getId()).isNotNull();
        assertThat(savedTahap8.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap8.getCompletionPercentage()).isEqualTo(33); // 1 out of 3 = 33%
        assertThat(savedTahap8.getUploadFileName()).isEqualTo("evaluation_report.docx");
        assertThat(savedTahap8.getUploadFilePath()).isEqualTo("./uploads/kegiatan/3/tahap/8/evaluation_report.docx");
        assertThat(savedTahap8.getUploadTimestamp()).isNotNull();
    }

    @Test
    void testUpdate_ExistingTahap8() {
        Optional<Tahap8> existing = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap8 tahap8 = existing.get();
        tahap8.setSubtahap_2(true);
        tahap8.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap8.setSubtahap_2_tanggal_realisasi(LocalDate.now());
        tahap8.setUploadFileName("updated_final_report.pdf");
        tahap8.setUploadTimestamp(LocalDateTime.now());

        Tahap8 updatedTahap8 = tahap8Repository.save(tahap8);

        assertThat(updatedTahap8.isSubtahap_2()).isTrue();
        assertThat(updatedTahap8.getCompletionPercentage()).isEqualTo(100); // 3 out of 3 = 100%
        assertThat(updatedTahap8.getUploadFileName()).isEqualTo("updated_final_report.pdf");
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap8> result = tahap8Repository.findById(tahap8_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap8> result = tahap8Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap8> allTahap8 = tahap8Repository.findAll();

        assertThat(allTahap8).hasSize(2);
        assertThat(allTahap8).extracting(tahap8 -> tahap8.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap8_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap8> beforeDeletion = tahap8Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap8Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap8> afterDeletion = tahap8Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap8> remaining = tahap8Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();
        int completionPercentage = tahap8.getCompletionPercentage();

        // 2 out of 3 subtahap completed = 66%
        assertThat(completionPercentage).isEqualTo(66);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();
        int completionPercentage = tahap8.getCompletionPercentage();

        // 3 out of 3 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testTahap8Structure_HasThreeSubtahap() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Verify Tahap8 has exactly 3 subtahap (final stage)
        boolean[] subtahapStatus = {
                tahap8.isSubtahap_1(),
                tahap8.isSubtahap_2(),
                tahap8.isSubtahap_3()
        };

        assertThat(subtahapStatus).hasSize(3);
    }

    @Test
    void testFileUploadCapability() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Verify file upload fields are present and functional
        assertThat(tahap8.getUploadFileName()).isEqualTo("final_report.pdf");
        assertThat(tahap8.getUploadFilePath()).isEqualTo("./uploads/kegiatan/1/tahap/8/final_report.pdf");
        assertThat(tahap8.getUploadTimestamp()).isNotNull();
        assertThat(tahap8.getUploadTimestamp()).isBefore(LocalDateTime.now());
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(null);

        assertThat(result).isEmpty();
    }

    @Test
    void testSave_WithoutFileUpload() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("No Upload Test");
        newKegiatan.setCode("KEG004");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap8 tahap8WithoutUpload = Tahap8.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_3_tanggal_perencanaan(LocalDate.now())
                .subtahap_3_tanggal_realisasi(LocalDate.now())
                // No file upload fields set
                .build();

        Tahap8 savedTahap8 = tahap8Repository.save(tahap8WithoutUpload);

        assertThat(savedTahap8.getId()).isNotNull();
        assertThat(savedTahap8.getCompletionPercentage()).isEqualTo(33); // 1 out of 3 = 33%
        assertThat(savedTahap8.getUploadFileName()).isNull();
        assertThat(savedTahap8.getUploadFilePath()).isNull();
        assertThat(savedTahap8.getUploadTimestamp()).isNull();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }

    @Test
    void testTahap8_FinalStageCharacteristics() {
        // Test characteristics specific to Tahap8 as final stage
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Verify that Tahap8 has the fewest subtahap (3) as it's the final stage
        assertThat(tahap8.getCompletionPercentage()).isEqualTo(100);

        // Each subtahap completion has significant impact (33% each)
        assertThat(tahap8.isSubtahap_1()).isTrue();
        assertThat(tahap8.isSubtahap_2()).isTrue();
        assertThat(tahap8.isSubtahap_3()).isTrue();

        // Verify all dates are present for completed subtahap
        assertThat(tahap8.getSubtahap_1_tanggal_perencanaan()).isNotNull();
        assertThat(tahap8.getSubtahap_1_tanggal_realisasi()).isNotNull();
        assertThat(tahap8.getSubtahap_2_tanggal_perencanaan()).isNotNull();
        assertThat(tahap8.getSubtahap_2_tanggal_realisasi()).isNotNull();
        assertThat(tahap8.getSubtahap_3_tanggal_perencanaan()).isNotNull();
        assertThat(tahap8.getSubtahap_3_tanggal_realisasi()).isNotNull();
    }

    @Test
    void testTahap8_FileUploadIntegration() {
        // Test that Tahap8 supports file upload (along with Tahap7)
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Update file upload information
        tahap8.setUploadFileName("completion_certificate.pdf");
        tahap8.setUploadFilePath("./uploads/kegiatan/1/tahap/8/completion_certificate.pdf");
        tahap8.setUploadTimestamp(LocalDateTime.now());

        Tahap8 updatedTahap8 = tahap8Repository.save(tahap8);

        assertThat(updatedTahap8.getUploadFileName()).isEqualTo("completion_certificate.pdf");
        assertThat(updatedTahap8.getUploadFilePath()).contains("completion_certificate.pdf");
        assertThat(updatedTahap8.getUploadTimestamp()).isNotNull();
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Check that completed subtahap have both planning and realization dates
        if (tahap8.isSubtahap_1()) {
            assertThat(tahap8.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap8.getSubtahap_1_tanggal_realisasi()).isNotNull();
            assertThat(tahap8.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap8.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap8.isSubtahap_3()) {
            assertThat(tahap8.getSubtahap_3_tanggal_perencanaan()).isNotNull();
            assertThat(tahap8.getSubtahap_3_tanggal_realisasi()).isNotNull();
            assertThat(tahap8.getSubtahap_3_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap8.getSubtahap_3_tanggal_perencanaan());
        }
    }

    @Test
    void testTahap8_ProjectCompletion() {
        // Test scenario where completing Tahap8 means project completion
        Optional<Tahap8> result = tahap8Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap8 tahap8 = result.get();

        // Verify that when Tahap8 is 100% complete, it represents project completion
        assertThat(tahap8.getCompletionPercentage()).isEqualTo(100);

        // All subtahap should be completed for project closure
        assertThat(tahap8.isSubtahap_1()).isTrue(); // Project evaluation
        assertThat(tahap8.isSubtahap_2()).isTrue(); // Final reporting
        assertThat(tahap8.isSubtahap_3()).isTrue(); // Project closure
    }

    @Test
    void testSave_WithAllSubtahapIncomplete() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("Incomplete Project");
        newKegiatan.setCode("KEG005");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap8 incompleteTahap8 = Tahap8.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(false)
                .subtahap_2(false)
                .subtahap_3(false)
                .build();

        Tahap8 savedTahap8 = tahap8Repository.save(incompleteTahap8);

        assertThat(savedTahap8.getId()).isNotNull();
        assertThat(savedTahap8.getCompletionPercentage()).isEqualTo(0);
        assertThat(savedTahap8.isSubtahap_1()).isFalse();
        assertThat(savedTahap8.isSubtahap_2()).isFalse();
        assertThat(savedTahap8.isSubtahap_3()).isFalse();
    }

    @Test
    void testUpdate_ProgressiveCompletion() {
        Optional<Tahap8> existing = tahap8Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap8 tahap8 = existing.get();

        // Initially: subtahap_1=true, subtahap_2=false, subtahap_3=true (66%)
        assertThat(tahap8.getCompletionPercentage()).isEqualTo(66);

        // Complete subtahap_2 to achieve 100%
        tahap8.setSubtahap_2(true);
        tahap8.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap8.setSubtahap_2_tanggal_realisasi(LocalDate.now());

        Tahap8 updatedTahap8 = tahap8Repository.save(tahap8);

        assertThat(updatedTahap8.getCompletionPercentage()).isEqualTo(100);
        assertThat(updatedTahap8.isSubtahap_1()).isTrue();
        assertThat(updatedTahap8.isSubtahap_2()).isTrue();
        assertThat(updatedTahap8.isSubtahap_3()).isTrue();
    }
}