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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
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
import com.sms.controller.ProvinceController;
import com.sms.dto.ProvinceDto;
import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.repository.SatkerRepository;
import com.sms.service.ProvinceService;

@ExtendWith(MockitoExtension.class)
class ProvinceControllerTest {

    @Mock
    private ProvinceService provinceService;

    @Mock
    private SatkerRepository satkerRepository;

    @InjectMocks
    private ProvinceController provinceController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private ProvinceDto provinceDto;
    private Province province;
    private Province province1;
    private Province province2;
    private List<Province> provinceList = new ArrayList<>();
    private List<ProvinceDto> provinceDtoList = new ArrayList<>();
    private List<Satker> satkerList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(provinceController).build();
        objectMapper = new ObjectMapper();

        ReflectionTestUtils.setField(provinceController, "satkerRepository", satkerRepository);

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

        province1 = Province.builder()
                .id(2L)
                .name("Jawa Barat")
                .code("32")
                .build();

        province2 = Province.builder()
                .id(3L)
                .name("Jawa Tengah")
                .code("33")
                .build();

        // Setup DTO list
        ProvinceDto provinceDto1 = ProvinceDto.builder()
                .id(2L)
                .name("Jawa Barat")
                .code("32")
                .build();

        ProvinceDto provinceDto2 = ProvinceDto.builder()
                .id(3L)
                .name("Jawa Tengah")
                .code("33")
                .build();

        provinceList.add(province1);
        provinceList.add(province2);

        provinceDtoList.add(provinceDto1);
        provinceDtoList.add(provinceDto2);

        // Setup satker data for province code test
        Satker satker1 = Satker.builder()
                .id(1L)
                .name("Satker Jakarta 1")
                .code("3101")
                .province(province)
                .build();

        Satker satker2 = Satker.builder()
                .id(2L)
                .name("Satker Jakarta 2")
                .code("3102")
                .province(province)
                .build();

        satkerList.add(satker1);
        satkerList.add(satker2);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    // ===============================================
    // Test Cases for GET /api/provinces
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllProvinces_Success() throws Exception {
        // Given
        when(provinceService.ambilDaftarProvinsi()).thenReturn(provinceDtoList);

        // When & Then
        mockMvc.perform(get("/api/provinces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Jawa Barat"))
                .andExpect(jsonPath("$[0].code").value("32"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Jawa Tengah"))
                .andExpect(jsonPath("$[1].code").value("33"));

        verify(provinceService).ambilDaftarProvinsi();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllProvinces_EmptyList() throws Exception {
        // Given
        when(provinceService.ambilDaftarProvinsi()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/provinces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(provinceService).ambilDaftarProvinsi();
    }

    // ===============================================
    // Test Cases for GET /api/provinces/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProvinceById_Success() throws Exception {
        // Given
        when(provinceService.cariProvinceById(1L)).thenReturn(provinceDto);

        // When & Then
        mockMvc.perform(get("/api/provinces/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("DKI Jakarta"))
                .andExpect(jsonPath("$.code").value("31"));

        verify(provinceService).cariProvinceById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProvinceById_NotFound() throws Exception {
        // Given
        when(provinceService.cariProvinceById(999L)).thenThrow(new RuntimeException("Province not found"));

        // When & Then
        mockMvc.perform(get("/api/provinces/999"))
                .andExpect(status().isNotFound());

        verify(provinceService).cariProvinceById(999L);
    }

    // ===============================================
    // Test Cases for GET /api/provinces/code/{code}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProvinceByCode_Success() throws Exception {
        // Given
        when(provinceService.cariProvinceByCode("31")).thenReturn(provinceDto);

        // When & Then
        mockMvc.perform(get("/api/provinces/code/31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("DKI Jakarta"))
                .andExpect(jsonPath("$.code").value("31"));

        verify(provinceService).cariProvinceByCode("31");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetProvinceByCode_NotFound() throws Exception {
        // Given
        when(provinceService.cariProvinceByCode("99")).thenThrow(new RuntimeException("Province not found"));

        // When & Then
        mockMvc.perform(get("/api/provinces/code/99"))
                .andExpect(status().isNotFound());

        verify(provinceService).cariProvinceByCode("99");
    }

    // ===============================================
    // Test Cases for POST /api/provinces
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProvince_Success() throws Exception {
        // Given
        doNothing().when(provinceService).simpanDataProvinsi(any(ProvinceDto.class));

        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("DKI Jakarta"))
                .andExpect(jsonPath("$.code").value("31"));

        verify(provinceService).simpanDataProvinsi(any(ProvinceDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateProvince_InvalidData() throws Exception {
        // Given - Create province with invalid data
        ProvinceDto invalidProvince = ProvinceDto.builder()
                .name("") // Empty name
                .code("") // Empty code
                .build();

        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProvince)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for PUT /api/provinces/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateProvince_Success() throws Exception {
        // Given
        doNothing().when(provinceService).perbaruiDataProvinsi(any(ProvinceDto.class));

        // When & Then
        mockMvc.perform(put("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("DKI Jakarta"))
                .andExpect(jsonPath("$.code").value("31"));

        verify(provinceService).perbaruiDataProvinsi(any(ProvinceDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateProvince_NotFound() throws Exception {
        // Given
        doNothing().when(provinceService).perbaruiDataProvinsi(any(ProvinceDto.class));

        // When & Then
        mockMvc.perform(put("/api/provinces/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isNotFound());
    }

    // ===============================================
    // Test Cases for PATCH /api/provinces/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProvince_Success() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "DKI Jakarta Updated");
        when(provinceService.patchProvince(eq(1L), any(Map.class))).thenReturn(provinceDto);

        // When & Then
        mockMvc.perform(patch("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(provinceService).patchProvince(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchProvince_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Name");
        doNothing().when(provinceService.patchProvince(eq(999L), any(Map.class)));

        // When & Then
        mockMvc.perform(patch("/api/provinces/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());

        verify(provinceService).patchProvince(eq(999L), any(Map.class));
    }

    // ===============================================
    // Test Cases for DELETE /api/provinces/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteProvince_Success() throws Exception {
        // Given
        doNothing().when(provinceService).hapusDataProvinsi(1L);

        // When & Then
        mockMvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Province with ID 1 deleted successfully"));

        verify(provinceService).hapusDataProvinsi(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteProvince_NotFound() throws Exception {
        // Given
        doNothing().when(provinceService).hapusDataProvinsi(999L);

        // When & Then
        mockMvc.perform(delete("/api/provinces/999"))
                .andExpect(status().isNotFound());

        verify(provinceService).hapusDataProvinsi(999L);
    }

    // ===============================================
    // Test Cases for GET /api/provinces/{provinceCode}/satkers
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetSatkersByProvinceCode_Success() throws Exception {
        // Given
        when(satkerRepository.findByCodeStartingWith("31")).thenReturn(satkerList);

        // When & Then
        mockMvc.perform(get("/api/provinces/31/satkers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(satkerRepository).findByCodeStartingWith("31");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetSatkersByProvinceCode_EmptyList() throws Exception {
        // Given
        when(satkerRepository.findByCodeStartingWith("99")).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/provinces/99/satkers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(satkerRepository).findByCodeStartingWith("99");
    }
}