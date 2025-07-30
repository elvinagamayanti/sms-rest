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

import com.sms.dto.ProgramDto;
import com.sms.entity.Program;
import com.sms.repository.ProgramRepository;
import com.sms.service.ProgramService;
import com.sms.service.impl.ProgramServiceImpl;

public class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;

    private ProgramService programService;

    AutoCloseable autoCloseable;
    Program program;
    ProgramDto programDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        programService = new ProgramServiceImpl(programRepository);

        // Setup test data
        program = Program.builder()
                .id(1L)
                .name("Test Program")
                .code("TP01")
                .year("2025")
                .build();

        programDto = ProgramDto.builder()
                .id(1L)
                .name("Test Program")
                .code("TP01")
                .year("2025")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for ambilDaftarProgram()
    // ===============================================

    @Test
    public void testAmbilDaftarProgram_Success() {
        // Given
        List<Program> programList = List.of(program);
        when(programRepository.findAll()).thenReturn(programList);

        // When
        List<ProgramDto> result = programService.ambilDaftarProgram();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Program");
        assertThat(result.get(0).getCode()).isEqualTo("TP01");
        assertThat(result.get(0).getYear()).isEqualTo("2025");
        verify(programRepository).findAll();
    }

    @Test
    public void testAmbilDaftarProgram_EmptyList() {
        // Given
        when(programRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ProgramDto> result = programService.ambilDaftarProgram();

        // Then
        assertThat(result).isEmpty();
        verify(programRepository).findAll();
    }

    @Test
    public void testAmbilDaftarProgram_WithMultiplePrograms() {
        // Given
        Program program2 = Program.builder()
                .id(2L)
                .name("Test Program 2")
                .code("TP02")
                .year("2025")
                .build();

        List<Program> programList = List.of(program, program2);
        when(programRepository.findAll()).thenReturn(programList);

        // When
        List<ProgramDto> result = programService.ambilDaftarProgram();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Program");
        assertThat(result.get(1).getName()).isEqualTo("Test Program 2");
        assertThat(result.get(0).getCode()).isEqualTo("TP01");
        assertThat(result.get(1).getCode()).isEqualTo("TP02");
        assertThat(result.get(0).getYear()).isEqualTo("2025");
        assertThat(result.get(1).getYear()).isEqualTo("2025");
        verify(programRepository).findAll();
    }

    // ===============================================
    // Test Cases for cariProgramById()
    // ===============================================

    @Test
    public void testCariProgramById_Found() {
        // Given
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        // When
        ProgramDto result = programService.cariProgramById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Program");
        assertThat(result.getCode()).isEqualTo("TP01");
        assertThat(result.getYear()).isEqualTo("2025");
        verify(programRepository).findById(1L);
    }

    @Test
    public void testCariProgramById_NotFound() {
        // Given
        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            programService.cariProgramById(1L);
        });
        verify(programRepository).findById(1L);
    }

    @Test
    public void testCariProgramById_WithDifferentYear() {
        // Given
        Program program2024 = Program.builder()
                .id(2L)
                .name("Program 2024")
                .code("TP24")
                .year("2024")
                .build();

        when(programRepository.findById(2L)).thenReturn(Optional.of(program2024));

        // When
        ProgramDto result = programService.cariProgramById(2L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Program 2024");
        assertThat(result.getCode()).isEqualTo("TP24");
        assertThat(result.getYear()).isEqualTo("2024");
        verify(programRepository).findById(2L);
    }

    // ===============================================
    // Test Cases for simpanDataProgram()
    // ===============================================

    @Test
    public void testSimpanDataProgram_Success() {
        // Given
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        programService.simpanDataProgram(programDto);

        // Then
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testSimpanDataProgram_WithNewProgram() {
        // Given
        ProgramDto newProgramDto = ProgramDto.builder()
                .name("New Program")
                .code("NP01")
                .year("2025")
                .build();

        Program savedProgram = Program.builder()
                .id(3L)
                .name("New Program")
                .code("NP01")
                .year("2025")
                .build();

        when(programRepository.save(any(Program.class))).thenReturn(savedProgram);

        // When
        programService.simpanDataProgram(newProgramDto);

        // Then
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testSimpanDataProgram_WithDifferentYear() {
        // Given
        ProgramDto programDto2024 = ProgramDto.builder()
                .name("Program 2024")
                .code("P24")
                .year("2024")
                .build();

        Program savedProgram = Program.builder()
                .id(4L)
                .name("Program 2024")
                .code("P24")
                .year("2024")
                .build();

        when(programRepository.save(any(Program.class))).thenReturn(savedProgram);

        // When
        programService.simpanDataProgram(programDto2024);

        // Then
        verify(programRepository).save(any(Program.class));
    }

    // ===============================================
    // Test Cases for perbaruiDataProgram()
    // ===============================================

    @Test
    public void testPerbaruiDataProgram_Success() {
        // Given
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        programService.perbaruiDataProgram(programDto);

        // Then
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPerbaruiDataProgram_WithUpdatedData() {
        // Given
        ProgramDto updatedProgramDto = ProgramDto.builder()
                .id(1L)
                .name("Updated Test Program")
                .code("UTP01")
                .year("2025")
                .build();

        Program updatedProgram = Program.builder()
                .id(1L)
                .name("Updated Test Program")
                .code("UTP01")
                .year("2025")
                .build();

        when(programRepository.save(any(Program.class))).thenReturn(updatedProgram);

        // When
        programService.perbaruiDataProgram(updatedProgramDto);

        // Then
        verify(programRepository).save(any(Program.class));
    }

    // ===============================================
    // Test Cases for hapusDataProgram()
    // ===============================================

    @Test
    public void testHapusDataProgram_Success() {
        // Given
        doNothing().when(programRepository).deleteById(1L);

        // When
        programService.hapusDataProgram(1L);

        // Then
        verify(programRepository).deleteById(1L);
    }

    @Test
    public void testHapusDataProgram_WithDifferentId() {
        // Given
        doNothing().when(programRepository).deleteById(2L);

        // When
        programService.hapusDataProgram(2L);

        // Then
        verify(programRepository).deleteById(2L);
    }

    // ===============================================
    // Test Cases for patchProgram()
    // ===============================================

    @Test
    public void testPatchProgram_Success() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Patched Program Name");
        updates.put("code", "PPT01");
        updates.put("year", "2025");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_ProgramNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Program Name");

        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            programService.patchProgram(1L, updates);
        });
        verify(programRepository).findById(1L);
    }

    @Test
    public void testPatchProgram_EmptyUpdates() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_OnlyNameUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "New Program Name");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_OnlyCodeUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code", "NTC01");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_OnlyYearUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("year", "2026");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_WithNullValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", null);
        updates.put("code", "TC01");
        updates.put("year", "2025");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_WithUnknownFields() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("unknownField", "should be ignored");
        updates.put("anotherUnknownField", 123);

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testPatchProgram_AllFieldsUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Completely Updated Program");
        updates.put("code", "CUP01");
        updates.put("year", "2026");

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        // When
        ProgramDto result = programService.patchProgram(1L, updates);

        // Then
        assertNotNull(result);
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testAmbilDaftarProgram_RepositoryException() {
        // Given
        when(programRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            programService.ambilDaftarProgram();
        });
        verify(programRepository).findAll();
    }

    @Test
    public void testSimpanDataProgram_RepositoryException() {
        // Given
        when(programRepository.save(any(Program.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            programService.simpanDataProgram(programDto);
        });
        verify(programRepository).save(any(Program.class));
    }

    @Test
    public void testCariProgramById_WithSpecialCharacters() {
        // Given
        Program specialProgram = Program.builder()
                .id(3L)
                .name("Program with Special Characters !@#")
                .code("PSC01")
                .year("2025")
                .build();

        when(programRepository.findById(3L)).thenReturn(Optional.of(specialProgram));

        // When
        ProgramDto result = programService.cariProgramById(3L);

        // Then
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Program with Special Characters !@#");
        assertThat(result.getCode()).isEqualTo("PSC01");
        assertThat(result.getYear()).isEqualTo("2025");
        verify(programRepository).findById(3L);
    }

    @Test
    public void testAmbilDaftarProgram_WithDifferentYears() {
        // Given
        Program program2024 = Program.builder()
                .id(2L)
                .name("Program 2024")
                .code("P24")
                .year("2024")
                .build();

        Program program2026 = Program.builder()
                .id(3L)
                .name("Program 2026")
                .code("P26")
                .year("2026")
                .build();

        List<Program> programList = List.of(program, program2024, program2026);
        when(programRepository.findAll()).thenReturn(programList);

        // When
        List<ProgramDto> result = programService.ambilDaftarProgram();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getYear()).isEqualTo("2025");
        assertThat(result.get(1).getYear()).isEqualTo("2024");
        assertThat(result.get(2).getYear()).isEqualTo("2026");
        verify(programRepository).findAll();
    }

    @Test
    public void testPatchProgram_WithInvalidId() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(programRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            programService.patchProgram(999L, updates);
        });

        assertThat(exception.getMessage()).contains("Program not found with id: 999");
        verify(programRepository).findById(999L);
    }
}