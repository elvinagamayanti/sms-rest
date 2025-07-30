package com.sms.unit.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.SubtahapDetailDto;
import com.sms.dto.TahapStatusDto;
import com.sms.entity.Kegiatan;
import com.sms.entity.Tahap1;
import com.sms.entity.Tahap2;
import com.sms.entity.Tahap3;
import com.sms.entity.Tahap4;
import com.sms.entity.Tahap5;
import com.sms.entity.Tahap6;
import com.sms.entity.Tahap7;
import com.sms.entity.Tahap8;
import com.sms.repository.Tahap1Repository;
import com.sms.repository.Tahap2Repository;
import com.sms.repository.Tahap3Repository;
import com.sms.repository.Tahap4Repository;
import com.sms.repository.Tahap5Repository;
import com.sms.repository.Tahap6Repository;
import com.sms.repository.Tahap7Repository;
import com.sms.repository.Tahap8Repository;
import com.sms.service.FileUploadService;
import com.sms.service.TahapService;

public class TahapServiceTest {

    @Mock
    private Tahap1Repository tahap1Repository;

    @Mock
    private Tahap2Repository tahap2Repository;

    @Mock
    private Tahap3Repository tahap3Repository;

    @Mock
    private Tahap4Repository tahap4Repository;

    @Mock
    private Tahap5Repository tahap5Repository;

    @Mock
    private Tahap6Repository tahap6Repository;

    @Mock
    private Tahap7Repository tahap7Repository;

    @Mock
    private Tahap8Repository tahap8Repository;

    @Mock
    private FileUploadService fileUploadService;

    private TahapService tahapService;

    AutoCloseable autoCloseable;
    Kegiatan kegiatan;
    Tahap1 tahap1;
    Tahap2 tahap2;
    Tahap3 tahap3;
    Tahap4 tahap4;
    Tahap5 tahap5;
    Tahap6 tahap6;
    Tahap7 tahap7;
    Tahap8 tahap8;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        tahapService = new TahapService(tahap1Repository, tahap2Repository, tahap3Repository,
                tahap4Repository, tahap5Repository, tahap6Repository, tahap7Repository,
                tahap8Repository, fileUploadService);

        // Setup test data
        kegiatan = new Kegiatan();
        kegiatan.setId(1L);
        kegiatan.setName("Test Kegiatan");

        // Setup Tahap1
        tahap1 = new Tahap1();
        tahap1.setId(1L);
        tahap1.setKegiatan(kegiatan);
        tahap1.setSubtahap_1(true);
        tahap1.setSubtahap_2(false);
        tahap1.setSubtahap_3(true);
        tahap1.setSubtahap_4(false);
        tahap1.setSubtahap_5(true);
        tahap1.setSubtahap_6(false);
        tahap1.setSubtahap_1_tanggal_perencanaan(LocalDate.now());
        tahap1.setSubtahap_1_tanggal_realisasi(LocalDate.now());

        // Setup Tahap2
        tahap2 = new Tahap2();
        tahap2.setId(1L);
        tahap2.setKegiatan(kegiatan);
        tahap2.setSubtahap_1(true);
        tahap2.setSubtahap_2(true);
        tahap2.setSubtahap_3(false);
        tahap2.setSubtahap_4(false);
        tahap2.setSubtahap_5(false);
        tahap2.setSubtahap_6(false);

        // Setup other Tahap entities with minimal data
        tahap3 = new Tahap3();
        tahap3.setId(1L);
        tahap3.setKegiatan(kegiatan);
        tahap3.setSubtahap_1(false);

        tahap4 = new Tahap4();
        tahap4.setId(1L);
        tahap4.setKegiatan(kegiatan);
        tahap4.setSubtahap_1(false);

        tahap5 = new Tahap5();
        tahap5.setId(1L);
        tahap5.setKegiatan(kegiatan);
        tahap5.setSubtahap_1(false);

        tahap6 = new Tahap6();
        tahap6.setId(1L);
        tahap6.setKegiatan(kegiatan);
        tahap6.setSubtahap_1(false);

        tahap7 = new Tahap7();
        tahap7.setId(1L);
        tahap7.setKegiatan(kegiatan);
        tahap7.setSubtahap_1(false);

        tahap8 = new Tahap8();
        tahap8.setId(1L);
        tahap8.setKegiatan(kegiatan);
        tahap8.setSubtahap_1(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for isSubtaskCompleted()
    // ===============================================

    @Test
    void testIsSubtaskCompleted_Tahap1_SubtahapCompleted() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));

        boolean result = tahapService.isSubtaskCompleted(1L, 1, 1);

        assertThat(result).isTrue();
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testIsSubtaskCompleted_Tahap1_SubtahapNotCompleted() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));

        boolean result = tahapService.isSubtaskCompleted(1L, 1, 2);

        assertThat(result).isFalse();
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testIsSubtaskCompleted_Tahap1_NotFound() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());

        boolean result = tahapService.isSubtaskCompleted(1L, 1, 1);

        assertThat(result).isFalse();
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testIsSubtaskCompleted_Tahap2_SubtahapCompleted() {
        mock(Tahap2Repository.class);

        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap2));

        boolean result = tahapService.isSubtaskCompleted(1L, 2, 1);

        assertThat(result).isTrue();
        verify(tahap2Repository).findByKegiatanId(1L);
    }

    @Test
    void testIsSubtaskCompleted_InvalidTahap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.isSubtaskCompleted(1L, 9, 1));

        assertThat(exception.getMessage()).contains("Invalid tahap: 9");
    }

    @Test
    void testIsSubtaskCompleted_InvalidSubtahap() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.isSubtaskCompleted(1L, 1, 7));

        assertThat(exception.getMessage()).contains("Invalid subtahap: 7");
    }

    // ===============================================
    // Test Cases for getTahapCompletionPercentage()
    // ===============================================

    @Test
    void testGetTahapCompletionPercentage_Tahap1_WithData() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap1.getCompletionPercentage()).thenReturn(50);

        int result = tahapService.getTahapCompletionPercentage(1L, 1);

        assertThat(result).isEqualTo(50);
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testGetTahapCompletionPercentage_Tahap1_NoData() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());

        int result = tahapService.getTahapCompletionPercentage(1L, 1);

        assertThat(result).isEqualTo(0);
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testGetTahapCompletionPercentage_AllTahap() {
        mock(Tahap1Repository.class);
        mock(Tahap2Repository.class);
        mock(Tahap3Repository.class);
        mock(Tahap4Repository.class);
        mock(Tahap5Repository.class);
        mock(Tahap6Repository.class);
        mock(Tahap7Repository.class);
        mock(Tahap8Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap2));
        when(tahap3Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap3));
        when(tahap4Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap4));
        when(tahap5Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap5));
        when(tahap6Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap6));
        when(tahap7Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap7));
        when(tahap8Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap8));

        when(tahap1.getCompletionPercentage()).thenReturn(50);
        when(tahap2.getCompletionPercentage()).thenReturn(33);
        when(tahap3.getCompletionPercentage()).thenReturn(0);
        when(tahap4.getCompletionPercentage()).thenReturn(25);
        when(tahap5.getCompletionPercentage()).thenReturn(75);
        when(tahap6.getCompletionPercentage()).thenReturn(60);
        when(tahap7.getCompletionPercentage()).thenReturn(80);
        when(tahap8.getCompletionPercentage()).thenReturn(100);

        // Test all tahap
        assertThat(tahapService.getTahapCompletionPercentage(1L, 1)).isEqualTo(50);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 2)).isEqualTo(33);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 3)).isEqualTo(0);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 4)).isEqualTo(25);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 5)).isEqualTo(75);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 6)).isEqualTo(60);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 7)).isEqualTo(80);
        assertThat(tahapService.getTahapCompletionPercentage(1L, 8)).isEqualTo(100);
    }

    @Test
    void testGetTahapCompletionPercentage_InvalidTahap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.getTahapCompletionPercentage(1L, 9));

        assertThat(exception.getMessage()).contains("Invalid tahap: 9");
    }

    // ===============================================
    // Test Cases for getTahapStatus()
    // ===============================================

    @Test
    void testGetTahapStatus_WithAllData() {
        mock(Tahap1Repository.class);
        mock(Tahap2Repository.class);
        mock(Tahap3Repository.class);
        mock(Tahap4Repository.class);
        mock(Tahap5Repository.class);
        mock(Tahap6Repository.class);
        mock(Tahap7Repository.class);
        mock(Tahap8Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap2));
        when(tahap3Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap3));
        when(tahap4Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap4));
        when(tahap5Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap5));
        when(tahap6Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap6));
        when(tahap7Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap7));
        when(tahap8Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap8));

        when(tahap1.getCompletionPercentage()).thenReturn(50);
        when(tahap2.getCompletionPercentage()).thenReturn(33);
        when(tahap3.getCompletionPercentage()).thenReturn(0);
        when(tahap4.getCompletionPercentage()).thenReturn(25);
        when(tahap5.getCompletionPercentage()).thenReturn(75);
        when(tahap6.getCompletionPercentage()).thenReturn(60);
        when(tahap7.getCompletionPercentage()).thenReturn(80);
        when(tahap8.getCompletionPercentage()).thenReturn(100);

        TahapStatusDto result = tahapService.getTahapStatus(1L);

        assertThat(result.getKegiatanId()).isEqualTo(1L);
        assertThat(result.getTahap1Percentage()).isEqualTo(50);
        assertThat(result.getTahap2Percentage()).isEqualTo(33);
        assertThat(result.getTahap3Percentage()).isEqualTo(0);
        assertThat(result.getTahap4Percentage()).isEqualTo(25);
        assertThat(result.getTahap5Percentage()).isEqualTo(75);
        assertThat(result.getTahap6Percentage()).isEqualTo(60);
        assertThat(result.getTahap7Percentage()).isEqualTo(80);
        assertThat(result.getTahap8Percentage()).isEqualTo(100);

        verify(tahap1Repository).findByKegiatanId(1L);
        verify(tahap2Repository).findByKegiatanId(1L);
        verify(tahap3Repository).findByKegiatanId(1L);
        verify(tahap4Repository).findByKegiatanId(1L);
        verify(tahap5Repository).findByKegiatanId(1L);
        verify(tahap6Repository).findByKegiatanId(1L);
        verify(tahap7Repository).findByKegiatanId(1L);
        verify(tahap8Repository).findByKegiatanId(1L);
    }

    @Test
    void testGetTahapStatus_WithEmptyData() {
        mock(Tahap1Repository.class);
        mock(Tahap2Repository.class);
        mock(Tahap3Repository.class);
        mock(Tahap4Repository.class);
        mock(Tahap5Repository.class);
        mock(Tahap6Repository.class);
        mock(Tahap7Repository.class);
        mock(Tahap8Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap3Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap4Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap5Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap6Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap7Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap8Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());

        TahapStatusDto result = tahapService.getTahapStatus(1L);

        assertThat(result.getKegiatanId()).isEqualTo(1L);
        assertThat(result.getTahap1Percentage()).isEqualTo(0);
        assertThat(result.getTahap2Percentage()).isEqualTo(0);
        assertThat(result.getTahap3Percentage()).isEqualTo(0);
        assertThat(result.getTahap4Percentage()).isEqualTo(0);
        assertThat(result.getTahap5Percentage()).isEqualTo(0);
        assertThat(result.getTahap6Percentage()).isEqualTo(0);
        assertThat(result.getTahap7Percentage()).isEqualTo(0);
        assertThat(result.getTahap8Percentage()).isEqualTo(0);
    }

    // ===============================================
    // Test Cases for updateSubtaskStatus()
    // ===============================================

    @Test
    void testUpdateSubtaskStatus_Tahap1_Existing() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap1Repository.save(Mockito.any(Tahap1.class))).thenReturn(tahap1);

        tahapService.updateSubtaskStatus(1L, 1, 1, true);

        verify(tahap1Repository).findByKegiatanId(1L);
        verify(tahap1Repository).save(Mockito.any(Tahap1.class));
    }

    @Test
    void testUpdateSubtaskStatus_Tahap1_CreateNew() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());
        when(tahap1Repository.save(Mockito.any(Tahap1.class))).thenReturn(tahap1);

        tahapService.updateSubtaskStatus(1L, 1, 1, true);

        verify(tahap1Repository).findByKegiatanId(1L);
        verify(tahap1Repository).save(Mockito.any(Tahap1.class));
    }

    @Test
    void testUpdateSubtaskStatus_Tahap2() {
        mock(Tahap2Repository.class);

        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap2));
        when(tahap2Repository.save(Mockito.any(Tahap2.class))).thenReturn(tahap2);

        tahapService.updateSubtaskStatus(1L, 2, 1, true);

        verify(tahap2Repository).findByKegiatanId(1L);
        verify(tahap2Repository).save(Mockito.any(Tahap2.class));
    }

    @Test
    void testUpdateSubtaskStatus_AllTahap() {
        mock(Tahap1Repository.class);
        mock(Tahap2Repository.class);
        mock(Tahap3Repository.class);
        mock(Tahap4Repository.class);
        mock(Tahap5Repository.class);
        mock(Tahap6Repository.class);
        mock(Tahap7Repository.class);
        mock(Tahap8Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap2Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap2));
        when(tahap3Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap3));
        when(tahap4Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap4));
        when(tahap5Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap5));
        when(tahap6Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap6));
        when(tahap7Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap7));
        when(tahap8Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap8));

        when(tahap1Repository.save(Mockito.any(Tahap1.class))).thenReturn(tahap1);
        when(tahap2Repository.save(Mockito.any(Tahap2.class))).thenReturn(tahap2);
        when(tahap3Repository.save(Mockito.any(Tahap3.class))).thenReturn(tahap3);
        when(tahap4Repository.save(Mockito.any(Tahap4.class))).thenReturn(tahap4);
        when(tahap5Repository.save(Mockito.any(Tahap5.class))).thenReturn(tahap5);
        when(tahap6Repository.save(Mockito.any(Tahap6.class))).thenReturn(tahap6);
        when(tahap7Repository.save(Mockito.any(Tahap7.class))).thenReturn(tahap7);
        when(tahap8Repository.save(Mockito.any(Tahap8.class))).thenReturn(tahap8);

        // Test update all tahap
        tahapService.updateSubtaskStatus(1L, 1, 1, true);
        tahapService.updateSubtaskStatus(1L, 2, 1, true);
        tahapService.updateSubtaskStatus(1L, 3, 1, true);
        tahapService.updateSubtaskStatus(1L, 4, 1, true);
        tahapService.updateSubtaskStatus(1L, 5, 1, true);
        tahapService.updateSubtaskStatus(1L, 6, 1, true);
        tahapService.updateSubtaskStatus(1L, 7, 1, true);
        tahapService.updateSubtaskStatus(1L, 8, 1, true);

        verify(tahap1Repository).save(Mockito.any(Tahap1.class));
        verify(tahap2Repository).save(Mockito.any(Tahap2.class));
        verify(tahap3Repository).save(Mockito.any(Tahap3.class));
        verify(tahap4Repository).save(Mockito.any(Tahap4.class));
        verify(tahap5Repository).save(Mockito.any(Tahap5.class));
        verify(tahap6Repository).save(Mockito.any(Tahap6.class));
        verify(tahap7Repository).save(Mockito.any(Tahap7.class));
        verify(tahap8Repository).save(Mockito.any(Tahap8.class));
    }

    @Test
    void testUpdateSubtaskStatus_InvalidTahap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.updateSubtaskStatus(1L, 9, 1, true));

        assertThat(exception.getMessage()).contains("Invalid tahap: 9");
    }

    // ===============================================
    // Test Cases for getSubtahapDetail()
    // ===============================================

    @Test
    void testGetSubtahapDetail_Tahap1_WithData() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));

        SubtahapDetailDto result = tahapService.getSubtahapDetail(1L, 1, 1);

        assertThat(result).isNotNull();
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    @Test
    void testGetSubtahapDetail_Tahap1_NoData() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.empty());

        SubtahapDetailDto result = tahapService.getSubtahapDetail(1L, 1, 1);

        assertThat(result).isNotNull();
        assertThat(result.isCompleted()).isFalse();
        verify(tahap1Repository).findByKegiatanId(1L);
    }

    // ===============================================
    // Test Cases for Date Update Methods
    // ===============================================

    @Test
    void testUpdateSubtahapTanggalPerencanaan_Tahap1() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap1Repository.save(Mockito.any(Tahap1.class))).thenReturn(tahap1);

        LocalDate testDate = LocalDate.now().plusDays(7);
        tahapService.updateSubtahapTanggalPerencanaan(1L, 1, 1, testDate);

        verify(tahap1Repository).findByKegiatanId(1L);
        verify(tahap1Repository).save(Mockito.any(Tahap1.class));
    }

    @Test
    void testUpdateSubtahapTanggalRealisasi_Tahap1() {
        mock(Tahap1Repository.class);

        when(tahap1Repository.findByKegiatanId(1L)).thenReturn(Optional.of(tahap1));
        when(tahap1Repository.save(Mockito.any(Tahap1.class))).thenReturn(tahap1);

        LocalDate testDate = LocalDate.now();
        tahapService.updateSubtahapTanggalRealisasi(1L, 1, 1, testDate);

        verify(tahap1Repository).findByKegiatanId(1L);
        verify(tahap1Repository).save(Mockito.any(Tahap1.class));
    }

    @Test
    void testUpdateSubtahapTanggalPerencanaan_InvalidTahap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.updateSubtahapTanggalPerencanaan(1L, 9, 1, LocalDate.now()));

        assertThat(exception.getMessage()).contains("Invalid tahap: 9");
    }

    @Test
    void testUpdateSubtahapTanggalRealisasi_InvalidTahap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tahapService.updateSubtahapTanggalRealisasi(1L, 9, 1, LocalDate.now()));

        assertThat(exception.getMessage()).contains("Invalid tahap: 9");
    }
}