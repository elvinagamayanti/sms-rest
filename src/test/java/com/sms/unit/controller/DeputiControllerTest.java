package com.sms.unit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.DeputiController;
import com.sms.dto.DeputiDto;
import com.sms.entity.User;
import com.sms.service.DeputiService;

/**
 * Unit Tests for DeputiController
 * 
 * @author generated
 */
// @ExtendWith(MockitoExtension.class)
public class DeputiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeputiService deputiService;

    @InjectMocks
    private DeputiController deputiController;

    private ObjectMapper objectMapper;
    private DeputiDto deputiDto;
    private User user;

    AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(deputiController).build();
        objectMapper = new ObjectMapper();

        // Setup Deputi DTO
        deputiDto = DeputiDto.builder()
                .id(1L)
                .name("Test Deputi")
                .code("TD01")
                .build();

        // Setup User - simple object without complex relationships
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .nip("1234567890")
                .isActive(true)
                .password("password123")
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for GET /api/deputis
    // ===============================================

    @Test
    public void testGetAllDeputis_Success() throws Exception {
        // Given
        List<DeputiDto> deputiList = Arrays.asList(deputiDto);
        when(deputiService.ambilDaftarDeputi()).thenReturn(deputiList);

        // When & Then
        mockMvc.perform(get("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Deputi")))
                .andExpect(jsonPath("$[0].code", is("TD01")));

        verify(deputiService, times(1)).ambilDaftarDeputi();
    }

    @Test
    public void testGetAllDeputis_EmptyList() throws Exception {
        // Given
        when(deputiService.ambilDaftarDeputi()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(deputiService, times(1)).ambilDaftarDeputi();
    }

    // ===============================================
    // Test Cases for GET /api/deputis/{id}
    // ===============================================

    @Test
    public void testGetDeputiById_Success() throws Exception {
        // Given
        when(deputiService.cariDeputiById(1L)).thenReturn(deputiDto);

        // When & Then
        mockMvc.perform(get("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deputi")))
                .andExpect(jsonPath("$.code", is("TD01")));

        verify(deputiService, times(1)).cariDeputiById(1L);
    }

    @Test
    public void testGetDeputiById_NotFound() throws Exception {
        // Given
        when(deputiService.cariDeputiById(999L)).thenThrow(new RuntimeException("Deputi not found"));

        // When & Then
        mockMvc.perform(get("/api/deputis/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).cariDeputiById(999L);
    }

    // ===============================================
    // Test Cases for GET /api/deputis/code/{code}
    // ===============================================

    @Test
    public void testGetDeputiByCode_Success() throws Exception {
        // Given
        when(deputiService.cariDeputiByCode("TD01")).thenReturn(deputiDto);

        // When & Then
        mockMvc.perform(get("/api/deputis/code/TD01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deputi")))
                .andExpect(jsonPath("$.code", is("TD01")));

        verify(deputiService, times(1)).cariDeputiByCode("TD01");
    }

    @Test
    public void testGetDeputiByCode_NotFound() throws Exception {
        // Given
        when(deputiService.cariDeputiByCode("INVALID")).thenThrow(new RuntimeException("Deputi not found"));

        // When & Then
        mockMvc.perform(get("/api/deputis/code/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).cariDeputiByCode("INVALID");
    }

    // ===============================================
    // Test Cases for POST /api/deputis
    // ===============================================

    @Test
    public void testCreateDeputi_Success() throws Exception {
        // Given
        doNothing().when(deputiService).simpanDataDeputi(any(DeputiDto.class));

        // When & Then
        mockMvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deputiDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deputi")))
                .andExpect(jsonPath("$.code", is("TD01")));

        verify(deputiService, times(1)).simpanDataDeputi(any(DeputiDto.class));
    }

    @Test
    public void testCreateDeputi_InvalidInput() throws Exception {
        // Given
        DeputiDto invalidDeputiDto = DeputiDto.builder()
                .id(1L)
                .name("") // Invalid empty name
                .code("TD01")
                .build();

        // When & Then
        mockMvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDeputiDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDeputi_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error")).when(deputiService).simpanDataDeputi(any(DeputiDto.class));

        // When & Then
        mockMvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deputiDto)))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).simpanDataDeputi(any(DeputiDto.class));
    }

    // ===============================================
    // Test Cases for PUT /api/deputis/{id}
    // ===============================================

    @Test
    public void testUpdateDeputi_Success() throws Exception {
        // Given
        doNothing().when(deputiService).perbaruiDataDeputi(any(DeputiDto.class));

        // When & Then
        mockMvc.perform(put("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deputiDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deputi")))
                .andExpect(jsonPath("$.code", is("TD01")));

        verify(deputiService, times(1)).perbaruiDataDeputi(any(DeputiDto.class));
    }

    @Test
    public void testUpdateDeputi_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Deputi not found")).when(deputiService).perbaruiDataDeputi(any(DeputiDto.class));

        // When & Then
        mockMvc.perform(put("/api/deputis/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deputiDto)))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).perbaruiDataDeputi(any(DeputiDto.class));
    }

    @Test
    public void testUpdateDeputi_InvalidInput() throws Exception {
        // Given
        DeputiDto invalidDeputiDto = DeputiDto.builder()
                .id(1L)
                .name("") // Invalid empty name
                .code("TD01")
                .build();

        // When & Then
        mockMvc.perform(put("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDeputiDto)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for DELETE /api/deputis/{id}
    // ===============================================

    @Test
    public void testDeleteDeputi_Success() throws Exception {
        // Given
        doNothing().when(deputiService).hapusDataDeputi(1L);

        // When & Then
        mockMvc.perform(delete("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Deputi with ID 1 deleted successfully")));

        verify(deputiService, times(1)).hapusDataDeputi(1L);
    }

    @Test
    public void testDeleteDeputi_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Deputi not found")).when(deputiService).hapusDataDeputi(999L);

        // When & Then
        mockMvc.perform(delete("/api/deputis/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).hapusDataDeputi(999L);
    }

    // ===============================================
    // Test Cases for GET /api/deputis/{id}/users
    // ===============================================

    @Test
    public void testGetUsersByDeputiId_Success() throws Exception {
        // Given
        List<User> userList = Arrays.asList(user);
        when(deputiService.getUsersByDeputiId(1L)).thenReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/deputis/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[0].email", is("test@email.com")))
                .andExpect(jsonPath("$[0].nip", is("1234567890")))
                .andExpect(jsonPath("$[0].isActive", is(true)));

        verify(deputiService, times(1)).getUsersByDeputiId(1L);
    }

    @Test
    public void testGetUsersByDeputiId_EmptyList() throws Exception {
        // Given
        when(deputiService.getUsersByDeputiId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/deputis/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(deputiService, times(1)).getUsersByDeputiId(1L);
    }

    @Test
    public void testGetUsersByDeputiId_DeputiNotFound() throws Exception {
        // Given
        when(deputiService.getUsersByDeputiId(999L)).thenThrow(new RuntimeException("Deputi not found"));

        // When & Then
        mockMvc.perform(get("/api/deputis/999/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).getUsersByDeputiId(999L);
    }

    // ===============================================
    // Test Cases for PATCH /api/deputis/{id}
    // ===============================================

    @Test
    public void testPatchDeputi_Success() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Deputi Name");
        updates.put("code", "UDN01");

        when(deputiService.patchDeputi(anyLong(), anyMap())).thenReturn(deputiDto);

        // When & Then
        mockMvc.perform(patch("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Deputi")))
                .andExpect(jsonPath("$.code", is("TD01")));

        verify(deputiService, times(1)).patchDeputi(anyLong(), anyMap());
    }

    @Test
    public void testPatchDeputi_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Deputi Name");

        when(deputiService.patchDeputi(anyLong(), anyMap()))
                .thenThrow(new RuntimeException("Deputi not found"));

        // When & Then
        mockMvc.perform(patch("/api/deputis/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isInternalServerError());

        verify(deputiService, times(1)).patchDeputi(anyLong(), anyMap());
    }

    @Test
    public void testPatchDeputi_EmptyUpdate() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = new HashMap<>();
        when(deputiService.patchDeputi(anyLong(), anyMap())).thenReturn(deputiDto);

        // When & Then
        mockMvc.perform(patch("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(deputiService, times(1)).patchDeputi(anyLong(), anyMap());
    }

    @Test
    public void testPatchDeputi_PartialUpdate() throws Exception {
        // Given
        Map<String, Object> partialUpdates = new HashMap<>();
        partialUpdates.put("name", "Partially Updated Deputi");

        when(deputiService.patchDeputi(anyLong(), anyMap())).thenReturn(deputiDto);

        // When & Then
        mockMvc.perform(patch("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(deputiService, times(1)).patchDeputi(anyLong(), anyMap());
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testGetAllDeputis_MultipleDeputis() throws Exception {
        // Given
        DeputiDto deputi2 = DeputiDto.builder()
                .id(2L)
                .name("Test Deputi 2")
                .code("TD02")
                .build();

        List<DeputiDto> deputiList = Arrays.asList(deputiDto, deputi2);
        when(deputiService.ambilDaftarDeputi()).thenReturn(deputiList);

        // When & Then
        mockMvc.perform(get("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Deputi")))
                .andExpect(jsonPath("$[1].name", is("Test Deputi 2")));

        verify(deputiService, times(1)).ambilDaftarDeputi();
    }

    @Test
    public void testCreateDeputi_WithSpecialCharacters() throws Exception {
        // Given
        DeputiDto specialDeputiDto = DeputiDto.builder()
                .id(1L)
                .name("Deputi with Special Characters !@#")
                .code("DSC01")
                .build();

        doNothing().when(deputiService).simpanDataDeputi(any(DeputiDto.class));

        // When & Then
        mockMvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialDeputiDto)))
                .andExpect(status().isCreated());

        verify(deputiService, times(1)).simpanDataDeputi(any(DeputiDto.class));
    }

    @Test
    public void testGetUsersByDeputiId_MultipleUsers() throws Exception {
        // Given
        User user2 = User.builder()
                .id(2L)
                .name("Test User 2")
                .email("test2@email.com")
                .nip("1234567891")
                .isActive(true)
                .password("password456")
                .build();

        List<User> userList = Arrays.asList(user, user2);
        when(deputiService.getUsersByDeputiId(1L)).thenReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/deputis/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[1].name", is("Test User 2")));

        verify(deputiService, times(1)).getUsersByDeputiId(1L);
    }
}