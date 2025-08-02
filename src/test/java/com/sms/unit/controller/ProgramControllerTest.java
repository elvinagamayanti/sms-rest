package com.sms.unit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.ProgramController;
import com.sms.dto.ProgramDto;
import com.sms.entity.Program;
import com.sms.service.ProgramService;

@ExtendWith(MockitoExtension.class)
class ProgramControllerTest {

    @Mock
    private ProgramService programService;

    @InjectMocks
    private ProgramController programController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private ProgramDto programDto;
    private Program program;
    private Program program1;
    private Program program2;
    private List<Program> programList = new ArrayList<>();
    private List<ProgramDto> programDtoList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(programController).build();
        objectMapper = new ObjectMapper();

        // Setup test program data
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

        program1 = Program.builder()
                .id(2L)
                .name("Program Pembangunan")
                .code("PP01")
                .year("2025")
                .build();

        program2 = Program.builder()
                .id(3L)
                .name("Program Sosial")
                .code("PS01")
                .year("2024")
                .build();

        // Setup DTO list
        ProgramDto programDto1 = ProgramDto.builder()
                .id(2L)
                .name("Program Pembangunan")
                .code("PP01")
                .year("2025")
                .build();

        ProgramDto programDto2 = ProgramDto.builder()
                .id(3L)
                .name("Program Sosial")
                .code("PS01")
                .year("2024")
                .build();

        programList.add(program1);
        programList.add(program2);

        programDtoList.add(programDto1);
        programDtoList.add(programDto2);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    // ===============================================
    // Test Cases for GET /api/programs
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllPrograms_Success() throws Exception {
        // Given
        when(programService.ambilDaftarProgram()).thenReturn(programDtoList);

        // When & Then
        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Program Pembangunan"))
                .andExpect(jsonPath("$[0].code").value("PP01"))
                .andExpect(jsonPath("$[0].year").value("2025"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Program Sosial"))
                .andExpect(jsonPath("$[1].code").value("PS01"))
                .andExpect(jsonPath("$[1].year").value("2024"));

        verify(programService).ambilDaftarProgram();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllPrograms_EmptyList() throws Exception {
        // Given
        when(programService.ambilDaftarProgram()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(programService).ambilDaftarProgram();
    }

    // ===============================================
    // Test Cases for GET /api/programs/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProgramById_Success() throws Exception {
        // Given
        when(programService.cariProgramById(1L)).thenReturn(programDto);

        // When & Then
        mockMvc.perform(get("/api/programs/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Program"))
                .andExpect(jsonPath("$.code").value("TP01"))
                .andExpect(jsonPath("$.year").value("2025"));

        verify(programService).cariProgramById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProgramById_NotFound() throws Exception {
        // Given
        when(programService.cariProgramById(999L))
                .thenThrow(new RuntimeException("Program not found"));

        // When & Then
        mockMvc.perform(get("/api/programs/999"))
                .andExpect(status().is5xxServerError());

        verify(programService).cariProgramById(999L);
    }

    // ===============================================
    // Test Cases for POST /api/programs
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProgram_Success() throws Exception {
        // Given
        doNothing().when(programService).simpanDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(programDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Program"))
                .andExpect(jsonPath("$.code").value("TP01"))
                .andExpect(jsonPath("$.year").value("2025"));

        verify(programService).simpanDataProgram(any(ProgramDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProgram_InvalidData() throws Exception {
        // Given - Create program with invalid data
        ProgramDto invalidProgram = ProgramDto.builder()
                .name("") // Empty name
                .code("") // Empty code
                .year("") // Empty year
                .build();

        // When & Then
        mockMvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProgram)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for PUT /api/programs/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateProgram_Success() throws Exception {
        // Given
        doNothing().when(programService).perbaruiDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(put("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(programDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Program"))
                .andExpect(jsonPath("$.code").value("TP01"))
                .andExpect(jsonPath("$.year").value("2025"));

        verify(programService).perbaruiDataProgram(any(ProgramDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateProgram_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Program not found"))
                .when(programService).perbaruiDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(put("/api/programs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(programDto)))
                .andExpect(status().is5xxServerError());

        verify(programService).perbaruiDataProgram(any(ProgramDto.class));
    }

    // ===============================================
    // Test Cases for PATCH /api/programs/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProgram_Success() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Program Name");
        when(programService.patchProgram(eq(1L), any(Map.class))).thenReturn(programDto);

        // When & Then
        mockMvc.perform(patch("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(programService).patchProgram(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProgram_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Name");
        when(programService.patchProgram(eq(999L), any(Map.class)))
                .thenThrow(new RuntimeException("Program not found"));

        // When & Then
        mockMvc.perform(patch("/api/programs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().is5xxServerError());

        verify(programService).patchProgram(eq(999L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProgram_MultipleFields() throws Exception {
        // Given
        Map<String, Object> updates = Map.of(
                "name", "Updated Program Name",
                "code", "UPN01",
                "year", "2026");
        when(programService.patchProgram(eq(1L), any(Map.class))).thenReturn(programDto);

        // When & Then
        mockMvc.perform(patch("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(programService).patchProgram(eq(1L), any(Map.class));
    }

    // ===============================================
    // Test Cases for DELETE /api/programs/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteProgram_Success() throws Exception {
        // Given
        doNothing().when(programService).hapusDataProgram(1L);

        // When & Then
        mockMvc.perform(delete("/api/programs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Program with ID 1 deleted successfully"));

        verify(programService).hapusDataProgram(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteProgram_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Program not found"))
                .when(programService).hapusDataProgram(999L);

        // When & Then
        mockMvc.perform(delete("/api/programs/999"))
                .andExpect(status().is5xxServerError());

        verify(programService).hapusDataProgram(999L);
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProgram_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error"))
                .when(programService).simpanDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(programDto)))
                .andExpect(status().is5xxServerError());

        verify(programService).simpanDataProgram(any(ProgramDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateProgram_PathVariableBinding() throws Exception {
        // Given - Test that path variable ID is correctly set to DTO
        doNothing().when(programService).perbaruiDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(put("/api/programs/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(programDto)))
                .andExpect(status().isOk());

        // Verify service was called (ID should be set from path variable)
        verify(programService).perbaruiDataProgram(any(ProgramDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProgram_EmptyUpdates() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = Map.of();
        when(programService.patchProgram(eq(1L), any(Map.class))).thenReturn(programDto);

        // When & Then
        mockMvc.perform(patch("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(programService).patchProgram(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProgram_WithDifferentYear() throws Exception {
        // Given
        ProgramDto program2024 = ProgramDto.builder()
                .name("Program 2024")
                .code("P24")
                .year("2024")
                .build();

        doNothing().when(programService).simpanDataProgram(any(ProgramDto.class));

        // When & Then
        mockMvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(program2024)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Program 2024"))
                .andExpect(jsonPath("$.code").value("P24"))
                .andExpect(jsonPath("$.year").value("2024"));

        verify(programService).simpanDataProgram(any(ProgramDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProgramById_ValidateAllFields() throws Exception {
        // Given - Program with all fields populated
        ProgramDto fullProgram = ProgramDto.builder()
                .id(5L)
                .name("Full Program Details")
                .code("FPD01")
                .year("2025")
                .build();

        when(programService.cariProgramById(5L)).thenReturn(fullProgram);

        // When & Then
        mockMvc.perform(get("/api/programs/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Full Program Details"))
                .andExpect(jsonPath("$.code").value("FPD01"))
                .andExpect(jsonPath("$.year").value("2025"));

        verify(programService).cariProgramById(5L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProgram_SingleField() throws Exception {
        // Given - Update only one field
        Map<String, Object> updates = Map.of("year", "2026");
        when(programService.patchProgram(eq(1L), any(Map.class))).thenReturn(programDto);

        // When & Then
        mockMvc.perform(patch("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(programService).patchProgram(eq(1L), any(Map.class));
    }
}