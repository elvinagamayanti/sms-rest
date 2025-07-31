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
import com.sms.entity.Tahap7;
import com.sms.repository.Tahap7Repository;

@DataJpaTest
@ActiveProfiles("test")
public class Tahap7RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Tahap7Repository tahap7Repository;

    private Kegiatan kegiatan1;
    private Kegiatan kegiatan2;
    private Tahap7 tahap7_kegiatan1;
    private Tahap7 tahap7_kegiatan2;
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

        // Setup Tahap7 for kegiatan1 with partial completion and file upload capability
        tahap7_kegiatan1 = Tahap7.builder()
                .kegiatan(kegiatan1)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(true)
                .subtahap_4(false)
                .subtahap_5(true)
                .subtahap_1_tanggal_perencanaan(LocalDate.now().minusDays(10))
                .subtahap_1_tanggal_realisasi(LocalDate.now().minusDays(9))
                .subtahap_3_tanggal_perencanaan(LocalDate.now().minusDays(8))
                .subtahap_3_tanggal_realisasi(LocalDate.now().minusDays(7))
                .subtahap_5_tanggal_perencanaan(LocalDate.now().minusDays(6))
                .subtahap_5_tanggal_realisasi(LocalDate.now().minusDays(5))
                .uploadFileName("tahap7_document.pdf")
                .uploadFilePath("./uploads/kegiatan/1/tahap/7/tahap7_document.pdf")
                .uploadTimestamp(LocalDateTime.now().minusDays(2))
                .build();
        entityManager.persistAndFlush(tahap7_kegiatan1);

        // Setup Tahap7 for kegiatan2 with full completion but no file upload
        tahap7_kegiatan2 = Tahap7.builder()
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
        entityManager.persistAndFlush(tahap7_kegiatan2);
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
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());

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
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_WithAllSubtahapCompleted() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan2.getId());

        assertThat(result).isPresent();
        Tahap7 tahap7 = result.get();
        assertThat(tahap7.isSubtahap_1()).isTrue();
        assertThat(tahap7.isSubtahap_2()).isTrue();
        assertThat(tahap7.isSubtahap_3()).isTrue();
        assertThat(tahap7.isSubtahap_4()).isTrue();
        assertThat(tahap7.isSubtahap_5()).isTrue();
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_NewTahap7WithFileUpload() {
        Kegiatan newKegiatan = new Kegiatan();
        newKegiatan.setName("New Test Kegiatan");
        newKegiatan.setCode("KEG003");
        newKegiatan.setStartDate(Date.valueOf(LocalDate.now()));
        newKegiatan.setEndDate(Date.valueOf(LocalDate.now().plusMonths(6)));
        newKegiatan.setProgram(program);
        newKegiatan.setOutput(output);
        entityManager.persistAndFlush(newKegiatan);

        Tahap7 newTahap7 = Tahap7.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(true)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now())
                .subtahap_1_tanggal_realisasi(LocalDate.now())
                .subtahap_2_tanggal_perencanaan(LocalDate.now())
                .subtahap_2_tanggal_realisasi(LocalDate.now())
                .uploadFileName("new_document.docx")
                .uploadFilePath("./uploads/kegiatan/3/tahap/7/new_document.docx")
                .uploadTimestamp(LocalDateTime.now())
                .build();

        Tahap7 savedTahap7 = tahap7Repository.save(newTahap7);

        assertThat(savedTahap7.getId()).isNotNull();
        assertThat(savedTahap7.getKegiatan().getId()).isEqualTo(newKegiatan.getId());
        assertThat(savedTahap7.getCompletionPercentage()).isEqualTo(40); // 2 out of 5 = 40%
        assertThat(savedTahap7.getUploadFileName()).isEqualTo("new_document.docx");
        assertThat(savedTahap7.getUploadFilePath()).isEqualTo("./uploads/kegiatan/3/tahap/7/new_document.docx");
        assertThat(savedTahap7.getUploadTimestamp()).isNotNull();
    }

    @Test
    void testUpdate_ExistingTahap7() {
        Optional<Tahap7> existing = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(existing).isPresent();

        Tahap7 tahap7 = existing.get();
        tahap7.setSubtahap_2(true);
        tahap7.setSubtahap_2_tanggal_perencanaan(LocalDate.now().minusDays(1));
        tahap7.setSubtahap_2_tanggal_realisasi(LocalDate.now());
        tahap7.setUploadFileName("updated_document.pdf");
        tahap7.setUploadTimestamp(LocalDateTime.now());

        Tahap7 updatedTahap7 = tahap7Repository.save(tahap7);

        assertThat(updatedTahap7.isSubtahap_2()).isTrue();
        assertThat(updatedTahap7.getCompletionPercentage()).isEqualTo(80); // 4 out of 5 = 80%
        assertThat(updatedTahap7.getUploadFileName()).isEqualTo("updated_document.pdf");
    }

    @Test
    void testFindById_Found() {
        Optional<Tahap7> result = tahap7Repository.findById(tahap7_kegiatan1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatan1.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Tahap7> result = tahap7Repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        List<Tahap7> allTahap7 = tahap7Repository.findAll();

        assertThat(allTahap7).hasSize(2);
        assertThat(allTahap7).extracting(tahap7 -> tahap7.getKegiatan().getId())
                .containsExactlyInAnyOrder(kegiatan1.getId(), kegiatan2.getId());
    }

    @Test
    void testDeleteById() {
        Long tahapId = tahap7_kegiatan1.getId();

        // Verify exists before deletion
        Optional<Tahap7> beforeDeletion = tahap7Repository.findById(tahapId);
        assertThat(beforeDeletion).isPresent();

        // Delete
        tahap7Repository.deleteById(tahapId);
        entityManager.flush();

        // Verify deleted
        Optional<Tahap7> afterDeletion = tahap7Repository.findById(tahapId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<Tahap7> remaining = tahap7Repository.findAll();
        assertThat(remaining).hasSize(1);
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testCompletionPercentage_PartiallyCompleted() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();
        int completionPercentage = tahap7.getCompletionPercentage();

        // 3 out of 5 subtahap completed = 60%
        assertThat(completionPercentage).isEqualTo(60);
    }

    @Test
    void testCompletionPercentage_FullyCompleted() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan2.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();
        int completionPercentage = tahap7.getCompletionPercentage();

        // 5 out of 5 subtahap completed = 100%
        assertThat(completionPercentage).isEqualTo(100);
    }

    @Test
    void testTahap7Structure_HasFiveSubtahap() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();

        // Verify Tahap7 has exactly 5 subtahap
        boolean[] subtahapStatus = {
                tahap7.isSubtahap_1(),
                tahap7.isSubtahap_2(),
                tahap7.isSubtahap_3(),
                tahap7.isSubtahap_4(),
                tahap7.isSubtahap_5()
        };

        assertThat(subtahapStatus).hasSize(5);
    }

    @Test
    void testFileUploadCapability() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();

        // Verify file upload fields are present and functional
        assertThat(tahap7.getUploadFileName()).isEqualTo("tahap7_document.pdf");
        assertThat(tahap7.getUploadFilePath()).isEqualTo("./uploads/kegiatan/1/tahap/7/tahap7_document.pdf");
        assertThat(tahap7.getUploadTimestamp()).isNotNull();
        assertThat(tahap7.getUploadTimestamp()).isBefore(LocalDateTime.now());
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testFindByKegiatanId_WithNullKegiatanId() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(null);

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

        Tahap7 tahap7WithoutUpload = Tahap7.builder()
                .kegiatan(newKegiatan)
                .subtahap_1(true)
                .subtahap_2(false)
                .subtahap_3(false)
                .subtahap_4(false)
                .subtahap_5(false)
                .subtahap_1_tanggal_perencanaan(LocalDate.now())
                .subtahap_1_tanggal_realisasi(LocalDate.now())
                // No file upload fields set
                .build();

        Tahap7 savedTahap7 = tahap7Repository.save(tahap7WithoutUpload);

        assertThat(savedTahap7.getId()).isNotNull();
        assertThat(savedTahap7.getCompletionPercentage()).isEqualTo(20); // 1 out of 5 = 20%
        assertThat(savedTahap7.getUploadFileName()).isNull();
        assertThat(savedTahap7.getUploadFilePath()).isNull();
        assertThat(savedTahap7.getUploadTimestamp()).isNull();
    }

    @Test
    void testRepositoryPersistence_AfterEntityManagerClear() {
        Long kegiatanId = kegiatan1.getId();
        entityManager.clear();

        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatanId);

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatan().getId()).isEqualTo(kegiatanId);
    }

    @Test
    void testTahap7_FileUploadIntegration() {
        // Test that Tahap7 supports file upload (along with Tahap8)
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();

        // Update file upload information
        tahap7.setUploadFileName("integration_test.xlsx");
        tahap7.setUploadFilePath("./uploads/kegiatan/1/tahap/7/integration_test.xlsx");
        tahap7.setUploadTimestamp(LocalDateTime.now());

        Tahap7 updatedTahap7 = tahap7Repository.save(tahap7);

        assertThat(updatedTahap7.getUploadFileName()).isEqualTo("integration_test.xlsx");
        assertThat(updatedTahap7.getUploadFilePath()).contains("integration_test.xlsx");
        assertThat(updatedTahap7.getUploadTimestamp()).isNotNull();
    }

    @Test
    void testSubtahapDatesConsistency() {
        Optional<Tahap7> result = tahap7Repository.findByKegiatanId(kegiatan1.getId());
        assertThat(result).isPresent();

        Tahap7 tahap7 = result.get();

        // Check that completed subtahap have both planning and realization dates
        if (tahap7.isSubtahap_1()) {
            assertThat(tahap7.getSubtahap_1_tanggal_perencanaan()).isNotNull();
            assertThat(tahap7.getSubtahap_1_tanggal_realisasi()).isNotNull();
            assertThat(tahap7.getSubtahap_1_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap7.getSubtahap_1_tanggal_perencanaan());
        }

        if (tahap7.isSubtahap_5()) {
            assertThat(tahap7.getSubtahap_5_tanggal_perencanaan()).isNotNull();
            assertThat(tahap7.getSubtahap_5_tanggal_realisasi()).isNotNull();
            assertThat(tahap7.getSubtahap_5_tanggal_realisasi())
                    .isAfterOrEqualTo(tahap7.getSubtahap_5_tanggal_perencanaan());
        }
    }
}