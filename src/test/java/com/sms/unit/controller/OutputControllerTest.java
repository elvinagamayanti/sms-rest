package com.sms.unit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.AuthController;
import com.sms.controller.OutputController;
import com.sms.dto.OutputDto;
import com.sms.dto.ProgramDto;
import com.sms.dto.SimpleOutputDto;
import com.sms.entity.Program;
import com.sms.mapper.ProgramMapper;
import com.sms.service.OutputService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OutputControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OutputService outputService;

    @InjectMocks
    private OutputController outputController;

    @Autowired
    private ObjectMapper objectMapper;

    private Program program;
    private OutputDto outputDto;
    private SimpleOutputDto simpleOutputDto;
    private ProgramDto programDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(outputController).build();
        objectMapper = new ObjectMapper();

        // Setup Program DTO
        programDto = ProgramDto.builder()
                .id(1L)
                .name("Test Program")
                .code("TP01")
                .year("2025")
                .build();

        program = ProgramMapper.mapToProgram(programDto);

        // Setup Output DTO
        outputDto = OutputDto.builder()
                .id(1L)
                .name("Test Output")
                .code("TO01")
                .year("2025")
                .program(program)
                .build();

        // Setup Simple Output DTO
        simpleOutputDto = SimpleOutputDto.builder()
                .id(1L)
                .name("Test Output")
                .code("TO01")
                .year("2025")
                .build();
    }

    // ===============================================
    // Test Cases for GET /api/outputs
    // ===============================================

    @Test
    public void testGetAllOutputs_Success() throws Exception {
        // Given
        List<OutputDto> outputList = Arrays.asList(outputDto);
        when(outputService.ambilDaftarOutput()).thenReturn(outputList);

        // When & Then
        mockMvc.perform(get("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Output")))
                .andExpect(jsonPath("$[0].code", is("TO01")))
                .andExpect(jsonPath("$[0].year", is("2025")));

        verify(outputService, times(1)).ambilDaftarOutput();
    }

    @Test
    public void testGetAllOutputs_EmptyList() throws Exception {
        // Given
        when(outputService.ambilDaftarOutput()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(outputService, times(1)).ambilDaftarOutput();
    }

    // ===============================================
    // Test Cases for GET /api/outputs/{id}
    // ===============================================

    @Test
    public void testGetOutputById_Success() throws Exception {
        // Given
        when(outputService.cariOutputById(1L)).thenReturn(outputDto);

        // When & Then
        mockMvc.perform(get("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Output")))
                .andExpect(jsonPath("$.code", is("TO01")))
                .andExpect(jsonPath("$.year", is("2025")));

        verify(outputService, times(1)).cariOutputById(1L);
    }

    @Test
    public void testGetOutputById_NotFound() throws Exception {
        // Given
        doNothing().when(outputService.cariOutputById(999L));

        // When & Then
        mockMvc.perform(get("/api/outputs/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(outputService, times(1)).cariOutputById(999L);
    }

    // ===============================================
    // Test Cases for POST /api/outputs
    // ===============================================

    @Test
    public void testCreateOutput_Success() throws Exception {
        // Given
        doNothing().when(outputService).simpanDataOutput(any(OutputDto.class));

        // When & Then
        mockMvc.perform(post("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outputDto)))
                .andExpect(status().isCreated());

        verify(outputService, times(1)).simpanDataOutput(any(OutputDto.class));
    }

    @Test
    public void testCreateOutput_InvalidInput() throws Exception {
        // Given
        OutputDto invalidOutputDto = OutputDto.builder()
                .id(1L)
                .name("") // Invalid empty name
                .code("TO01")
                .year("2025")
                .program(program)
                .build();

        // When & Then
        mockMvc.perform(post("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOutputDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateOutput_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error")).when(outputService).simpanDataOutput(any(OutputDto.class));

        // When & Then
        mockMvc.perform(post("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outputDto)))
                .andExpect(status().isInternalServerError());

        verify(outputService, times(1)).simpanDataOutput(any(OutputDto.class));
    }

    // ===============================================
    // Test Cases for PUT /api/outputs/{id}
    // ===============================================

    @Test
    public void testUpdateOutput_Success() throws Exception {
        // Given
        doNothing().when(outputService).perbaruiDataOutput(any(OutputDto.class));

        // When & Then
        mockMvc.perform(put("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Output")))
                .andExpect(jsonPath("$.code", is("TO01")));

        verify(outputService, times(1)).perbaruiDataOutput(any(OutputDto.class));
    }

    @Test
    public void testUpdateOutput_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Output not found")).when(outputService).perbaruiDataOutput(any(OutputDto.class));

        // When & Then
        mockMvc.perform(put("/api/outputs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outputDto)))
                .andExpect(status().isInternalServerError());

        verify(outputService, times(1)).perbaruiDataOutput(any(OutputDto.class));
    }

    // ===============================================
    // Test Cases for PATCH /api/outputs/{id}
    // ===============================================

    @Test
    public void testPatchOutput_Success() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Output Name");
        updates.put("code", "UON01");

        when(outputService.patchOutput(anyLong(), anyMap())).thenReturn(outputDto);

        // When & Then
        mockMvc.perform(patch("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Output")))
                .andExpect(jsonPath("$.code", is("TO01")));

        verify(outputService, times(1)).patchOutput(anyLong(), anyMap());
    }

    @Test
    public void testPatchOutput_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Output Name");

        when(outputService.patchOutput(anyLong(), anyMap()))
                .thenThrow(new RuntimeException("Output not found"));

        // When & Then
        mockMvc.perform(patch("/api/outputs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isInternalServerError());

        verify(outputService, times(1)).patchOutput(anyLong(), anyMap());
    }

    @Test
    public void testPatchOutput_EmptyUpdate() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = new HashMap<>();
        when(outputService.patchOutput(anyLong(), anyMap())).thenReturn(outputDto);

        // When & Then
        mockMvc.perform(patch("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(outputService, times(1)).patchOutput(anyLong(), anyMap());
    }

    // ===============================================
    // Test Cases for DELETE /api/outputs/{id}
    // ===============================================

    @Test
    public void testDeleteOutput_Success() throws Exception {
        // Given
        doNothing().when(outputService).hapusDataOutput(1L);

        // When & Then
        mockMvc.perform(delete("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(outputService, times(1)).hapusDataOutput(1L);
    }

    @Test
    public void testDeleteOutput_NotFound() throws Exception {
        // Given
        doNothing().when(outputService).hapusDataOutput(999L);

        // When & Then
        mockMvc.perform(delete("/api/outputs/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(outputService, times(1)).hapusDataOutput(1L);
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testGetAllOutputs_MultipleOutputs() throws Exception {
        // Given
        ProgramDto program2Dto = ProgramDto.builder()
                .id(2L)
                .name("Test Program 2")
                .code("TP02")
                .year("2025")
                .build();

        Program program2 = ProgramMapper.mapToProgram(programDto);

        OutputDto output2 = OutputDto.builder()
                .id(2L)
                .name("Test Output 2")
                .code("TO02")
                .year("2025")
                .program(program2)
                .build();

        List<OutputDto> outputList = Arrays.asList(outputDto, output2);
        when(outputService.ambilDaftarOutput()).thenReturn(outputList);

        // When & Then
        mockMvc.perform(get("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Output")))
                .andExpect(jsonPath("$[1].name", is("Test Output 2")));

        verify(outputService, times(1)).ambilDaftarOutput();
    }

    @Test
    public void testCreateOutput_WithSpecialCharacters() throws Exception {
        // Given
        OutputDto specialOutputDto = OutputDto.builder()
                .id(1L)
                .name("Output with Special Characters !@#")
                .code("OSC01")
                .year("2025")
                .program(program)
                .build();

        doNothing().when(outputService).simpanDataOutput(any(OutputDto.class));

        // When & Then
        mockMvc.perform(post("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialOutputDto)))
                .andExpect(status().isCreated());

        verify(outputService, times(1)).simpanDataOutput(any(OutputDto.class));
    }

    @Test
    public void testPatchOutput_PartialUpdate() throws Exception {
        // Given
        Map<String, Object> partialUpdates = new HashMap<>();
        partialUpdates.put("name", "Partially Updated Output");

        when(outputService.patchOutput(anyLong(), anyMap())).thenReturn(outputDto);

        // When & Then
        mockMvc.perform(patch("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(outputService, times(1)).patchOutput(anyLong(), anyMap());
    }
}