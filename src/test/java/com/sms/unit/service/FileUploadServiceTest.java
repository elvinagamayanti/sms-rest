package com.sms.unit.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.sms.entity.TahapUploadedFile;
import com.sms.repository.TahapUploadedFileRepository;
import com.sms.service.FileUploadService;

public class FileUploadServiceTest {

    @Mock
    private TahapUploadedFileRepository tahapUploadedFileRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private InputStream inputStream;

    private FileUploadService fileUploadService;

    AutoCloseable autoCloseable;
    TahapUploadedFile tahapUploadedFile;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        fileUploadService = new FileUploadService(tahapUploadedFileRepository);

        // Use reflection to set the upload directory
        try {
            java.lang.reflect.Field uploadDirField = FileUploadService.class.getDeclaredField("uploadDir");
            uploadDirField.setAccessible(true);
            uploadDirField.set(fileUploadService, "./test-uploads");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set upload directory", e);
        }

        // Setup test data
        tahapUploadedFile = new TahapUploadedFile();
        tahapUploadedFile.setId(1L);
        tahapUploadedFile.setKegiatanId(1L);
        tahapUploadedFile.setTahapId(7);
        tahapUploadedFile.setOriginalFilename("test-file.pdf");
        tahapUploadedFile.setStoredFilename("1234567890_test-file.pdf");
        tahapUploadedFile.setFilePath("./test-uploads/kegiatan/1/tahap/7/1234567890_test-file.pdf");
        tahapUploadedFile.setFileSize(1024L);
        tahapUploadedFile.setUploadTimestamp(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for storeFile()
    // ===============================================

    @Test
    void testStoreFile_IOExceptionHandling() throws IOException {
        mock(MultipartFile.class);

        when(multipartFile.getOriginalFilename()).thenReturn("test-file.pdf");
        when(multipartFile.getInputStream()).thenThrow(new IOException("Failed to read file"));

        assertThrows(IOException.class, () -> fileUploadService.storeFile(multipartFile, 1L, 7));
    }

    @Test
    void testStoreFile_NullFilename() throws IOException {
        mock(MultipartFile.class);

        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // Should handle null filename gracefully
        assertThrows(Exception.class, () -> fileUploadService.storeFile(multipartFile, 1L, 7));
    }

    // ===============================================
    // Test Cases for loadFileAsResource()
    // ===============================================

    @Test
    void testLoadFileAsResource_ExceptionHandling() {
        // Test with invalid parameters that would cause path errors
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileUploadService.loadFileAsResource(null, 7, "test-file.pdf"));

        assertThat(exception.getMessage()).contains("Error loading file");
    }

    // ===============================================
    // Test Cases for getUploadedFiles()
    // ===============================================

    @Test
    void testGetUploadedFiles_Success() {
        mock(TahapUploadedFileRepository.class);

        List<TahapUploadedFile> files = new ArrayList<>(Collections.singleton(tahapUploadedFile));
        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7)).thenReturn(files);

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(tahapUploadedFile.getStoredFilename());
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(1L, 7);
    }

    @Test
    void testGetUploadedFiles_EmptyList() {
        mock(TahapUploadedFileRepository.class);

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7)).thenReturn(new ArrayList<>());

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).isEmpty();
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(1L, 7);
    }

    @Test
    void testGetUploadedFiles_MultipleFiles() {
        mock(TahapUploadedFileRepository.class);

        TahapUploadedFile file1 = new TahapUploadedFile();
        file1.setStoredFilename("1234567890_file1.pdf");

        TahapUploadedFile file2 = new TahapUploadedFile();
        file2.setStoredFilename("1234567891_file2.docx");

        TahapUploadedFile file3 = new TahapUploadedFile();
        file3.setStoredFilename("1234567892_file3.xlsx");

        List<TahapUploadedFile> files = List.of(file1, file2, file3);
        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7)).thenReturn(files);

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).hasSize(3);
        assertThat(result).contains("1234567890_file1.pdf");
        assertThat(result).contains("1234567891_file2.docx");
        assertThat(result).contains("1234567892_file3.xlsx");
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(1L, 7);
    }

    @Test
    void testGetUploadedFiles_DifferentTahap() {
        mock(TahapUploadedFileRepository.class);

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 8)).thenReturn(new ArrayList<>());

        List<String> result = fileUploadService.getUploadedFiles(1L, 8);

        assertThat(result).isEmpty();
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(1L, 8);
    }

    @Test
    void testGetUploadedFiles_DifferentKegiatan() {
        mock(TahapUploadedFileRepository.class);

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(2L, 7)).thenReturn(new ArrayList<>());

        List<String> result = fileUploadService.getUploadedFiles(2L, 7);

        assertThat(result).isEmpty();
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(2L, 7);
    }

    // ===============================================
    // Test Cases for Repository Integration
    // ===============================================

    @Test
    void testGetUploadedFiles_RepositoryInteraction() {
        mock(TahapUploadedFileRepository.class);

        // Test dengan berbagai kombinasi kegiatanId dan tahapId
        Long[] kegiatanIds = { 1L, 2L, 3L };
        int[] tahapIds = { 7, 8 };

        for (Long kegiatanId : kegiatanIds) {
            for (int tahapId : tahapIds) {
                when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(kegiatanId, tahapId))
                        .thenReturn(new ArrayList<>());

                List<String> result = fileUploadService.getUploadedFiles(kegiatanId, tahapId);

                assertThat(result).isEmpty();
                verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(kegiatanId, tahapId);
            }
        }
    }

    @Test
    void testGetUploadedFiles_WithNullKegiatanId() {
        mock(TahapUploadedFileRepository.class);

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(null, 7)).thenReturn(new ArrayList<>());

        List<String> result = fileUploadService.getUploadedFiles(null, 7);

        assertThat(result).isEmpty();
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(null, 7);
    }

    @Test
    void testGetUploadedFiles_FilenameMappingCorrectness() {
        mock(TahapUploadedFileRepository.class);

        TahapUploadedFile file1 = new TahapUploadedFile();
        file1.setOriginalFilename("original1.pdf");
        file1.setStoredFilename("stored1.pdf");

        TahapUploadedFile file2 = new TahapUploadedFile();
        file2.setOriginalFilename("original2.docx");
        file2.setStoredFilename("stored2.docx");

        List<TahapUploadedFile> files = List.of(file1, file2);
        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7)).thenReturn(files);

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        // Should return stored filenames, not original filenames
        assertThat(result).hasSize(2);
        assertThat(result).contains("stored1.pdf");
        assertThat(result).contains("stored2.docx");
        assertThat(result).doesNotContain("original1.pdf");
        assertThat(result).doesNotContain("original2.docx");
    }

    // ===============================================
    // Test Cases for Business Logic
    // ===============================================

    @Test
    void testGetUploadedFiles_BusinessRules() {
        mock(TahapUploadedFileRepository.class);

        // Test business rule: hanya tahap 7 dan 8 yang bisa upload file
        int[] validTahapIds = { 7, 8 };
        int[] invalidTahapIds = { 1, 2, 3, 4, 5, 6 };

        for (int tahapId : validTahapIds) {
            when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, tahapId))
                    .thenReturn(Collections.singletonList(tahapUploadedFile));

            List<String> result = fileUploadService.getUploadedFiles(1L, tahapId);

            assertThat(result).isNotEmpty();
        }

        for (int tahapId : invalidTahapIds) {
            when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, tahapId))
                    .thenReturn(new ArrayList<>());

            List<String> result = fileUploadService.getUploadedFiles(1L, tahapId);

            assertThat(result).isEmpty();
        }
    }

    @Test
    void testGetUploadedFiles_DataConsistency() {
        mock(TahapUploadedFileRepository.class);

        // Setup file dengan data lengkap
        TahapUploadedFile completeFile = new TahapUploadedFile();
        completeFile.setId(1L);
        completeFile.setKegiatanId(1L);
        completeFile.setTahapId(7);
        completeFile.setOriginalFilename("document.pdf");
        completeFile.setStoredFilename("123456_document.pdf");
        completeFile.setFilePath("/path/to/file");
        completeFile.setFileSize(2048L);
        completeFile.setUploadTimestamp(LocalDateTime.now());

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7))
                .thenReturn(Collections.singletonList(completeFile));

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("123456_document.pdf");
        verify(tahapUploadedFileRepository).findByKegiatanIdAndTahapId(1L, 7);
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testGetUploadedFiles_EmptyStoredFilename() {
        mock(TahapUploadedFileRepository.class);

        TahapUploadedFile fileWithEmptyStoredName = new TahapUploadedFile();
        fileWithEmptyStoredName.setOriginalFilename("test.pdf");
        fileWithEmptyStoredName.setStoredFilename(""); // Empty stored filename

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7))
                .thenReturn(Collections.singletonList(fileWithEmptyStoredName));

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEmpty();
    }

    @Test
    void testGetUploadedFiles_NullStoredFilename() {
        mock(TahapUploadedFileRepository.class);

        TahapUploadedFile fileWithNullStoredName = new TahapUploadedFile();
        fileWithNullStoredName.setOriginalFilename("test.pdf");
        fileWithNullStoredName.setStoredFilename(null); // Null stored filename

        when(tahapUploadedFileRepository.findByKegiatanIdAndTahapId(1L, 7))
                .thenReturn(Collections.singletonList(fileWithNullStoredName));

        List<String> result = fileUploadService.getUploadedFiles(1L, 7);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isNull();
    }
}