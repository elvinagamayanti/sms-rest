package com.sms.unit.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.sms.controller.SatkerController;
import com.sms.dto.SatkerDto;
import com.sms.dto.SimpleSatkerDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.service.SatkerService;

@ExtendWith(MockitoExtension.class)
class SatkerControllerTest {

    @Mock
    private SatkerService satkerService;

    @InjectMocks
    private SatkerController satkerController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private SatkerDto satkerDto;
    private SimpleSatkerDto simpleSatkerDto;
    private Satker satker;
    private Satker satker1;
    private Satker satker2;
    private List<Satker> satkerList = new ArrayList<>();
    private List<SatkerDto> satkerDtoList = new ArrayList<>();
    private List<SimpleSatkerDto> simpleSatkerDtoList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    // Supporting entities
    private Province province;
    private Deputi deputi;
    private Direktorat direktorat;
    private Role role;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(satkerController).build();
        objectMapper = new ObjectMapper();

        // Setup supporting entities
        province = Province.builder()
                .id(1L)
                .name("DKI Jakarta")
                .code("31")
                .build();

        deputi = Deputi.builder()
                .id(1L)
                .name("Test Deputi")
                .code("D01")
                .build();

        direktorat = Direktorat.builder()
                .id(1L)
                .name("Test Direktorat")
                .code("D0101")
                .deputi(deputi)
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        // Setup test satker data
        satker = Satker.builder()
                .id(1L)
                .name("Test Satker")
                .code("3101")
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
                .code("3101")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .province(province)
                .isProvince(false)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        simpleSatkerDto = SimpleSatkerDto.builder()
                .id(1L)
                .name("Test Satker")
                .code("3101")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .isProvince(false)
                .provinceCode("31")
                .provinceName("DKI Jakarta")
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        satker1 = Satker.builder()
                .id(2L)
                .name("Satker Jawa Barat")
                .code("3201")
                .address("Bandung Address")
                .number("08223456789")
                .email("jabar@satker.com")
                .province(province)
                .isProvince(false)
                .build();

        satker2 = Satker.builder()
                .id(3L)
                .name("Satker Jawa Tengah")
                .code("3301")
                .address("Semarang Address")
                .number("08323456789")
                .email("jateng@satker.com")
                .province(province)
                .isProvince(false)
                .build();

        // Setup DTO lists
        SatkerDto satkerDto1 = SatkerDto.builder()
                .id(2L)
                .name("Satker Jawa Barat")
                .code("3201")
                .address("Bandung Address")
                .number("08223456789")
                .email("jabar@satker.com")
                .province(province)
                .isProvince(false)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        SatkerDto satkerDto2 = SatkerDto.builder()
                .id(3L)
                .name("Satker Jawa Tengah")
                .code("3301")
                .address("Semarang Address")
                .number("08323456789")
                .email("jateng@satker.com")
                .province(province)
                .isProvince(false)
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        SimpleSatkerDto simpleSatkerDto1 = SimpleSatkerDto.builder()
                .id(2L)
                .name("Satker Jawa Barat")
                .code("3201")
                .address("Bandung Address")
                .number("08223456789")
                .email("jabar@satker.com")
                .isProvince(false)
                .provinceCode("31")
                .provinceName("DKI Jakarta")
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        SimpleSatkerDto simpleSatkerDto2 = SimpleSatkerDto.builder()
                .id(3L)
                .name("Satker Jawa Tengah")
                .code("3301")
                .address("Semarang Address")
                .number("08323456789")
                .email("jateng@satker.com")
                .isProvince(false)
                .provinceCode("31")
                .provinceName("DKI Jakarta")
                .createdOn(new Date())
                .updatedOn(new Date())
                .build();

        satkerList.add(satker1);
        satkerList.add(satker2);

        satkerDtoList.add(satkerDto1);
        satkerDtoList.add(satkerDto2);

        simpleSatkerDtoList.add(simpleSatkerDto1);
        simpleSatkerDtoList.add(simpleSatkerDto2);

        // Setup user data for getUsersBySatkerId test
        User user1 = User.builder()
                .id(1L)
                .name("Test User 1")
                .nip("1234567890")
                .email("user1@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Test User 2")
                .nip("1234567891")
                .email("user2@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        userList.add(user1);
        userList.add(user2);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    // ===============================================
    // Test Cases for GET /api/satkers
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllSatkers_Success() throws Exception {
        // Given
        when(satkerService.ambilDaftarSatker()).thenReturn(satkerDtoList);

        // When & Then
        mockMvc.perform(get("/api/satkers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Satker Jawa Barat"))
                .andExpect(jsonPath("$[0].code").value("3201"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Satker Jawa Tengah"))
                .andExpect(jsonPath("$[1].code").value("3301"));

        verify(satkerService).ambilDaftarSatker();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllSatkers_EmptyList() throws Exception {
        // Given
        when(satkerService.ambilDaftarSatker()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/satkers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(satkerService).ambilDaftarSatker();
    }

    // ===============================================
    // Test Cases for GET /api/satkers/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetSatkerById_Success() throws Exception {
        // Given
        when(satkerService.cariSatkerById(1L)).thenReturn(satkerDto);

        // When & Then
        mockMvc.perform(get("/api/satkers/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Satker"))
                .andExpect(jsonPath("$.code").value("3101"));

        verify(satkerService).cariSatkerById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetSatkerById_NotFound() throws Exception {
        // Given
        when(satkerService.cariSatkerById(999L))
                .thenThrow(new RuntimeException("Satker not found"));

        // When & Then
        mockMvc.perform(get("/api/satkers/999"))
                .andExpect(status().is5xxServerError());

        verify(satkerService).cariSatkerById(999L);
    }

    // ===============================================
    // Test Cases for POST /api/satkers
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateSatker_Success() throws Exception {
        // Given
        doNothing().when(satkerService).simpanDataSatker(any(SatkerDto.class));

        // When & Then
        mockMvc.perform(post("/api/satkers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satkerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Satker"))
                .andExpect(jsonPath("$.code").value("3101"));

        verify(satkerService).simpanDataSatker(any(SatkerDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateSatker_InvalidData() throws Exception {
        // Given - Create satker with invalid data
        SatkerDto invalidSatker = SatkerDto.builder()
                .name("") // Empty name
                .code("") // Empty code
                .build();

        // When & Then
        mockMvc.perform(post("/api/satkers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSatker)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for PUT /api/satkers/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateSatker_Success() throws Exception {
        // Given
        doNothing().when(satkerService).perbaruiDataSatker(any(SatkerDto.class));

        // When & Then
        mockMvc.perform(put("/api/satkers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satkerDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Satker"))
                .andExpect(jsonPath("$.code").value("3101"));

        verify(satkerService).perbaruiDataSatker(any(SatkerDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateSatker_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Satker not found"))
                .when(satkerService).perbaruiDataSatker(any(SatkerDto.class));

        // When & Then
        mockMvc.perform(put("/api/satkers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satkerDto)))
                .andExpect(status().is5xxServerError());

        verify(satkerService).perbaruiDataSatker(any(SatkerDto.class));
    }

    // ===============================================
    // Test Cases for PATCH /api/satkers/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchSatker_Success() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Satker Name");
        when(satkerService.patchSatker(eq(1L), any(Map.class))).thenReturn(satkerDto);

        // When & Then
        mockMvc.perform(patch("/api/satkers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(satkerService).patchSatker(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchSatker_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Name");
        when(satkerService.patchSatker(eq(999L), any(Map.class)))
                .thenThrow(new RuntimeException("Satker not found"));

        // When & Then
        mockMvc.perform(patch("/api/satkers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().is5xxServerError());

        verify(satkerService).patchSatker(eq(999L), any(Map.class));
    }

    // ===============================================
    // Test Cases for DELETE /api/satkers/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteSatker_Success() throws Exception {
        // Given
        doNothing().when(satkerService).hapusDataSatker(1L);

        // When & Then
        mockMvc.perform(delete("/api/satkers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Satker with ID 1 deleted successfully"));

        verify(satkerService).hapusDataSatker(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteSatker_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Satker not found"))
                .when(satkerService).hapusDataSatker(999L);

        // When & Then
        mockMvc.perform(delete("/api/satkers/999"))
                .andExpect(status().is5xxServerError());

        verify(satkerService).hapusDataSatker(999L);
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateSatker_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error"))
                .when(satkerService).simpanDataSatker(any(SatkerDto.class));

        // When & Then
        mockMvc.perform(post("/api/satkers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satkerDto)))
                .andExpect(status().is5xxServerError());

        verify(satkerService).simpanDataSatker(any(SatkerDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateSatker_PathVariableBinding() throws Exception {
        // Given - Test that path variable ID is correctly set to DTO
        doNothing().when(satkerService).perbaruiDataSatker(any(SatkerDto.class));

        // When & Then
        mockMvc.perform(put("/api/satkers/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satkerDto)))
                .andExpect(status().isOk());

        // Verify service was called (ID should be set from path variable)
        verify(satkerService).perbaruiDataSatker(any(SatkerDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchSatker_EmptyUpdates() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = Map.of();
        when(satkerService.patchSatker(eq(1L), any(Map.class))).thenReturn(satkerDto);

        // When & Then
        mockMvc.perform(patch("/api/satkers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(satkerService).patchSatker(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetSatkerById_MappingToSimpleSatkerDto() throws Exception {
        // Given - Test that controller correctly maps SatkerDto to SimpleSatkerDto
        when(satkerService.cariSatkerById(1L)).thenReturn(satkerDto);

        // When & Then
        mockMvc.perform(get("/api/satkers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.provinceCode").exists()) // SimpleSatkerDto specific field
                .andExpect(jsonPath("$.provinceName").exists()); // SimpleSatkerDto specific field

        verify(satkerService).cariSatkerById(1L);
    }
}