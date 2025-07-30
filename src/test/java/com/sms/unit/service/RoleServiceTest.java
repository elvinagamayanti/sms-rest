package com.sms.unit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
        roleRepository.save(role);

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
    }

    @Test
    void testPerbaruiDataRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.save(role)).thenReturn(role);
        assertThat(roleService.perbaruiDataRole(roleDto)).isEqualTo("Success");
    }

    @Test
    void testSimpanDataRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.save(role)).thenReturn(role);
        assertThat(roleService.simpanDataRole(roleDto)).isEqualTo("Success");
    }

    @Test
    void testHapusDataRole() {
        mock(Role.class);
        mock(RoleRepository.class, Mockito.CALLS_REAL_METHODS);

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        Mockito.doNothing().when(roleRepository).deleteById(Mockito.any());
        Long delId = role.getId();
        assertThat(roleService.hapusDataRole(delId)).isEqualTo("Success");
    }

    @Test
    void testCariRoleById() {
        mock(Role.class);
        mock(RoleRepository.class);

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        Long searchId = role.getId();
        assertThat(roleService.cariRoleById(searchId).getName()).isEqualTo(role.getName());

    }

    @Test
    void testGetUsersByRoleId() {
        mock(Role.class);
        mock(User.class);
        mock(RoleRepository.class);
        mock(UserRepository.class);

        Long searchId = role.getId();
        when(userRepository.findAllUsersByRoleId(searchId))
                .thenReturn(new ArrayList<User>(Collections.singleton(user)));
        when(roleRepository.findAll()).thenReturn(new ArrayList<Role>(Collections.singleton(role)));
        assertThat(roleService.getUsersByRoleId(searchId).get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    void testPatchRole() {
        mock(Role.class);
        mock(RoleRepository.class);

        Long updatedId = role.getId();
        Map<String, Object> updates = Map.of("name", "ROLE_USER");
        when(roleRepository.findById(updatedId)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);
        assertThat(roleService.patchRole(updatedId, updates).getName()).isEqualTo(role.getName());
    }
}
