package com.sms.unit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.sms.dto.RoleDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.RoleMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.RoleService;
import com.sms.service.impl.RoleServiceImpl;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private SatkerRepository satkerRepository;

    @Mock
    private DeputiRepository deputiRepository;

    @Mock
    private DirektoratRepository direktoratRepository;

    private RoleService roleService;

    AutoCloseable autoCloseable;
    Role role;
    RoleDto roleDto;
    User user;
    Province province;
    Satker satker;
    Deputi deputi;
    Direktorat direktorat;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        roleService = new RoleServiceImpl(roleRepository, userRepository);

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roleDto = RoleMapper.mapToRoleDto(role);
        // roleRepository.save(role);

        province = new Province();
        province.setName("Test Province");
        province.setCode("01");
        provinceRepository.save(province);

        satker = new Satker();
        satker.setName("Test Satker");
        satker.setCode("0100");
        satker.setAddress("123 Test Street");
        satker.setNumber("12345");
        satker.setEmail("satker@email.com");
        satker.setIsProvince(true);
        satker.setProvince(province);
        satkerRepository.save(satker);

        deputi = new Deputi();
        deputi.setName("Test Deputi");
        deputi.setCode("D01");
        deputiRepository.save(deputi);

        direktorat = new Direktorat();
        direktorat.setName("Test Direktorat");
        direktorat.setCode("D0101");
        direktorat.setDeputi(deputi);
        direktoratRepository.save(direktorat);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setNip("1234567890");
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setIsActive(true);
        user.setSatker(satker);
        user.setDirektorat(direktorat);
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testAmbilDaftarRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.findAll()).thenReturn(new ArrayList<Role>(Collections.singleton(role)));
        assertThat(roleService.ambilDaftarRole().get(0).getName()).isEqualTo(role.getName());
        verify(roleRepository).findAll();
    }

    @Test
    void testAmbilDaftarRole_EmptyList() {
        mock(RoleRepository.class);

        when(roleRepository.findAll()).thenReturn(new ArrayList<>());

        List<RoleDto> result = roleService.ambilDaftarRole();
        assertThat(result).isEmpty();
        verify(roleRepository).findAll();
    }

    @Test
    void testPerbaruiDataRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.save(role)).thenReturn(role);
        String result = roleService.perbaruiDataRole(roleDto);

        assertThat(result).isEqualTo("Success");
        verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    void testSimpanDataRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.save(role)).thenReturn(role);
        String result = roleService.simpanDataRole(roleDto);

        assertThat(result).isEqualTo("Success");
        verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    void testHapusDataRole_Success() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(1L);

        String result = roleService.hapusDataRole(1L);

        assertThat(result).isEqualTo("Success");
        verify(roleRepository).findById(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void testHapusDataRole_NotFound() {
        mock(RoleRepository.class);

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.hapusDataRole(1L));

        assertThat(exception.getMessage()).contains("Role not found with id: 1");
        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).deleteById(1L);
    }

    @Test
    void testCariRoleById_Success() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDto result = roleService.cariRoleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(role.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    void testCariRoleById_NotFound() {
        mock(RoleRepository.class);

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.cariRoleById(1L));

        verify(roleRepository).findById(1L);
    }

    @Test
    void testGetUsersByRoleId_Success() {
        mock(User.class);
        mock(UserRepository.class);

        when(userRepository.findAllUsersByRoleId(1L))
                .thenReturn(new ArrayList<User>(Collections.singleton(user)));

        List<User> result = roleService.getUsersByRoleId(1L);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo(user.getName());
        verify(userRepository).findAllUsersByRoleId(1L);
    }

    @Test
    void testGetUsersByRoleId_EmptyList() {
        mock(UserRepository.class);

        when(userRepository.findAllUsersByRoleId(1L)).thenReturn(new ArrayList<>());

        List<User> result = roleService.getUsersByRoleId(1L);

        assertThat(result).isEmpty();
        verify(userRepository).findAllUsersByRoleId(1L);
    }

    @Test
    void testPatchRole_Success() {
        mock(Role.class);
        mock(RoleRepository.class);

        Map<String, Object> updates = Map.of("name", "ROLE_UPDATED");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        RoleDto result = roleService.patchRole(1L, updates);

        assertThat(result).isNotNull();
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    void testPatchRole_NotFound() {
        mock(RoleRepository.class);

        Map<String, Object> updates = Map.of("name", "ROLE_UPDATED");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.patchRole(1L, updates));

        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(Mockito.any(Role.class));
    }

    @Test
    void testPatchRole_NameUpdate() {
        mock(Role.class);
        mock(RoleRepository.class);

        Map<String, Object> updates = Map.of("name", "ROLE_ADMIN");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> {
            Role updatedRole = invocation.getArgument(0);
            return updatedRole;
        });

        RoleDto result = roleService.patchRole(1L, updates);

        assertThat(result).isNotNull();
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    void testPatchRole_EmptyUpdates() {
        mock(Role.class);
        mock(RoleRepository.class);

        Map<String, Object> emptyUpdates = Map.of();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        RoleDto result = roleService.patchRole(1L, emptyUpdates);

        assertThat(result).isNotNull();
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(role);
    }

    @Test
    void testPatchRole_UnknownFields() {
        mock(Role.class);
        mock(RoleRepository.class);

        Map<String, Object> updates = Map.of(
                "name", "ROLE_ADMIN",
                "unknownField", "someValue");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        RoleDto result = roleService.patchRole(1L, updates);

        assertThat(result).isNotNull();
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    void testPatchRole_NullValue() {
        mock(Role.class);
        mock(RoleRepository.class);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", null); // Use HashMap instead of Map.of() for null values
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        RoleDto result = roleService.patchRole(1L, updates);

        assertThat(result).isNotNull();
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(role);
    }
    // ===============================================
    // Test Cases for Multiple Roles
    // ===============================================

    @Test
    void testAmbilDaftarRole_MultipleRoles() {
        mock(RoleRepository.class);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        Role role3 = new Role();
        role3.setId(3L);
        role3.setName("ROLE_SUPERADMIN");

        List<Role> roles = List.of(role1, role2, role3);
        when(roleRepository.findAll()).thenReturn(roles);

        List<RoleDto> result = roleService.ambilDaftarRole();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("ROLE_USER");
        assertThat(result.get(1).getName()).isEqualTo("ROLE_ADMIN");
        assertThat(result.get(2).getName()).isEqualTo("ROLE_SUPERADMIN");
    }

    @Test
    void testGetUsersByRoleId_MultipleUsers() {
        mock(UserRepository.class);

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@email.com");

        List<User> users = List.of(user1, user2);
        when(userRepository.findAllUsersByRoleId(1L)).thenReturn(users);

        List<User> result = roleService.getUsersByRoleId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("User 1");
        assertThat(result.get(1).getName()).isEqualTo("User 2");
    }

    // ===============================================
    // Test Cases for Edge Cases
    // ===============================================

    @Test
    void testSimpanDataRole_NullRoleDto() {
        assertThrows(Exception.class, () -> roleService.simpanDataRole(null));
    }

    @Test
    void testPerbaruiDataRole_NullRoleDto() {
        assertThrows(Exception.class, () -> roleService.perbaruiDataRole(null));
    }

    @Test
    void testCariRoleById_NullId() {
        mock(RoleRepository.class);

        when(roleRepository.findById(null)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.cariRoleById(null));
    }

    @Test
    void testHapusDataRole_NullId() {
        mock(RoleRepository.class);

        when(roleRepository.findById(null)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.hapusDataRole(null));
    }
}