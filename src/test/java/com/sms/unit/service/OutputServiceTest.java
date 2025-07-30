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
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.OutputDto;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.service.OutputService;
import com.sms.service.impl.OutputServiceImpl;

public class OutputServiceTest {

    @Mock
    private OutputRepository outputRepository;

    @Mock
    private ProgramRepository programRepository;

    private OutputService outputService;

    AutoCloseable autoCloseable;
    Output output;
    OutputDto outputDto;
    Program program;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        outputService = new OutputServiceImpl(outputRepository, programRepository);

        // Setup test data
        program = Program.builder()
                .id(1L)
                .name("Test Program")
                .code("TP01")
                .year("2025")
                .build();

        output = Output.builder()
                .id(1L)
                .name("Test Output")
                .code("TO01")
                .year("2025")
                .program(program)
                .build();

        outputDto = OutputDto.builder()
                .id(1L)
                .name("Test Output")
                .code("TO01")
                .year("2025")
                .program(program)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for ambilDaftarOutput()
    // ===============================================

    @Test
    public void testAmbilDaftarOutput_Success() {
        // Given
        List<Output> outputList = List.of(output);
        when(outputRepository.findAll()).thenReturn(outputList);

        // When
        List<OutputDto> result = outputService.ambilDaftarOutput();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Output");
        assertThat(result.get(0).getCode()).isEqualTo("TO01");
        assertThat(result.get(0).getYear()).isEqualTo("2025");
        assertThat(result.get(0).getProgram().getName()).isEqualTo("Test Program");
        verify(outputRepository).findAll();
    }

    @Test
    public void testAmbilDaftarOutput_EmptyList() {
        // Given
        when(outputRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<OutputDto> result = outputService.ambilDaftarOutput();

        // Then
        assertThat(result).isEmpty();
        verify(outputRepository).findAll();
    }

    @Test
    public void testAmbilDaftarOutput_WithMultipleOutputs() {
        // Given
        Program program2 = Program.builder()
                .id(2L)
                .name("Test Program 2")
                .code("TP02")
                .year("2025")
                .build();

        Output output2 = Output.builder()
                .id(2L)
                .name("Test Output 2")
                .code("TO02")
                .year("2025")
                .program(program2)
                .build();

        List<Output> outputList = List.of(output, output2);
        when(outputRepository.findAll()).thenReturn(outputList);

        // When
        List<OutputDto> result = outputService.ambilDaftarOutput();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Output");
        assertThat(result.get(1).getName()).isEqualTo("Test Output 2");
        assertThat(result.get(0).getCode()).isEqualTo("TO01");
        assertThat(result.get(1).getCode()).isEqualTo("TO02");
        assertThat(result.get(0).getProgram().getName()).isEqualTo("Test Program");
        assertThat(result.get(1).getProgram().getName()).isEqualTo("Test Program 2");
        verify(outputRepository).findAll();
    }

    // ===============================================
    // Test Cases for cariOutputById()
    // ===============================================

    @Test
    public void testCariOutputById_Found() {
        // Given
        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));

        // When
        OutputDto result = outputService.cariOutputById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Output");
        assertThat(result.getCode()).isEqualTo("TO01");
        assertThat(result.getYear()).isEqualTo("2025");
        assertThat(result.getProgram().getName()).isEqualTo("Test Program");
        verify(outputRepository).findById(1L);
    }

    @Test
    public void testCariOutputById_NotFound() {
        // Given
        when(outputRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            outputService.cariOutputById(1L);
        });
        verify(outputRepository).findById(1L);
    }

    @Test
    public void testCariOutputById_WithDifferentProgram() {
        // Given
        Program differentProgram = Program.builder()
                .id(2L)
                .name("Different Program")
                .code("DP01")
                .year("2024")
                .build();

        Output outputWithDifferentProgram = Output.builder()
                .id(2L)
                .name("Output with Different Program")
                .code("ODP01")
                .year("2024")
                .program(differentProgram)
                .build();

        when(outputRepository.findById(2L)).thenReturn(Optional.of(outputWithDifferentProgram));

        // When
        OutputDto result = outputService.cariOutputById(2L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Output with Different Program");
        assertThat(result.getCode()).isEqualTo("ODP01");
        assertThat(result.getYear()).isEqualTo("2024");
        assertThat(result.getProgram().getName()).isEqualTo("Different Program");
        verify(outputRepository).findById(2L);
    }

    // ===============================================
    // Test Cases for simpanDataOutput()
    // ===============================================

    @Test
    public void testSimpanDataOutput_Success() {
        // Given
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        outputService.simpanDataOutput(outputDto);

        // Then
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testSimpanDataOutput_WithNewOutput() {
        // Given
        OutputDto newOutputDto = OutputDto.builder()
                .name("New Output")
                .code("NO01")
                .year("2025")
                .program(program)
                .build();

        Output savedOutput = Output.builder()
                .id(3L)
                .name("New Output")
                .code("NO01")
                .year("2025")
                .program(program)
                .build();

        when(outputRepository.save(any(Output.class))).thenReturn(savedOutput);

        // When
        outputService.simpanDataOutput(newOutputDto);

        // Then
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testSimpanDataOutput_WithDifferentYear() {
        // Given
        OutputDto outputDto2024 = OutputDto.builder()
                .name("Output 2024")
                .code("O24")
                .year("2024")
                .program(program)
                .build();

        Output savedOutput = Output.builder()
                .id(4L)
                .name("Output 2024")
                .code("O24")
                .year("2024")
                .program(program)
                .build();

        when(outputRepository.save(any(Output.class))).thenReturn(savedOutput);

        // When
        outputService.simpanDataOutput(outputDto2024);

        // Then
        verify(outputRepository).save(any(Output.class));
    }

    // ===============================================
    // Test Cases for perbaruiDataOutput()
    // ===============================================

    @Test
    public void testPerbaruiDataOutput_Success() {
        // Given
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        outputService.perbaruiDataOutput(outputDto);

        // Then
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPerbaruiDataOutput_WithUpdatedData() {
        // Given
        OutputDto updatedOutputDto = OutputDto.builder()
                .id(1L)
                .name("Updated Test Output")
                .code("UTO01")
                .year("2025")
                .program(program)
                .build();

        Output updatedOutput = Output.builder()
                .id(1L)
                .name("Updated Test Output")
                .code("UTO01")
                .year("2025")
                .program(program)
                .build();

        when(outputRepository.save(any(Output.class))).thenReturn(updatedOutput);

        // When
        outputService.perbaruiDataOutput(updatedOutputDto);

        // Then
        verify(outputRepository).save(any(Output.class));
    }

    // ===============================================
    // Test Cases for hapusDataOutput()
    // ===============================================

    @Test
    public void testHapusDataOutput_Success() {
        // Given
        doNothing().when(outputRepository).deleteById(1L);

        // When
        outputService.hapusDataOutput(1L);

        // Then
        verify(outputRepository).deleteById(1L);
    }

    @Test
    public void testHapusDataOutput_WithDifferentId() {
        // Given
        doNothing().when(outputRepository).deleteById(2L);

        // When
        outputService.hapusDataOutput(2L);

        // Then
        verify(outputRepository).deleteById(2L);
    }

    // ===============================================
    // Test Cases for patchOutput()
    // ===============================================

    @Test
    public void testPatchOutput_Success() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Patched Output Name");
        updates.put("code", "POT01");
        updates.put("year", "2025");

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_OutputNotFound() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Output Name");

        when(outputRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            outputService.patchOutput(1L, updates);
        });
        verify(outputRepository).findById(1L);
    }

    @Test
    public void testPatchOutput_EmptyUpdates() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_OnlyNameUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "New Output Name");

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_OnlyCodeUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code", "NTC01");

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_OnlyYearUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("year", "2026");

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_ProgramUpdate() {
        // Given
        Program newProgram = Program.builder()
                .id(2L)
                .name("New Program")
                .code("NP01")
                .year("2025")
                .build();

        Map<String, Object> programData = new HashMap<>();
        programData.put("id", 2L);

        Map<String, Object> updates = new HashMap<>();
        updates.put("program", programData);

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(programRepository.findById(2L)).thenReturn(Optional.of(newProgram));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(programRepository).findById(2L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_ProgramUpdateNotFound() {
        // Given
        Map<String, Object> programData = new HashMap<>();
        programData.put("id", 999L);

        Map<String, Object> updates = new HashMap<>();
        updates.put("program", programData);

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(programRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            outputService.patchOutput(1L, updates);
        });
        verify(outputRepository).findById(1L);
        verify(programRepository).findById(999L);
    }

    @Test
    public void testPatchOutput_WithNullValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", null);
        updates.put("code", "TC01");
        updates.put("year", "2025");

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_WithUnknownFields() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("unknownField", "should be ignored");
        updates.put("anotherUnknownField", 123);

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testPatchOutput_AllFieldsUpdate() {
        // Given
        Program newProgram = Program.builder()
                .id(3L)
                .name("Updated Program")
                .code("UP01")
                .year("2026")
                .build();

        Map<String, Object> programData = new HashMap<>();
        programData.put("id", 3L);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Completely Updated Output");
        updates.put("code", "CUO01");
        updates.put("year", "2026");
        updates.put("program", programData);

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(programRepository.findById(3L)).thenReturn(Optional.of(newProgram));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(programRepository).findById(3L);
        verify(outputRepository).save(any(Output.class));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testAmbilDaftarOutput_RepositoryException() {
        // Given
        when(outputRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            outputService.ambilDaftarOutput();
        });
        verify(outputRepository).findAll();
    }

    @Test
    public void testSimpanDataOutput_RepositoryException() {
        // Given
        when(outputRepository.save(any(Output.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            outputService.simpanDataOutput(outputDto);
        });
        verify(outputRepository).save(any(Output.class));
    }

    @Test
    public void testCariOutputById_WithSpecialCharacters() {
        // Given
        Output specialOutput = Output.builder()
                .id(3L)
                .name("Output with Special Characters !@#")
                .code("OSC01")
                .year("2025")
                .program(program)
                .build();

        when(outputRepository.findById(3L)).thenReturn(Optional.of(specialOutput));

        // When
        OutputDto result = outputService.cariOutputById(3L);

        // Then
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Output with Special Characters !@#");
        assertThat(result.getCode()).isEqualTo("OSC01");
        assertThat(result.getYear()).isEqualTo("2025");
        verify(outputRepository).findById(3L);
    }

    @Test
    public void testAmbilDaftarOutput_WithDifferentYears() {
        // Given
        Output output2024 = Output.builder()
                .id(2L)
                .name("Output 2024")
                .code("O24")
                .year("2024")
                .program(program)
                .build();

        Output output2026 = Output.builder()
                .id(3L)
                .name("Output 2026")
                .code("O26")
                .year("2026")
                .program(program)
                .build();

        List<Output> outputList = List.of(output, output2024, output2026);
        when(outputRepository.findAll()).thenReturn(outputList);

        // When
        List<OutputDto> result = outputService.ambilDaftarOutput();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getYear()).isEqualTo("2025");
        assertThat(result.get(1).getYear()).isEqualTo("2024");
        assertThat(result.get(2).getYear()).isEqualTo("2026");
        verify(outputRepository).findAll();
    }

    @Test
    public void testPatchOutput_WithInvalidId() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");

        when(outputRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            outputService.patchOutput(999L, updates);
        });

        assertThat(exception.getMessage()).contains("Output not found with id: 999");
        verify(outputRepository).findById(999L);
    }

    @Test
    public void testPatchOutput_WithNullProgramUpdate() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("program", null);

        when(outputRepository.findById(1L)).thenReturn(Optional.of(output));
        when(outputRepository.save(any(Output.class))).thenReturn(output);

        // When
        OutputDto result = outputService.patchOutput(1L, updates);

        // Then
        assertNotNull(result);
        verify(outputRepository).findById(1L);
        verify(outputRepository).save(any(Output.class));
        // Should not call programRepository when program is null
        Mockito.verifyNoInteractions(programRepository);
    }
}