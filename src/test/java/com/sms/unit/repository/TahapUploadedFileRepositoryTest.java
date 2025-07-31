package com.sms.unit.repository;

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

import com.sms.entity.TahapUploadedFile;
import com.sms.repository.TahapUploadedFileRepository;

@DataJpaTest
@ActiveProfiles("test")
public class TahapUploadedFileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TahapUploadedFileRepository tahapUploadedFileRepository;

    private TahapUploadedFile testFile1;
    private TahapUploadedFile testFile2;
    private TahapUploadedFile testFile3;

    @BeforeEach
    void setUp() {
        // Setup test data for kegiatan 1, tahap 7
        testFile1 = TahapUploadedFile.builder()
                .kegiatanId(1L)
                .tahapId(7)
                .originalFilename("document1.pdf")
                .storedFilename("1234567890_document1.pdf")
                .filePath("./test-uploads/kegiatan/1/tahap/7/1234567890_document1.pdf")
                .fileSize(1024L)
                .uploadTimestamp(LocalDateTime.now())
                .uploadedByUserId(100L)
                .build();

        // Setup test data for kegiatan 1, tahap 8
        testFile2 = TahapUploadedFile.builder()
                .kegiatanId(1L)
                .tahapId(8)
                .originalFilename("document2.docx")
                .storedFilename("1234567891_document2.docx")
                .filePath("./test-uploads/kegiatan/1/tahap/8/1234567891_document2.docx")
                .fileSize(2048L)
                .uploadTimestamp(LocalDateTime.now())
                .uploadedByUserId(100L)
                .build();

        // Setup test data for kegiatan 2, tahap 7
        testFile3 = TahapUploadedFile.builder()
                .kegiatanId(2L)
                .tahapId(7)
                .originalFilename("document3.xlsx")
                .storedFilename("1234567892_document3.xlsx")
                .filePath("./test-uploads/kegiatan/2/tahap/7/1234567892_document3.xlsx")
                .fileSize(4096L)
                .uploadTimestamp(LocalDateTime.now())
                .uploadedByUserId(101L)
                .build();

        entityManager.persistAndFlush(testFile1);
        entityManager.persistAndFlush(testFile2);
        entityManager.persistAndFlush(testFile3);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    // ===============================================
    // Test Cases for findByKegiatanId()
    // ===============================================

    @Test
    void testFindByKegiatanId_Success() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TahapUploadedFile::getKegiatanId)
                .containsOnly(1L);
        assertThat(result).extracting(TahapUploadedFile::getTahapId)
                .containsExactlyInAnyOrder(7, 8);
    }

    @Test
    void testFindByKegiatanId_NotFound() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanId_SingleResult() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanId(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKegiatanId()).isEqualTo(2L);
        assertThat(result.get(0).getTahapId()).isEqualTo(7);
        assertThat(result.get(0).getOriginalFilename()).isEqualTo("document3.xlsx");
    }

    // ===============================================
    // Test Cases for findByKegiatanIdAndTahapId()
    // ===============================================

    @Test
    void testFindByKegiatanIdAndTahapId_Success() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKegiatanId()).isEqualTo(1L);
        assertThat(result.get(0).getTahapId()).isEqualTo(7);
        assertThat(result.get(0).getOriginalFilename()).isEqualTo("document1.pdf");
        assertThat(result.get(0).getStoredFilename()).isEqualTo("1234567890_document1.pdf");
    }

    @Test
    void testFindByKegiatanIdAndTahapId_NotFound() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 5);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindByKegiatanIdAndTahapId_DifferentKegiatan() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(2L, 7);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKegiatanId()).isEqualTo(2L);
        assertThat(result.get(0).getTahapId()).isEqualTo(7);
        assertThat(result.get(0).getOriginalFilename()).isEqualTo("document3.xlsx");
    }

    @Test
    void testFindByKegiatanIdAndTahapId_ValidTahapIds() {
        // Test dengan tahap 7 dan 8 yang merupakan tahap valid untuk upload file
        List<TahapUploadedFile> resultTahap7 = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7);
        List<TahapUploadedFile> resultTahap8 = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 8);

        assertThat(resultTahap7).hasSize(1);
        assertThat(resultTahap8).hasSize(1);

        assertThat(resultTahap7.get(0).getTahapId()).isEqualTo(7);
        assertThat(resultTahap8.get(0).getTahapId()).isEqualTo(8);
    }

    @Test
    void testFindByKegiatanIdAndTahapId_WithNullKegiatanId() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(null, 7);

        assertThat(result).isEmpty();
    }

    // ===============================================
    // Test Cases for findByKegiatanIdAndTahapIdAndStoredFilename()
    // ===============================================

    @Test
    void testFindByKegiatanIdAndTahapIdAndStoredFilename_Success() {
        TahapUploadedFile result = tahapUploadedFileRepository
                .findByKegiatanIdAndTahapIdAndStoredFilename(1L, 7, "1234567890_document1.pdf");

        assertThat(result).isNotNull();
        assertThat(result.getKegiatanId()).isEqualTo(1L);
        assertThat(result.getTahapId()).isEqualTo(7);
        assertThat(result.getStoredFilename()).isEqualTo("1234567890_document1.pdf");
        assertThat(result.getOriginalFilename()).isEqualTo("document1.pdf");
    }

    @Test
    void testFindByKegiatanIdAndTahapIdAndStoredFilename_NotFound() {
        TahapUploadedFile result = tahapUploadedFileRepository
                .findByKegiatanIdAndTahapIdAndStoredFilename(1L, 7, "nonexistent_file.pdf");

        assertThat(result).isNull();
    }

    @Test
    void testFindByKegiatanIdAndTahapIdAndStoredFilename_WrongKegiatan() {
        TahapUploadedFile result = tahapUploadedFileRepository
                .findByKegiatanIdAndTahapIdAndStoredFilename(999L, 7, "1234567890_document1.pdf");

        assertThat(result).isNull();
    }

    @Test
    void testFindByKegiatanIdAndTahapIdAndStoredFilename_WrongTahap() {
        TahapUploadedFile result = tahapUploadedFileRepository
                .findByKegiatanIdAndTahapIdAndStoredFilename(1L, 5, "1234567890_document1.pdf");

        assertThat(result).isNull();
    }

    // ===============================================
    // Test Cases for deleteByKegiatanId()
    // ===============================================

    @Test
    void testDeleteByKegiatanId_Success() {
        // Verify data exists before deletion
        List<TahapUploadedFile> beforeDeletion = tahapUploadedFileRepository.findByKegiatanId(1L);
        assertThat(beforeDeletion).hasSize(2);

        // Delete all files for kegiatan 1
        tahapUploadedFileRepository.deleteByKegiatanId(1L);
        entityManager.flush();

        // Verify deletion
        List<TahapUploadedFile> afterDeletion = tahapUploadedFileRepository.findByKegiatanId(1L);
        assertThat(afterDeletion).isEmpty();

        // Verify other kegiatan data is still intact
        List<TahapUploadedFile> otherKegiatanData = tahapUploadedFileRepository.findByKegiatanId(2L);
        assertThat(otherKegiatanData).hasSize(1);
    }

    @Test
    void testDeleteByKegiatanId_NotFound() {
        // Try to delete files for non-existent kegiatan
        tahapUploadedFileRepository.deleteByKegiatanId(999L);
        entityManager.flush();

        // Verify original data is still intact
        List<TahapUploadedFile> allFiles = tahapUploadedFileRepository.findAll();
        assertThat(allFiles).hasSize(3);
    }

    // ===============================================
    // Test Cases for deleteByKegiatanIdAndTahapId()
    // ===============================================

    @Test
    void testDeleteByKegiatanIdAndTahapId_Success() {
        // Verify data exists before deletion
        List<TahapUploadedFile> beforeDeletion = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7);
        assertThat(beforeDeletion).hasSize(1);

        // Delete files for kegiatan 1, tahap 7
        tahapUploadedFileRepository.deleteByKegiatanIdAndTahapId(1L, 7);
        entityManager.flush();

        // Verify deletion
        List<TahapUploadedFile> afterDeletion = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7);
        assertThat(afterDeletion).isEmpty();

        // Verify other tahap data for same kegiatan is still intact
        List<TahapUploadedFile> otherTahapData = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 8);
        assertThat(otherTahapData).hasSize(1);

        // Verify other kegiatan data is still intact
        List<TahapUploadedFile> otherKegiatanData = tahapUploadedFileRepository.findByKegiatanId(2L);
        assertThat(otherKegiatanData).hasSize(1);
    }

    @Test
    void testDeleteByKegiatanIdAndTahapId_NotFound() {
        // Try to delete files for non-existent combination
        tahapUploadedFileRepository.deleteByKegiatanIdAndTahapId(1L, 5);
        entityManager.flush();

        // Verify original data is still intact
        List<TahapUploadedFile> allFiles = tahapUploadedFileRepository.findAll();
        assertThat(allFiles).hasSize(3);
    }

    // ===============================================
    // Test Cases for JPA Standard Methods
    // ===============================================

    @Test
    void testSave_Success() {
        TahapUploadedFile newFile = TahapUploadedFile.builder()
                .kegiatanId(3L)
                .tahapId(7)
                .originalFilename("new_document.pdf")
                .storedFilename("1234567893_new_document.pdf")
                .filePath("./test-uploads/kegiatan/3/tahap/7/1234567893_new_document.pdf")
                .fileSize(512L)
                .uploadTimestamp(LocalDateTime.now())
                .uploadedByUserId(102L)
                .build();

        TahapUploadedFile savedFile = tahapUploadedFileRepository.save(newFile);

        assertThat(savedFile.getId()).isNotNull();
        assertThat(savedFile.getKegiatanId()).isEqualTo(3L);
        assertThat(savedFile.getTahapId()).isEqualTo(7);
        assertThat(savedFile.getOriginalFilename()).isEqualTo("new_document.pdf");
        assertThat(savedFile.getStoredFilename()).isEqualTo("1234567893_new_document.pdf");
    }

    @Test
    void testFindById_Success() {
        Optional<TahapUploadedFile> result = tahapUploadedFileRepository.findById(testFile1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getKegiatanId()).isEqualTo(1L);
        assertThat(result.get().getTahapId()).isEqualTo(7);
        assertThat(result.get().getOriginalFilename()).isEqualTo("document1.pdf");
    }

    @Test
    void testFindById_NotFound() {
        Optional<TahapUploadedFile> result = tahapUploadedFileRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll_Success() {
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findAll();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(TahapUploadedFile::getKegiatanId)
                .containsExactlyInAnyOrder(1L, 1L, 2L);
        assertThat(result).extracting(TahapUploadedFile::getTahapId)
                .containsExactlyInAnyOrder(7, 8, 7);
    }

    @Test
    void testDeleteById_Success() {
        Long fileId = testFile1.getId();

        // Verify file exists
        Optional<TahapUploadedFile> beforeDeletion = tahapUploadedFileRepository.findById(fileId);
        assertThat(beforeDeletion).isPresent();

        // Delete the file
        tahapUploadedFileRepository.deleteById(fileId);
        entityManager.flush();

        // Verify deletion
        Optional<TahapUploadedFile> afterDeletion = tahapUploadedFileRepository.findById(fileId);
        assertThat(afterDeletion).isEmpty();

        // Verify total count decreased
        List<TahapUploadedFile> remainingFiles = tahapUploadedFileRepository.findAll();
        assertThat(remainingFiles).hasSize(2);
    }

    // ===============================================
    // Test Cases for Business Logic and Edge Cases
    // ===============================================

    @Test
    void testBusinessRule_OnlyTahap7And8CanUploadFiles() {
        // Verify that test data only contains tahap 7 and 8
        List<TahapUploadedFile> allFiles = tahapUploadedFileRepository.findAll();

        assertThat(allFiles).extracting(TahapUploadedFile::getTahapId)
                .containsOnly(7, 8);

        // Test that searches for other tahap return empty
        for (int tahapId = 1; tahapId <= 6; tahapId++) {
            List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, tahapId);
            assertThat(result).isEmpty();
        }
    }

    @Test
    void testDataConsistency_FilePathMatching() {
        List<TahapUploadedFile> allFiles = tahapUploadedFileRepository.findAll();

        for (TahapUploadedFile file : allFiles) {
            String expectedPath = "./test-uploads/kegiatan/" + file.getKegiatanId() + "/tahap/" + file.getTahapId()
                    + "/" + file.getStoredFilename();
            assertThat(file.getFilePath()).isEqualTo(expectedPath);
        }
    }

    @Test
    void testDataConsistency_StoredFilenameFormat() {
        List<TahapUploadedFile> allFiles = tahapUploadedFileRepository.findAll();

        for (TahapUploadedFile file : allFiles) {
            // Stored filename should contain timestamp prefix and original filename
            assertThat(file.getStoredFilename()).contains("_");
            assertThat(file.getStoredFilename())
                    .endsWith(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        }
    }

    @Test
    void testQueryPerformance_MultipleFiles() {
        // Add more test data for performance testing
        for (int i = 4; i <= 10; i++) {
            TahapUploadedFile file = TahapUploadedFile.builder()
                    .kegiatanId(1L)
                    .tahapId(7)
                    .originalFilename("document" + i + ".pdf")
                    .storedFilename("123456789" + i + "_document" + i + ".pdf")
                    .filePath("./test-uploads/kegiatan/1/tahap/7/123456789" + i + "_document" + i + ".pdf")
                    .fileSize(1024L * i)
                    .uploadTimestamp(LocalDateTime.now())
                    .uploadedByUserId(100L)
                    .build();
            entityManager.persistAndFlush(file);
        }

        // Query should handle multiple files efficiently
        List<TahapUploadedFile> result = tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7);
        assertThat(result).hasSizeGreaterThan(1);

        // All results should match the criteria
        assertThat(result).allMatch(file -> file.getKegiatanId().equals(1L) && file.getTahapId() == 7);
    }

    @Test
    void testCascadeDelete_Integration() {
        // Verify initial state
        List<TahapUploadedFile> initialFiles = tahapUploadedFileRepository.findAll();
        assertThat(initialFiles).hasSize(3);

        // Delete all files for kegiatan 1
        tahapUploadedFileRepository.deleteByKegiatanId(1L);
        entityManager.flush();

        // Verify only kegiatan 2 files remain
        List<TahapUploadedFile> remainingFiles = tahapUploadedFileRepository.findAll();
        assertThat(remainingFiles).hasSize(1);
        assertThat(remainingFiles.get(0).getKegiatanId()).isEqualTo(2L);
    }
}