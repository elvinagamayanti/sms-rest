package com.sms.unit.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.sms.controller.RoleController;
import com.sms.dto.RoleDto;
import com.sms.dto.SimpleUserDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.service.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private RoleDto roleDto;
    private Role role;
    private Role role1;
    private Role role2;
    private List<Role> roleList = new ArrayList<>();
    private List<RoleDto> roleDtoList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<SimpleUserDto> simpleUserDtoList = new ArrayList<>();

    // Supporting entities for User setup
    private Province province;
    private Satker satker;
    private Deputi deputi;
    private Direktorat direktorat;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
        objectMapper = new ObjectMapper();

        // Setup supporting entities
        province = Province.builder()
                .id(1L)
                .name("Test Province")
                .code("01")
                .build();

        satker = Satker.builder()
                .id(1L)
                .name("Test Satker")
                .code("0100")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .province(province)
                .isProvince(false)
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

        // Setup test role data
        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        roleDto = RoleDto.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        role1 = Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();

        role2 = Role.builder()
                .id(3L)
                .name("ROLE_SUPERADMIN")
                .build();

        // Setup DTO list
        RoleDto roleDto1 = RoleDto.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();

        RoleDto roleDto2 = RoleDto.builder()
                .id(3L)
                .name("ROLE_SUPERADMIN")
                .build();

        roleList.add(role1);
        roleList.add(role2);

        roleDtoList.add(roleDto1);
        roleDtoList.add(roleDto2);

        // Setup user data for getUsersByRoleId test
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

        // Setup SimpleUserDto list
        SimpleUserDto simpleUserDto1 = SimpleUserDto.builder()
                .id(1L)
                .name("Test User 1")
                .nip("1234567890")
                .email("user1@email.com")
                .satkerName(satker.getName())
                .direktoratName(direktorat.getName())
                .build();

        SimpleUserDto simpleUserDto2 = SimpleUserDto.builder()
                .id(2L)
                .name("Test User 2")
                .nip("1234567891")
                .email("user2@email.com")
                .satkerName(satker.getName())
                .direktoratName(direktorat.getName())
                .build();

        simpleUserDtoList.add(simpleUserDto1);
        simpleUserDtoList.add(simpleUserDto2);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    // ===============================================
    // Test Cases for GET /api/roles
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllRoles_Success() throws Exception {
        // Given
        when(roleService.ambilDaftarRole()).thenReturn(roleDtoList);

        // When & Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("ROLE_SUPERADMIN"));

        verify(roleService).ambilDaftarRole();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetAllRoles_EmptyList() throws Exception {
        // Given
        when(roleService.ambilDaftarRole()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(roleService).ambilDaftarRole();
    }

    // ===============================================
    // Test Cases for GET /api/roles/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetRoleById_Success() throws Exception {
        // Given
        when(roleService.cariRoleById(1L)).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(get("/api/roles/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ROLE_USER"));

        verify(roleService).cariRoleById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetRoleById_NotFound() throws Exception {
        // Given
        when(roleService.cariRoleById(999L))
                .thenThrow(new RuntimeException("Role not found"));

        // When & Then
        mockMvc.perform(get("/api/roles/999"))
                .andExpect(status().is5xxServerError());

        verify(roleService).cariRoleById(999L);
    }

    // ===============================================
    // Test Cases for POST /api/roles
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateRole_Success() throws Exception {
        // Given
        when(roleService.simpanDataRole(any(RoleDto.class))).thenReturn("Success");

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ROLE_USER"));

        verify(roleService).simpanDataRole(any(RoleDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateRole_InvalidData() throws Exception {
        // Given - Create role with invalid data
        RoleDto invalidRole = RoleDto.builder()
                .name("") // Empty name
                .build();

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRole)))
                .andExpect(status().isBadRequest());
    }

    // ===============================================
    // Test Cases for PUT /api/roles/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateRole_Success() throws Exception {
        // Given
        when(roleService.perbaruiDataRole(any(RoleDto.class))).thenReturn("Success");

        // When & Then
        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ROLE_USER"));

        verify(roleService).perbaruiDataRole(any(RoleDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateRole_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Role not found"))
                .when(roleService).perbaruiDataRole(any(RoleDto.class));

        // When & Then
        mockMvc.perform(put("/api/roles/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().is5xxServerError());

        verify(roleService).perbaruiDataRole(any(RoleDto.class));
    }

    // ===============================================
    // Test Cases for PATCH /api/roles/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchRole_Success() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "ROLE_UPDATED");
        when(roleService.patchRole(eq(1L), any(Map.class))).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(patch("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(roleService).patchRole(eq(1L), any(Map.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchRole_NotFound() throws Exception {
        // Given
        Map<String, Object> updates = Map.of("name", "Updated Name");
        when(roleService.patchRole(eq(999L), any(Map.class)))
                .thenThrow(new RuntimeException("Role not found"));

        // When & Then
        mockMvc.perform(patch("/api/roles/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().is5xxServerError());

        verify(roleService).patchRole(eq(999L), any(Map.class));
    }

    // ===============================================
    // Test Cases for DELETE /api/roles/{id}
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteRole_Success() throws Exception {
        // Given
        when(roleService.hapusDataRole(1L)).thenReturn("Success");

        // When & Then
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Role with ID 1 deleted successfully"));

        verify(roleService).hapusDataRole(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testDeleteRole_NotFound() throws Exception {
        // Given
        when(roleService.hapusDataRole(999L))
                .thenThrow(new RuntimeException("Role not found"));

        // When & Then
        mockMvc.perform(delete("/api/roles/999"))
                .andExpect(status().is5xxServerError());

        verify(roleService).hapusDataRole(999L);
    }

    // ===============================================
    // Test Cases for GET /api/roles/{id}/users
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetUsersByRoleId_Success() throws Exception {
        // Given
        when(roleService.getUsersByRoleId(1L)).thenReturn(userList);

        // When & Then
        mockMvc.perform(get("/api/roles/1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(roleService).getUsersByRoleId(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetUsersByRoleId_EmptyList() throws Exception {
        // Given
        when(roleService.getUsersByRoleId(999L)).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/roles/999/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(roleService).getUsersByRoleId(999L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testGetUsersByRoleId_RoleNotFound() throws Exception {
        // Given
        when(roleService.getUsersByRoleId(999L))
                .thenThrow(new RuntimeException("Role not found"));

        // When & Then
        mockMvc.perform(get("/api/roles/999/users"))
                .andExpect(status().is5xxServerError());

        verify(roleService).getUsersByRoleId(999L);
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testCreateRole_ServiceException() throws Exception {
        // Given
        when(roleService.simpanDataRole(any(RoleDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().is5xxServerError());

        verify(roleService).simpanDataRole(any(RoleDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testUpdateRole_PathVariableBinding() throws Exception {
        // Given - Test that path variable ID is correctly set to DTO
        when(roleService.perbaruiDataRole(any(RoleDto.class))).thenReturn("Success");

        // When & Then
        mockMvc.perform(put("/api/roles/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk());

        // Verify service was called (ID should be set from path variable)
        verify(roleService).perbaruiDataRole(any(RoleDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void testPatchRole_EmptyUpdates() throws Exception {
        // Given
        Map<String, Object> emptyUpdates = Map.of();
        when(roleService.patchRole(eq(1L), any(Map.class))).thenReturn(roleDto);

        // When & Then
        mockMvc.perform(patch("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdates)))
                .andExpect(status().isOk());

        verify(roleService).patchRole(eq(1L), any(Map.class));
    }
}