package com.sms.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.KegiatanController;
import com.sms.dto.KegiatanDto;
import com.sms.dto.ProgramDto;
import com.sms.dto.SimpleKegiatanDto;
import com.sms.entity.Program;
import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.mapper.ProgramMapper;
import com.sms.service.KegiatanService;
import com.sms.service.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KegiatanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private KegiatanController kegiatanController;

    @Mock
    private KegiatanService kegiatanService;

    @Autowired
    private ObjectMapper objectMapper;

    private KegiatanDto kegiatanDto;
    private List<KegiatanDto> kegiatanList;

    private ProgramDto programDto;
    private Program program;
    private Province province;
    private Satker satker;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(kegiatanController).build();
        objectMapper = new ObjectMapper();

        // Setup Program DTO
        programDto = ProgramDto.builder()
                .id(1L)
                .name("Test Program")
                .code("TP01")
                .year("2025")
                .build();

        program = ProgramMapper.mapToProgram(programDto);

        province = Province.builder()
                .id(1L)
                .name("DKI Jakarta")
                .code("31")
                .build();

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

        kegiatanDto = KegiatanDto.builder()
                .id(1L)
                .name("Kegiatan Test")
                .code("KGT001")
                .startDate(new Date())
                .endDate(new Date())
                .budget(new BigDecimal("1000000"))
                .program(program)
                .satker(satker)
                .build();

        kegiatanList = Arrays.asList(
                kegiatanDto,
                KegiatanDto.builder()
                        .id(2L)
                        .name("Kegiatan Test 2")
                        .code("KGT002")
                        .startDate(new Date())
                        .endDate(new Date())
                        .budget(new BigDecimal("2000000"))
                        .program(program)
                        .satker(satker)
                        .build());
    }

    // ===============================================
    // GET All Kegiatans Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllKegiatans_Success() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered()).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Kegiatan Test"))
                .andExpect(jsonPath("$[0].code").value("KGT001"));

        verify(kegiatanService).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "ADMIN_PUSAT")
    void testGetAllKegiatans_AdminPusat_Success() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered()).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(kegiatanService).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "OPERATOR_SATKER")
    void testGetAllKegiatans_OperatorSatker_Success() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered()).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isOk());

        verify(kegiatanService).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllKegiatans_InsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isForbidden());

        verify(kegiatanService, never()).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllKegiatans_SecurityException() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered())
                .thenThrow(new SecurityException("Tidak memiliki akses"));

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Tidak memiliki akses untuk melihat kegiatan"));

        verify(kegiatanService).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllKegiatans_GeneralException() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan sistem"));

        verify(kegiatanService).findAllKegiatanFiltered();
    }

    // ===============================================
    // GET Kegiatan by ID Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanById_Success() throws Exception {
        // Given
        when(kegiatanService.canAccessKegiatan(1L)).thenReturn(true);
        when(kegiatanService.cariKegiatanById(1L)).thenReturn(kegiatanDto);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kegiatan Test"))
                .andExpect(jsonPath("$.code").value("KGT001"));

        verify(kegiatanService).canAccessKegiatan(1L);
        verify(kegiatanService).cariKegiatanById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN_SATKER")
    void testGetKegiatanById_NoAccess() throws Exception {
        // Given
        when(kegiatanService.canAccessKegiatan(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Tidak memiliki akses untuk melihat kegiatan ini"));

        verify(kegiatanService).canAccessKegiatan(1L);
        verify(kegiatanService, never()).cariKegiatanById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanById_NotFound() throws Exception {
        // Given
        when(kegiatanService.canAccessKegiatan(999L)).thenReturn(true);
        when(kegiatanService.cariKegiatanById(999L))
                .thenThrow(new RuntimeException("Kegiatan not found"));

        // When & Then
        mockMvc.perform(get("/api/kegiatans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Kegiatan tidak ditemukan"));

        verify(kegiatanService).canAccessKegiatan(999L);
        verify(kegiatanService).cariKegiatanById(999L);
    }
    // ===============================================
    // GET Kegiatan Detail by ID Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanDetailById_Success() throws Exception {
        // Given
        when(kegiatanService.canAccessKegiatan(1L)).thenReturn(true);
        when(kegiatanService.cariKegiatanById(1L)).thenReturn(kegiatanDto);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/1/detail"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kegiatan Test"))
                .andExpect(jsonPath("$.description").value("Deskripsi kegiatan test"))
                .andExpect(jsonPath("$.budget").value(1000000));

        verify(kegiatanService).canAccessKegiatan(1L);
        verify(kegiatanService).cariKegiatanById(1L);
    }

    @Test
    @WithMockUser(roles = "OPERATOR_PROVINSI")
    void testGetKegiatanDetailById_NoAccess() throws Exception {
        // Given
        when(kegiatanService.canAccessKegiatan(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/1/detail"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Tidak memiliki akses untuk melihat detail kegiatan ini"));

        verify(kegiatanService).canAccessKegiatan(1L);
        verify(kegiatanService, never()).cariKegiatanById(1L);
    }

    // ===============================================
    // CREATE Kegiatan Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateKegiatan_Success() throws Exception {
        // Given
        doNothing().when(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));

        // When & Then
        mockMvc.perform(post("/api/kegiatans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kegiatanDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Kegiatan berhasil dibuat"));

        verify(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_PUSAT")
    void testCreateKegiatan_AdminPusat_Success() throws Exception {
        // Given
        doNothing().when(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));

        // When & Then
        mockMvc.perform(post("/api/kegiatans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kegiatanDto)))
                .andExpect(status().isCreated());

        verify(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_SATKER")
    void testCreateKegiatan_InsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/kegiatans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kegiatanDto)))
                .andExpect(status().isForbidden());

        verify(kegiatanService, never()).simpanDataKegiatan(any(KegiatanDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateKegiatan_ValidationError() throws Exception {
        // Given - Invalid data
        KegiatanDto invalidKegiatan = KegiatanDto.builder().build(); // Empty kegiatan

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));

        // When & Then
        mockMvc.perform(post("/api/kegiatans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidKegiatan)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data tidak valid"));

        verify(kegiatanService).simpanDataKegiatan(any(KegiatanDto.class));
    }

    // ===============================================
    // UPDATE Kegiatan Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateKegiatan_Success() throws Exception {
        // Given
        doNothing().when(kegiatanService).perbaruiDataKegiatan(any(KegiatanDto.class));

        // When & Then
        mockMvc.perform(put("/api/kegiatans/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kegiatanDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Kegiatan berhasil diperbarui"));

        verify(kegiatanService).perbaruiDataKegiatan(any(KegiatanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN_PUSAT")
    void testUpdateKegiatan_AdminPusat_Success() throws Exception {
        // Given
        doNothing().when(kegiatanService).perbaruiDataKegiatan(any(KegiatanDto.class));

        // When & Then
        mockMvc.perform(put("/api/kegiatans/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kegiatanDto)))
                .andExpect(status().isOk());

        verify(kegiatanService).perbaruiDataKegiatan(any(KegiatanDto.class));
    }

    // ===============================================
    // PATCH Kegiatan Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchKegiatan_Success() throws Exception {
        // Given
        Map<String, Object> updates = Map.of(
                "name", "Updated Kegiatan Name",
                "status", "INACTIVE");
        when(kegiatanService.patchKegiatan(eq(1L), any(Map.class))).thenReturn(kegiatanDto);

        // When & Then
        mockMvc.perform(patch("/api/kegiatans/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(kegiatanService).patchKegiatan(eq(1L), any(Map.class));
    }

    // ===============================================
    // DELETE Kegiatan Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteKegiatan_Success() throws Exception {
        // Given
        doNothing().when(kegiatanService).hapusDataKegiatan(1L);
        ;

        // When & Then
        mockMvc.perform(delete("/api/kegiatans/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(kegiatanService).hapusDataKegiatan(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN_PUSAT")
    void testDeleteKegiatan_InsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/kegiatans/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(kegiatanService, never()).hapusDataKegiatan(anyLong());
    }

    // ===============================================
    // GET Kegiatan by Deputi PJ Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanByDeputiPJ_Success() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJ(1L)).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(kegiatanService).getKegiatanByDeputiPJ(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN_PROVINSI")
    void testGetKegiatanByDeputiPJ_AdminProvinsi_Success() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJ(1L)).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/1"))
                .andExpect(status().isOk());

        verify(kegiatanService).getKegiatanByDeputiPJ(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanByDeputiPJ_Exception() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJ(1L))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Gagal mengambil data kegiatan"));

        verify(kegiatanService).getKegiatanByDeputiPJ(1L);
    }

    // ===============================================
    // GET Kegiatan by Deputi PJ Code Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanByDeputiPJCode_Success() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJCode("DEP001")).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/code/DEP001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(kegiatanService).getKegiatanByDeputiPJCode("DEP001");
    }

    @Test
    @WithMockUser(roles = "OPERATOR_PUSAT")
    void testGetKegiatanByDeputiPJCode_OperatorPusat_Success() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJCode("DEP001")).thenReturn(kegiatanList);

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/code/DEP001"))
                .andExpect(status().isOk());

        verify(kegiatanService).getKegiatanByDeputiPJCode("DEP001");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanByDeputiPJCode_Exception() throws Exception {
        // Given
        when(kegiatanService.getKegiatanByDeputiPJCode("INVALID"))
                .thenThrow(new RuntimeException("Invalid code"));

        // When & Then
        mockMvc.perform(get("/api/kegiatans/deputi/code/INVALID"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Gagal mengambil data kegiatan"));

        verify(kegiatanService).getKegiatanByDeputiPJCode("INVALID");
    }

    // ===============================================
    // Authorization Tests for Different Roles
    // ===============================================

    @Test
    void testUnauthorizedAccess() throws Exception {
        // When & Then - No authentication
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isUnauthorized());

        verify(kegiatanService, never()).findAllKegiatanFiltered();
    }

    @Test
    @WithMockUser(roles = "INVALID_ROLE")
    void testInvalidRoleAccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isForbidden());

        verify(kegiatanService, never()).findAllKegiatanFiltered();
    }

    // ===============================================
    // Edge Cases Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetKegiatanById_PathVariableEdgeCases() throws Exception {
        // Test with 0 ID
        when(kegiatanService.canAccessKegiatan(0L)).thenReturn(true);
        when(kegiatanService.cariKegiatanById(0L))
                .thenThrow(new RuntimeException("Invalid ID"));

        mockMvc.perform(get("/api/kegiatans/0"))
                .andExpect(status().isNotFound());

        verify(kegiatanService).canAccessKegiatan(0L);
        verify(kegiatanService).cariKegiatanById(0L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateKegiatan_NullValidation() throws Exception {
        // When & Then - Send null body
        mockMvc.perform(post("/api/kegiatans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());

        verify(kegiatanService, never()).simpanDataKegiatan(any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllKegiatans_EmptyResult() throws Exception {
        // Given
        when(kegiatanService.findAllKegiatanFiltered()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/kegiatans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(kegiatanService).findAllKegiatanFiltered();
    }
}