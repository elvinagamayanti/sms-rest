package com.sms.unit.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.ProvinceDto;
import com.sms.entity.Province;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;
import com.sms.service.ProvinceService;
import com.sms.service.impl.ProvinceServiceImpl;

public class ProvinceServiceTest {

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private SatkerRepository satkerRepository;

    private ProvinceService provinceService;

    AutoCloseable autoCloseable;
    Province province;
    ProvinceDto provinceDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        provinceService = new ProvinceServiceImpl(provinceRepository, satkerRepository);

        // Setup test data
        province = Province.builder()
                .id(1L)
                .name("DKI Jakarta")
                .code("31")
                .build();

        provinceDto = ProvinceDto.builder()
                .id(1L)
                .name("DKI Jakarta")
                .code("31")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for ambilDaftarProvinsi()
    // ===============================================

    @Test
    public void testAmbilDaftarProvinsi_Success() {
        // Given
        List<Province> provinceList = List.of(province);
        when(provinceRepository.findAll()).thenReturn(provinceList);

        // When
        List<ProvinceDto> result = provinceService.ambilDaftarProvinsi();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("DKI Jakarta");
        assertThat(result.get(0).getCode()).isEqualTo("31");
        verify(provinceRepository).findAll();
    }

    @Test
    public void testAmbilDaftarProvinsi_EmptyList() {
        // Given
        when(provinceRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ProvinceDto> result = provinceService.ambilDaftarProvinsi();

        // Then
        assertThat(result).isEmpty();
        verify(provinceRepository).findAll();
    }

    @Test
    public void testAmbilDaftarProvinsi_WithMultipleProvinces() {
        // Given
        Province province2 = Province.builder()
                .id(2L)
                .name("Jawa Barat")
                .code("32")
                .build();

        List<Province> provinceList = List.of(province, province2);
        when(provinceRepository.findAll()).thenReturn(provinceList);

        // When
        List<ProvinceDto> result = provinceService.ambilDaftarProvinsi();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("DKI Jakarta");
        assertThat(result.get(1).getName()).isEqualTo("Jawa Barat");
        assertThat(result.get(0).getCode()).isEqualTo("31");
        assertThat(result.get(1).getCode()).isEqualTo("32");
        verify(provinceRepository).findAll();
    }

    // ===============================================
    // Test Cases for cariProvinceById()
    // ===============================================

    @Test
    public void testCariProvinceById_Found() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));

        // When
        ProvinceDto result = provinceService.cariProvinceById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("DKI Jakarta");
        assertThat(result.getCode()).isEqualTo("31");
        verify(provinceRepository).findById(1L);
    }

    @Test
    public void testCariProvinceById_NotFound() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            provinceService.cariProvinceById(1L);
        });
        verify(provinceRepository).findById(1L);
    }

    // ===============================================
    // Test Cases for cariProvinceByCode()
    // ===============================================

    @Test
    public void testCariProvinceByCode_Found() {
        // Given
        when(provinceRepository.findByCode("31")).thenReturn(Optional.of(province));

        // When
        ProvinceDto result = provinceService.cariProvinceByCode("31");

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("DKI Jakarta");
        assertThat(result.getCode()).isEqualTo("31");
        verify(provinceRepository).findByCode("31");
    }

    @Test
    public void testCariProvinceByCode_NotFound() {
        // Given
        when(provinceRepository.findByCode("99")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            provinceService.cariProvinceByCode("99");
        });
        verify(provinceRepository).findByCode("99");
    }

    @Test
    public void testCariProvinceByCode_WithDifferentCode() {
        // Given
        Province jawaBarat = Province.builder()
                .id(2L)
                .name("Jawa Barat")
                .code("32")
                .build();

        when(provinceRepository.findByCode("32")).thenReturn(Optional.of(jawaBarat));

        // When
        ProvinceDto result = provinceService.cariProvinceByCode("32");

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Jawa Barat");
        assertThat(result.getCode()).isEqualTo("32");
        verify(provinceRepository).findByCode("32");
    }

    // ===============================================
    // Test Cases for simpanDataProvinsi()
    // ===============================================

    @Test
    public void testSimpanDataProvinsi_Success() {
        // Given
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        provinceService.simpanDataProvinsi(provinceDto);

        // Then
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testSimpanDataProvinsi_WithNewProvince() {
        // Given
        ProvinceDto newProvinceDto = ProvinceDto.builder()
                .name("Jawa Tengah")
                .code("33")
                .build();

        Province savedProvince = Province.builder()
                .id(3L)
                .name("Jawa Tengah")
                .code("33")
                .build();

        when(provinceRepository.save(any(Province.class))).thenReturn(savedProvince);

        // When
        provinceService.simpanDataProvinsi(newProvinceDto);

        // Then
        verify(provinceRepository).save(any(Province.class));
    }

    // ===============================================
    // Test Cases for perbaruiDataProvinsi()
    // ===============================================

    @Test
    public void testPerbaruiDataProvinsi_Success() {
        // Given
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        provinceService.perbaruiDataProvinsi(provinceDto);

        // Then
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPerbaruiDataProvinsi_WithUpdatedData() {
        // Given
        ProvinceDto updatedProvinceDto = ProvinceDto.builder()
                .id(1L)
                .name("DKI Jakarta - Updated")
                .code("31")
                .build();

        Province updatedProvince = Province.builder()
                .id(1L)
                .name("DKI Jakarta - Updated")
                .code("31")
                .build();

        when(provinceRepository.save(any(Province.class))).thenReturn(updatedProvince);

        // When
        provinceService.perbaruiDataProvinsi(updatedProvinceDto);

        // Then
        verify(provinceRepository).save(any(Province.class));
    }

    // ===============================================
    // Test Cases for hapusDataProvinsi()
    // ===============================================

    @Test
    public void testHapusDataProvinsi_Success() {
        // Given
        doNothing().when(provinceRepository).deleteById(1L);

        // When
        provinceService.hapusDataProvinsi(1L);

        // Then
        verify(provinceRepository).deleteById(1L);
    }

    @Test
    public void testHapusDataProvinsi_WithDifferentId() {
        // Given
        doNothing().when(provinceRepository).deleteById(2L);

        // When
        provinceService.hapusDataProvinsi(2L);

        // Then
        verify(provinceRepository).deleteById(2L);
    }

    // ===============================================
    // Test Cases for patchProvince()
    // ===============================================

    @Test
    public void testPatchProvince_Success() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "DKI Jakarta - Patched");
        updates.put("code", "31");

        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPatchProvince_ProvinceNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Province Name");

        when(provinceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            provinceService.patchProvince(1L, updates);
        });
        verify(provinceRepository).findById(1L);
    }

    @Test
    public void testPatchProvince_EmptyUpdates() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPatchProvince_OnlyNameUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "New Province Name");

        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPatchProvince_OnlyCodeUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code", "99");

        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPatchProvince_WithNullValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", null);
        updates.put("code", "31");

        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testPatchProvince_WithUnknownFields() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("unknownField", "should be ignored");

        when(provinceRepository.findById(1L)).thenReturn(Optional.of(province));
        when(provinceRepository.save(any(Province.class))).thenReturn(province);

        // When
        ProvinceDto result = provinceService.patchProvince(1L, updates);

        // Then
        assertNotNull(result);
        verify(provinceRepository).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testAmbilDaftarProvinsi_RepositoryException() {
        // Given
        when(provinceRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            provinceService.ambilDaftarProvinsi();
        });
        verify(provinceRepository).findAll();
    }

    @Test
    public void testSimpanDataProvinsi_RepositoryException() {
        // Given
        when(provinceRepository.save(any(Province.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            provinceService.simpanDataProvinsi(provinceDto);
        });
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    public void testCariProvinceByCode_WithSpecialCharacters() {
        // Given
        Province specialProvince = Province.builder()
                .id(3L)
                .name("Special Province")
                .code("00")
                .build();

        when(provinceRepository.findByCode("00")).thenReturn(Optional.of(specialProvince));

        // When
        ProvinceDto result = provinceService.cariProvinceByCode("00");

        // Then
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Special Province");
        assertThat(result.getCode()).isEqualTo("00");
        verify(provinceRepository).findByCode("00");
    }
}