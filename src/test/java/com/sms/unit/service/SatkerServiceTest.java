package com.sms.unit.service;

import java.util.Collections;
import java.util.Date;
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
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.SatkerDto;
import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.SatkerService;
import com.sms.service.impl.SatkerServiceImpl;

public class SatkerServiceTest {

    @Mock
    private SatkerRepository satkerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProvinceRepository provinceRepository;

    private SatkerService satkerService;

    AutoCloseable autoCloseable;
    Satker satker;
    SatkerDto satkerDto;
    Province province;
    User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        satkerService = new SatkerServiceImpl(satkerRepository, userRepository, provinceRepository);

        // Setup test data
        province = Province.builder()
                .id(1L)
                .code("01")
                .name("Test Province")
                .build();

        satker = Satker.builder()
                .id(1L)
                .name("Test Satker")
                .code("0100")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .province(province)
                .isProvince(false)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        satkerDto = SatkerDto.builder()
                .id(1L)
                .name("Test Satker")
                .code("0100")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .province(province)
                .isProvince(false)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .satker(satker)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for ambilDaftarSatker()
    // ===============================================

    @Test
    public void testAmbilDaftarSatker_Success() {
        // Given
        List<Satker> satkerList = List.of(satker);
        when(satkerRepository.findAll()).thenReturn(satkerList);

        // When
        List<SatkerDto> result = satkerService.ambilDaftarSatker();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Satker");
        assertThat(result.get(0).getCode()).isEqualTo("0100");
        verify(satkerRepository).findAll();
    }

    @Test
    public void testAmbilDaftarSatker_EmptyList() {
        // Given
        when(satkerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<SatkerDto> result = satkerService.ambilDaftarSatker();

        // Then
        assertThat(result).isEmpty();
        verify(satkerRepository).findAll();
    }

    // ===============================================
    // Test Cases for cariSatkerById()
    // ===============================================

    @Test
    public void testCariSatkerById_Found() {
        // Given
        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));

        // When
        SatkerDto result = satkerService.cariSatkerById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Satker");
        assertThat(result.getCode()).isEqualTo("0100");
        assertThat(result.getAddress()).isEqualTo("Test Address");
        assertThat(result.getNumber()).isEqualTo("08123456789");
        assertThat(result.getEmail()).isEqualTo("test@satker.com");
        verify(satkerRepository).findById(1L);
    }

    @Test
    public void testCariSatkerById_NotFound() {
        // Given
        when(satkerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            satkerService.cariSatkerById(1L);
        });
        verify(satkerRepository).findById(1L);
    }

    // ===============================================
    // Test Cases for simpanDataSatker()
    // ===============================================

    @Test
    public void testSimpanDataSatker_Success() {
        // Given
        when(provinceRepository.findByCode("01")).thenReturn(Optional.of(province));
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        satkerService.simpanDataSatker(satkerDto);

        // Then
        verify(provinceRepository).findByCode("01");
        verify(satkerRepository).save(any(Satker.class));
    }

    @Test
    public void testSimpanDataSatker_ProvinceNotFound() {
        // Given
        when(provinceRepository.findByCode("01")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            satkerService.simpanDataSatker(satkerDto);
        });
        verify(provinceRepository).findByCode("01");
    }

    @Test
    public void testSimpanDataSatker_CodeTooShort() {
        // Given
        satkerDto.setCode("1"); // Code with less than 2 characters
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        satkerService.simpanDataSatker(satkerDto);

        // Then
        verify(satkerRepository).save(any(Satker.class));
        // Should not call provinceRepository when code is too short
        Mockito.verifyNoInteractions(provinceRepository);
    }

    @Test
    public void testSimpanDataSatker_NullCode() {
        // Given
        satkerDto.setCode(null);
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        satkerService.simpanDataSatker(satkerDto);

        // Then
        verify(satkerRepository).save(any(Satker.class));
        // Should not call provinceRepository when code is null
        Mockito.verifyNoInteractions(provinceRepository);
    }

    // ===============================================
    // Test Cases for perbaruiDataSatker()
    // ===============================================

    @Test
    public void testPerbaruiDataSatker_Success() {
        // Given
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        satkerService.perbaruiDataSatker(satkerDto);

        // Then
        verify(satkerRepository).save(any(Satker.class));
    }

    // ===============================================
    // Test Cases for hapusDataSatker()
    // ===============================================

    @Test
    public void testHapusDataSatker_Success() {
        // Given
        doNothing().when(satkerRepository).deleteById(1L);

        // When
        satkerService.hapusDataSatker(1L);

        // Then
        verify(satkerRepository).deleteById(1L);
    }

    // ===============================================
    // Test Cases for getUsersBySatkerId()
    // ===============================================

    @Test
    public void testGetUsersBySatkerId_Success() {
        // Given
        List<User> userList = List.of(user);
        when(userRepository.findAllUsersBySatkerId(1L)).thenReturn(userList);

        // When
        List<User> result = satkerService.getUsersBySatkerId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
        assertThat(result.get(0).getEmail()).isEqualTo("user@test.com");
        verify(userRepository).findAllUsersBySatkerId(1L);
    }

    @Test
    public void testGetUsersBySatkerId_EmptyList() {
        // Given
        when(userRepository.findAllUsersBySatkerId(1L)).thenReturn(Collections.emptyList());

        // When
        List<User> result = satkerService.getUsersBySatkerId(1L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAllUsersBySatkerId(1L);
    }

    // ===============================================
    // Test Cases for patchSatker()
    // ===============================================

    @Test
    public void testPatchSatker_Success() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Satker Name");
        updates.put("email", "updated@satker.com");

        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        SatkerDto result = satkerService.patchSatker(1L, updates);

        // Then
        assertNotNull(result);
        verify(satkerRepository).findById(1L);
        verify(satkerRepository).save(any(Satker.class));
    }

    @Test
    public void testPatchSatker_SatkerNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Satker Name");

        when(satkerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            satkerService.patchSatker(1L, updates);
        });
        verify(satkerRepository).findById(1L);
    }

    @Test
    public void testPatchSatker_EmptyUpdates() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        SatkerDto result = satkerService.patchSatker(1L, updates);

        // Then
        assertNotNull(result);
        verify(satkerRepository).findById(1L);
        verify(satkerRepository).save(any(Satker.class));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testAmbilDaftarSatker_WithMultipleSatkers() {
        // Given
        Satker satker2 = Satker.builder()
                .id(2L)
                .name("Test Satker 2")
                .code("0200")
                .address("Test Address 2")
                .number("08987654321")
                .email("test2@satker.com")
                .province(province)
                .isProvince(true)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        List<Satker> satkerList = List.of(satker, satker2);
        when(satkerRepository.findAll()).thenReturn(satkerList);

        // When
        List<SatkerDto> result = satkerService.ambilDaftarSatker();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Satker");
        assertThat(result.get(1).getName()).isEqualTo("Test Satker 2");
        assertThat(result.get(1).getIsProvince()).isTrue();
        verify(satkerRepository).findAll();
    }

    @Test
    public void testSimpanDataSatker_WithProvinceCode() {
        // Given
        satkerDto.setCode("3100"); // Jakarta province code
        Province jakartaProvince = Province.builder()
                .id(2L)
                .code("31")
                .name("DKI Jakarta")
                .build();

        when(provinceRepository.findByCode("31")).thenReturn(Optional.of(jakartaProvince));
        when(satkerRepository.save(any(Satker.class))).thenReturn(satker);

        // When
        satkerService.simpanDataSatker(satkerDto);

        // Then
        verify(provinceRepository).findByCode("31");
        verify(satkerRepository).save(any(Satker.class));
    }
}