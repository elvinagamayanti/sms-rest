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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.DirektoratController;
import com.sms.dto.DeputiDto;
import com.sms.dto.DirektoratDto;
import com.sms.entity.Deputi;
import com.sms.entity.User;
import com.sms.mapper.DeputiMapper;
import com.sms.service.DirektoratService;

// @ExtendWith(MockitoExtension.class)
public class DirektoratControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DirektoratService direktoratService;

    @InjectMocks
    private DirektoratController direktoratController;

    private ObjectMapper objectMapper;
    private DirektoratDto direktoratDto;
    private DeputiDto deputiDto;
    private Deputi deputi;
    private User user;

    AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(direktoratController).build();
        objectMapper = new ObjectMapper();

        // Setup Deputi DTO
        deputiDto = DeputiDto.builder()
                .id(1L)
                .name("Test Deputi")
                .code("TD01")
                .build();

        deputi = DeputiMapper.mapToDeputi(deputiDto);

        // Setup Direktorat DTO
        direktoratDto = DirektoratDto.builder()
                .id(1L)
                .name("Test Direktorat")
                .code("TDR01")
                .deputi(deputi)
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
    // Test Cases for GET /api/direktorats
    // ===============================================

    @Test
    public void testGetAllDirektorats_Success() throws Exception {
        // Given
        List<DirektoratDto> direktoratList = Arrays.asList(direktoratDto);
        when(direktoratService.ambilDaftarDirektorat()).thenReturn(direktoratList);

        // When & Then
        mockMvc.perform(get("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Direktorat")))
                .andExpect(jsonPath("$[0].code", is("TDR01")));

        verify(direktoratService, times(1)).ambilDaftarDirektorat();
    }

    @Test
    public void testGetAllDirektorats_EmptyList() throws Exception {
        // Given
        when(direktoratService.ambilDaftarDirektorat()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(direktoratService, times(1)).ambilDaftarDirektorat();
    }

    @Test
    public void testGetAllDirektorats_ServiceException() throws Exception {
        // Given
        when(direktoratService.ambilDaftarDirektorat()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).ambilDaftarDirektorat();
    }

    // ===============================================
    // Test Cases for GET /api/direktorats/{id}
    // ===============================================

    @Test
    public void testGetDirektoratById_Success() throws Exception {
        // Given
        when(direktoratService.cariDirektoratById(1L)).thenReturn(direktoratDto);

        // When & Then
        mockMvc.perform(get("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Direktorat")))
                .andExpect(jsonPath("$.code", is("TDR01")));

        verify(direktoratService, times(1)).cariDirektoratById(1L);
    }

    @Test
    public void testGetDirektoratById_NotFound() throws Exception {
        // Given
        when(direktoratService.cariDirektoratById(999L)).thenThrow(new RuntimeException("Direktorat not found"));

        // When & Then
        mockMvc.perform(get("/api/direktorats/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).cariDirektoratById(999L);
    }

    // ===============================================
    // Test Cases for GET /api/direktorats/code/{code}
    // ===============================================

    @Test
    public void testGetDirektoratByCode_Success() throws Exception {
        // Given
        when(direktoratService.cariDirektoratByCode("TDR01")).thenReturn(direktoratDto);

        // When & Then
        mockMvc.perform(get("/api/direktorats/code/TDR01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Direktorat")))
                .andExpect(jsonPath("$.code", is("TDR01")));

        verify(direktoratService, times(1)).cariDirektoratByCode("TDR01");
    }

    @Test
    public void testGetDirektoratByCode_NotFound() throws Exception {
        // Given
        when(direktoratService.cariDirektoratByCode("INVALID")).thenThrow(new RuntimeException("Direktorat not found"));

        // When & Then
        mockMvc.perform(get("/api/direktorats/code/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).cariDirektoratByCode("INVALID");
    }

    // ===============================================
    // Test Cases for POST /api/direktorats
    // ===============================================

    @Test
    public void testCreateDirektorat_Success() throws Exception {
        // Given
        doNothing().when(direktoratService).simpanDataDirektorat(any(DirektoratDto.class));

        // When & Then
        mockMvc.perform(post("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(direktoratDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Direktorat")))
                .andExpect(jsonPath("$.code", is("TDR01")));

        verify(direktoratService, times(1)).simpanDataDirektorat(any(DirektoratDto.class));
    }

    @Test
    public void testCreateDirektorat_InvalidInput() throws Exception {
        // Given
        DirektoratDto invalidDirektoratDto = DirektoratDto.builder()
                .id(1L)
                .name("") // Invalid empty name
                .code("TDR01")
                .deputi(deputi)
                .build();

        // When & Then
        mockMvc.perform(post("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDirektoratDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDirektorat_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error")).when(direktoratService)
                .simpanDataDirektorat(any(DirektoratDto.class));

        // When & Then
        mockMvc.perform(post("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(direktoratDto)))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).simpanDataDirektorat(any(DirektoratDto.class));
    }

    // ===============================================
    // Test Cases for PUT /api/direktorats/{id}
    // ===============================================

    @Test
    public void testUpdateDirektorat_Success() throws Exception {
        // Given
        doNothing().when(direktoratService).perbaruiDataDirektorat(any(DirektoratDto.class));

        // When & Then
        mockMvc.perform(put("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(direktoratDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Direktorat")))
                .andExpect(jsonPath("$.code", is("TDR01")));

        verify(direktoratService, times(1)).perbaruiDataDirektorat(any(DirektoratDto.class));
    }

    @Test
    public void testUpdateDirektorat_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Direktorat not found")).when(direktoratService)
                .perbaruiDataDirektorat(any(DirektoratDto.class));

        // When & Then
        mockMvc.perform(put("/api/direktorats/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(direktoratDto)))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).perbaruiDataDirektorat(any(DirektoratDto.class));
    }

    @Test
    public void testUpdateDirektorat_InvalidInput() throws Exception {
        // Given
        DirektoratDto invalidDirektoratDto = DirektoratDto.builder()
                .id(1L)
                .name("") // Invalid empty name
                .code("TDR01")
                .deputi(deputi)
                .build();

        // When & Then
        mockMvc.perform(put("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDirektoratDto)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for DELETE /api/direktorats/{id}
    // ===============================================

    @Test
    public void testDeleteDirektorat_Success() throws Exception {
        // Given
        doNothing().when(direktoratService).hapusDataDirektorat(1L);

        // When & Then
        mockMvc.perform(delete("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Direktorat with ID 1 deleted successfully")));

        verify(direktoratService, times(1)).hapusDataDirektorat(1L);
    }

    @Test
    public void testDeleteDirektorat_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Direktorat not found")).when(direktoratService).hapusDataDirektorat(999L);

        // When & Then
        mockMvc.perform(delete("/api/direktorats/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).hapusDataDirektorat(999L);
    }

    // ===============================================
    // Test Cases for GET /api/direktorats/deputi/{deputiId}
    // ===============================================

    @Test
    public void testGetDirektoratsByDeputiId_Success() throws Exception {
        // Given
        List<DirektoratDto> direktoratList = Arrays.asList(direktoratDto);
        when(direktoratService.getDirektoratsByDeputiId(1L)).thenReturn(direktoratList);

        // When & Then
        mockMvc.perform(get("/api/direktorats/deputi/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Direktorat")))
                .andExpect(jsonPath("$[0].code", is("TDR01")));

        verify(direktoratService, times(1)).getDirektoratsByDeputiId(1L);
    }

    @Test
    public void testGetDirektoratsByDeputiId_EmptyList() throws Exception {
        // Given
        when(direktoratService.getDirektoratsByDeputiId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/direktorats/deputi/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(direktoratService, times(1)).getDirektoratsByDeputiId(1L);
    }

    @Test
    public void testGetDirektoratsByDeputiId_DeputiNotFound() throws Exception {
        // Given
        when(direktoratService.getDirektoratsByDeputiId(999L)).thenThrow(new RuntimeException("Deputi not found"));

        // When & Then
        mockMvc.perform(get("/api/direktorats/deputi/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).getDirektoratsByDeputiId(999L);
    }

    // ===============================================
    // Test Cases for GET /api/direktorats/{id}/users
    // ===============================================

    @Test
    public void testGetUsersByDirektoratId_Success() throws Exception {
        // Given
        List<User> userList = Arrays.asList(user);
        when(direktoratService.getUsersByDirektoratId(1L)).thenReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/direktorats/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[0].email", is("test@email.com")))
                .andExpect(jsonPath("$[0].nip", is("1234567890")))
                .andExpect(jsonPath("$[0].isActive", is(true)));

        verify(direktoratService, times(1)).getUsersByDirektoratId(1L);
    }

    @Test
    public void testGetUsersByDirektoratId_EmptyList() throws Exception {
        // Given
        when(direktoratService.getUsersByDirektoratId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/direktorats/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(direktoratService, times(1)).getUsersByDirektoratId(1L);
    }

    @Test
    public void testGetUsersByDirektoratId_DirektoratNotFound() throws Exception {
        // Given
        when(direktoratService.getUsersByDirektoratId(999L)).thenThrow(new RuntimeException("Direktorat not found"));

        // When & Then
        mockMvc.perform(get("/api/direktorats/999/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).getUsersByDirektoratId(999L);
    }

    // ===============================================
    // Test Cases for PATCH /api/direktorats/{id}
    // ===============================================

    @Test
    public void testPatchDirektorat_Success() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Direktorat Name");
        updates.put("code", "UDN01");

        when(direktoratService.patchDirektorat(anyLong(), anyMap())).thenReturn(direktoratDto);

        // When & Then
        mockMvc.perform(patch("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Direktorat")))
                .andExpect(jsonPath("$.code", is("TDR01")));

        verify(direktoratService, times(1)).patchDirektorat(anyLong(), anyMap());
    }

    @Test
    public void testPatchDirektorat_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Direktorat Name");

        when(direktoratService.patchDirektorat(anyLong(), anyMap()))
                .thenThrow(new RuntimeException("Direktorat not found"));

        // When & Then
        mockMvc.perform(patch("/api/direktorats/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isInternalServerError());

        verify(direktoratService, times(1)).patchDirektorat(anyLong(), anyMap());
    }

    @Test
    public void testPatchDirektorat_EmptyUpdate() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = new HashMap<>();
        when(direktoratService.patchDirektorat(anyLong(), anyMap())).thenReturn(direktoratDto);

        // When & Then
        mockMvc.perform(patch("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(direktoratService, times(1)).patchDirektorat(anyLong(), anyMap());
    }

    @Test
    public void testPatchDirektorat_PartialUpdate() throws Exception {
        // Given
        Map<String, Object> partialUpdates = new HashMap<>();
        partialUpdates.put("name", "Partially Updated Direktorat");

        when(direktoratService.patchDirektorat(anyLong(), anyMap())).thenReturn(direktoratDto);

        // When & Then
        mockMvc.perform(patch("/api/direktorats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(direktoratService, times(1)).patchDirektorat(anyLong(), anyMap());
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testGetAllDirektorats_MultipleDirektorats() throws Exception {
        // Given
        DeputiDto deputi2dto = DeputiDto.builder()
                .id(2L)
                .name("Test Deputi 2")
                .code("TD02")
                .build();

        Deputi deputi2 = DeputiMapper.mapToDeputi(deputi2dto);

        DirektoratDto direktorat2 = DirektoratDto.builder()
                .id(2L)
                .name("Test Direktorat 2")
                .code("TDR02")
                .deputi(deputi2)
                .build();

        List<DirektoratDto> direktoratList = Arrays.asList(direktoratDto, direktorat2);
        when(direktoratService.ambilDaftarDirektorat()).thenReturn(direktoratList);

        // When & Then
        mockMvc.perform(get("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Direktorat")))
                .andExpect(jsonPath("$[1].name", is("Test Direktorat 2")));

        verify(direktoratService, times(1)).ambilDaftarDirektorat();
    }

    @Test
    public void testCreateDirektorat_WithSpecialCharacters() throws Exception {
        // Given

        DirektoratDto specialDirektoratDto = DirektoratDto.builder()
                .id(1L)
                .name("Direktorat with Special Characters !@#")
                .code("DSC01")
                .deputi(deputi)
                .build();

        doNothing().when(direktoratService).simpanDataDirektorat(any(DirektoratDto.class));

        // When & Then
        mockMvc.perform(post("/api/direktorats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialDirektoratDto)))
                .andExpect(status().isCreated());

        verify(direktoratService, times(1)).simpanDataDirektorat(any(DirektoratDto.class));
    }

    @Test
    public void testGetUsersByDirektoratId_MultipleUsers() throws Exception {
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
        when(direktoratService.getUsersByDirektoratId(1L)).thenReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/direktorats/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[1].name", is("Test User 2")));

        verify(direktoratService, times(1)).getUsersByDirektoratId(1L);
    }

    @Test
    public void testGetDirektoratsByDeputiId_MultipleDirektorats() throws Exception {
        // Given
        DirektoratDto direktorat2 = DirektoratDto.builder()
                .id(2L)
                .name("Test Direktorat 2")
                .code("TDR02")
                .deputi(deputi)
                .build();

        List<DirektoratDto> direktoratList = Arrays.asList(direktoratDto, direktorat2);
        when(direktoratService.getDirektoratsByDeputiId(1L)).thenReturn(direktoratList);

        // When & Then
        mockMvc.perform(get("/api/direktorats/deputi/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Direktorat")))
                .andExpect(jsonPath("$[1].name", is("Test Direktorat 2")));

        verify(direktoratService, times(1)).getDirektoratsByDeputiId(1L);
    }
}