package com.sms.unit.service;

import java.util.ArrayList;
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

import com.sms.dto.DeputiDto;
import com.sms.entity.Deputi;
import com.sms.entity.User;
import com.sms.repository.DeputiRepository;
import com.sms.repository.UserRepository;
import com.sms.service.DeputiService;
import com.sms.service.impl.DeputiServiceImpl;

public class DeputiServiceTest {

    @Mock
    private DeputiRepository deputiRepository;

    @Mock
    private UserRepository userRepository;

    private DeputiService deputiService;

    AutoCloseable autoCloseable;
    Deputi deputi;
    DeputiDto deputiDto;
    User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        deputiService = new DeputiServiceImpl(deputiRepository, userRepository);

        // Setup test data
        deputi = Deputi.builder()
                .id(1L)
                .name("Test Deputi")
                .code("D01")
                .build();

        deputiDto = DeputiDto.builder()
                .id(1L)
                .name("Test Deputi")
                .code("D01")
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for ambilDaftarDeputi()
    // ===============================================

    @Test
    public void testAmbilDaftarDeputi_Success() {
        // Given
        List<Deputi> deputiList = List.of(deputi);
        when(deputiRepository.findAll()).thenReturn(deputiList);

        // When
        List<DeputiDto> result = deputiService.ambilDaftarDeputi();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Deputi");
        assertThat(result.get(0).getCode()).isEqualTo("D01");
        verify(deputiRepository).findAll();
    }

    @Test
    public void testAmbilDaftarDeputi_EmptyList() {
        // Given
        when(deputiRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DeputiDto> result = deputiService.ambilDaftarDeputi();

        // Then
        assertThat(result).isEmpty();
        verify(deputiRepository).findAll();
    }

    @Test
    public void testAmbilDaftarDeputi_WithMultipleDeputis() {
        // Given
        Deputi deputi2 = Deputi.builder()
                .id(2L)
                .name("Test Deputi 2")
                .code("D02")
                .build();

        List<Deputi> deputiList = List.of(deputi, deputi2);
        when(deputiRepository.findAll()).thenReturn(deputiList);

        // When
        List<DeputiDto> result = deputiService.ambilDaftarDeputi();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Deputi");
        assertThat(result.get(1).getName()).isEqualTo("Test Deputi 2");
        assertThat(result.get(0).getCode()).isEqualTo("D01");
        assertThat(result.get(1).getCode()).isEqualTo("D02");
        verify(deputiRepository).findAll();
    }

    // ===============================================
    // Test Cases for cariDeputiById()
    // ===============================================

    @Test
    public void testCariDeputiById_Found() {
        // Given
        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));

        // When
        DeputiDto result = deputiService.cariDeputiById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deputi");
        assertThat(result.getCode()).isEqualTo("D01");
        verify(deputiRepository).findById(1L);
    }

    @Test
    public void testCariDeputiById_NotFound() {
        // Given
        when(deputiRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deputiService.cariDeputiById(1L);
        });

        assertThat(exception.getMessage()).contains("Deputi not found with id: 1");
        verify(deputiRepository).findById(1L);
    }

    @Test
    public void testCariDeputiById_WithDifferentId() {
        // Given
        Deputi deputi2 = Deputi.builder()
                .id(2L)
                .name("Different Deputi")
                .code("D99")
                .build();

        when(deputiRepository.findById(2L)).thenReturn(Optional.of(deputi2));

        // When
        DeputiDto result = deputiService.cariDeputiById(2L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Different Deputi");
        assertThat(result.getCode()).isEqualTo("D99");
        verify(deputiRepository).findById(2L);
    }

    // ===============================================
    // Test Cases for cariDeputiByCode()
    // ===============================================

    @Test
    public void testCariDeputiByCode_Found() {
        // Given
        when(deputiRepository.findByCode("D01")).thenReturn(Optional.of(deputi));

        // When
        DeputiDto result = deputiService.cariDeputiByCode("D01");

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Deputi");
        assertThat(result.getCode()).isEqualTo("D01");
        verify(deputiRepository).findByCode("D01");
    }

    @Test
    public void testCariDeputiByCode_NotFound() {
        // Given
        when(deputiRepository.findByCode("D99")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deputiService.cariDeputiByCode("D99");
        });

        assertThat(exception.getMessage()).contains("Deputi not found with code: D99");
        verify(deputiRepository).findByCode("D99");
    }

    @Test
    public void testCariDeputiByCode_WithDifferentCode() {
        // Given
        Deputi deputiSpecial = Deputi.builder()
                .id(3L)
                .name("Special Deputi")
                .code("SP01")
                .build();

        when(deputiRepository.findByCode("SP01")).thenReturn(Optional.of(deputiSpecial));

        // When
        DeputiDto result = deputiService.cariDeputiByCode("SP01");

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Special Deputi");
        assertThat(result.getCode()).isEqualTo("SP01");
        verify(deputiRepository).findByCode("SP01");
    }

    // ===============================================
    // Test Cases for simpanDataDeputi()
    // ===============================================

    @Test
    public void testSimpanDataDeputi_Success() {
        // Given
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        deputiService.simpanDataDeputi(deputiDto);

        // Then
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testSimpanDataDeputi_WithNewDeputi() {
        // Given
        DeputiDto newDeputiDto = DeputiDto.builder()
                .name("New Deputi")
                .code("ND01")
                .build();

        Deputi savedDeputi = Deputi.builder()
                .id(3L)
                .name("New Deputi")
                .code("ND01")
                .build();

        when(deputiRepository.save(any(Deputi.class))).thenReturn(savedDeputi);

        // When
        deputiService.simpanDataDeputi(newDeputiDto);

        // Then
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testSimpanDataDeputi_WithSpecialCharacters() {
        // Given
        DeputiDto specialDeputiDto = DeputiDto.builder()
                .name("Deputi with Special Characters !@#")
                .code("DSC01")
                .build();

        Deputi savedDeputi = Deputi.builder()
                .id(4L)
                .name("Deputi with Special Characters !@#")
                .code("DSC01")
                .build();

        when(deputiRepository.save(any(Deputi.class))).thenReturn(savedDeputi);

        // When
        deputiService.simpanDataDeputi(specialDeputiDto);

        // Then
        verify(deputiRepository).save(any(Deputi.class));
    }

    // ===============================================
    // Test Cases for perbaruiDataDeputi()
    // ===============================================

    @Test
    public void testPerbaruiDataDeputi_Success() {
        // Given
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        deputiService.perbaruiDataDeputi(deputiDto);

        // Then
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPerbaruiDataDeputi_WithUpdatedData() {
        // Given
        DeputiDto updatedDeputiDto = DeputiDto.builder()
                .id(1L)
                .name("Updated Test Deputi")
                .code("UTD01")
                .build();

        Deputi updatedDeputi = Deputi.builder()
                .id(1L)
                .name("Updated Test Deputi")
                .code("UTD01")
                .build();

        when(deputiRepository.save(any(Deputi.class))).thenReturn(updatedDeputi);

        // When
        deputiService.perbaruiDataDeputi(updatedDeputiDto);

        // Then
        verify(deputiRepository).save(any(Deputi.class));
    }

    // ===============================================
    // Test Cases for hapusDataDeputi()
    // ===============================================

    @Test
    public void testHapusDataDeputi_Success() {
        // Given
        doNothing().when(deputiRepository).deleteById(1L);

        // When
        deputiService.hapusDataDeputi(1L);

        // Then
        verify(deputiRepository).deleteById(1L);
    }

    @Test
    public void testHapusDataDeputi_WithDifferentId() {
        // Given
        doNothing().when(deputiRepository).deleteById(2L);

        // When
        deputiService.hapusDataDeputi(2L);

        // Then
        verify(deputiRepository).deleteById(2L);
    }

    // ===============================================
    // Test Cases for getUsersByDeputiId()
    // ===============================================

    @Test
    public void testGetUsersByDeputiId_Success() {
        // Given
        List<User> userList = List.of(user);
        when(userRepository.findAllUsersByDeputiId(1L)).thenReturn(userList);

        // When
        List<User> result = deputiService.getUsersByDeputiId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
        assertThat(result.get(0).getEmail()).isEqualTo("user@test.com");
        verify(userRepository).findAllUsersByDeputiId(1L);
    }

    @Test
    public void testGetUsersByDeputiId_EmptyList() {
        // Given
        when(userRepository.findAllUsersByDeputiId(1L)).thenReturn(Collections.emptyList());

        // When
        List<User> result = deputiService.getUsersByDeputiId(1L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAllUsersByDeputiId(1L);
    }

    @Test
    public void testGetUsersByDeputiId_WithMultipleUsers() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .name("Test User 2")
                .email("user2@test.com")
                .build();

        List<User> userList = List.of(user, user2);
        when(userRepository.findAllUsersByDeputiId(1L)).thenReturn(userList);

        // When
        List<User> result = deputiService.getUsersByDeputiId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
        assertThat(result.get(1).getName()).isEqualTo("Test User 2");
        verify(userRepository).findAllUsersByDeputiId(1L);
    }

    // ===============================================
    // Test Cases for patchDeputi()
    // ===============================================

    @Test
    public void testPatchDeputi_Success() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Patched Deputi Name");
        updates.put("code", "PD01");

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_DeputiNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Deputi Name");

        when(deputiRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deputiService.patchDeputi(1L, updates);
        });

        assertThat(exception.getMessage()).contains("Deputi not found with id: 1");
        verify(deputiRepository).findById(1L);
    }

    @Test
    public void testPatchDeputi_EmptyUpdates() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_OnlyNameUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "New Deputi Name");

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_OnlyCodeUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code", "NTC01");

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_WithNullValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", null);
        updates.put("code", "TC01");

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_WithUnknownFields() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("unknownField", "should be ignored");
        updates.put("anotherUnknownField", 123);

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testPatchDeputi_AllFieldsUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Completely Updated Deputi");
        updates.put("code", "CUD01");

        when(deputiRepository.findById(1L)).thenReturn(Optional.of(deputi));
        when(deputiRepository.save(any(Deputi.class))).thenReturn(deputi);

        // When
        DeputiDto result = deputiService.patchDeputi(1L, updates);

        // Then
        assertNotNull(result);
        verify(deputiRepository).findById(1L);
        verify(deputiRepository).save(any(Deputi.class));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testAmbilDaftarDeputi_RepositoryException() {
        // Given
        when(deputiRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            deputiService.ambilDaftarDeputi();
        });
        verify(deputiRepository).findAll();
    }

    @Test
    public void testSimpanDataDeputi_RepositoryException() {
        // Given
        when(deputiRepository.save(any(Deputi.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            deputiService.simpanDataDeputi(deputiDto);
        });
        verify(deputiRepository).save(any(Deputi.class));
    }

    @Test
    public void testCariDeputiByCode_WithSpecialCharacters() {
        // Given
        Deputi specialDeputi = Deputi.builder()
                .id(3L)
                .name("Special Deputi")
                .code("SP@#01")
                .build();

        when(deputiRepository.findByCode("SP@#01")).thenReturn(Optional.of(specialDeputi));

        // When
        DeputiDto result = deputiService.cariDeputiByCode("SP@#01");

        // Then
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Special Deputi");
        assertThat(result.getCode()).isEqualTo("SP@#01");
        verify(deputiRepository).findByCode("SP@#01");
    }

    @Test
    public void testGetUsersByDeputiId_WithDifferentDeputiId() {
        // Given
        User specialUser = User.builder()
                .id(99L)
                .name("Special User")
                .email("special@test.com")
                .build();

        List<User> userList = List.of(specialUser);
        when(userRepository.findAllUsersByDeputiId(999L)).thenReturn(userList);

        // When
        List<User> result = deputiService.getUsersByDeputiId(999L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Special User");
        assertThat(result.get(0).getEmail()).isEqualTo("special@test.com");
        verify(userRepository).findAllUsersByDeputiId(999L);
    }

    @Test
    public void testPatchDeputi_WithInvalidId() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(deputiRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deputiService.patchDeputi(999L, updates);
        });

        assertThat(exception.getMessage()).contains("Deputi not found with id: 999");
        verify(deputiRepository).findById(999L);
    }

    @Test
    public void testAmbilDaftarDeputi_WithLargeDataset() {
        // Given
        List<Deputi> largeDeputiList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            largeDeputiList.add(Deputi.builder()
                    .id((long) i)
                    .name("Deputi " + i)
                    .code("D" + String.format("%02d", i))
                    .build());
        }
        when(deputiRepository.findAll()).thenReturn(largeDeputiList);

        // When
        List<DeputiDto> result = deputiService.ambilDaftarDeputi();

        // Then
        assertThat(result).hasSize(100);
        assertThat(result.get(0).getName()).isEqualTo("Deputi 1");
        assertThat(result.get(99).getName()).isEqualTo("Deputi 100");
        verify(deputiRepository).findAll();
    }
}