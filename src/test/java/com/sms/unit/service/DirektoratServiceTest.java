package com.sms.unit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.DirektoratDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.DirektoratMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.DirektoratService;
import com.sms.service.impl.DirektoratServiceImpl;

public class DirektoratServiceTest {

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

    private DirektoratService direktoratService;

    AutoCloseable autoCloseable;
    Direktorat direktorat;
    DirektoratDto direktoratDto;
    Deputi deputi;
    User user;
    Role role;
    Province province;
    Satker satker;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        direktoratService = new DirektoratServiceImpl(direktoratRepository, deputiRepository, userRepository);

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
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

        direktoratDto = DirektoratMapper.mapToDirektoratDto(direktorat);

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
    void testAmbilDaftarDirektorat() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        when(direktoratRepository.findAll()).thenReturn(new ArrayList<Direktorat>(Collections.singleton(direktorat)));
        assertThat(direktoratService.ambilDaftarDirektorat().get(0).getName()).isEqualTo(direktorat.getName());
    }

    @Test
    void testPerbaruiDataDirektorat() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        when(direktoratRepository.save(direktorat)).thenReturn(direktorat);
        direktoratService.perbaruiDataDirektorat(direktoratDto);
        // Method is void, so we just verify it doesn't throw exception
    }

    @Test
    void testSimpanDataDirektorat() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);
        mock(DeputiRepository.class);

        when(deputiRepository.findById(deputi.getId())).thenReturn(Optional.of(deputi));
        when(direktoratRepository.save(direktorat)).thenReturn(direktorat);

        direktoratService.simpanDataDirektorat(direktoratDto);
        // Method is void, so we just verify it doesn't throw exception
    }

    @Test
    void testHapusDataDirektorat() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class, Mockito.CALLS_REAL_METHODS);

        doNothing().when(direktoratRepository).deleteById(Mockito.any());
        Long delId = direktorat.getId();
        direktoratService.hapusDataDirektorat(delId);
        // Method is void, so we just verify it doesn't throw exception
    }

    @Test
    void testCariDirektoratById() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        when(direktoratRepository.findById(direktorat.getId())).thenReturn(Optional.of(direktorat));
        Long searchId = direktorat.getId();
        assertThat(direktoratService.cariDirektoratById(searchId).getName()).isEqualTo(direktorat.getName());
    }

    @Test
    void testCariDirektoratByCode() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        when(direktoratRepository.findByCode(direktorat.getCode())).thenReturn(Optional.of(direktorat));
        String searchCode = direktorat.getCode();
        assertThat(direktoratService.cariDirektoratByCode(searchCode).getName()).isEqualTo(direktorat.getName());
    }

    @Test
    void testGetDirektoratsByDeputiId() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        Long deputiId = deputi.getId();
        when(direktoratRepository.findByDeputiId(deputiId))
                .thenReturn(new ArrayList<Direktorat>(Collections.singleton(direktorat)));
        assertThat(direktoratService.getDirektoratsByDeputiId(deputiId).get(0).getName())
                .isEqualTo(direktorat.getName());
    }

    @Test
    void testGetUsersByDirektoratId() {
        mock(Direktorat.class);
        mock(User.class);
        mock(DirektoratRepository.class);
        mock(UserRepository.class);

        Long direktoratId = direktorat.getId();
        when(userRepository.findAllUsersByDirektoratId(direktoratId))
                .thenReturn(new ArrayList<User>(Collections.singleton(user)));
        assertThat(direktoratService.getUsersByDirektoratId(direktoratId).get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    void testPatchDirektorat() {
        mock(Direktorat.class);
        mock(DirektoratRepository.class);

        Long updatedId = direktorat.getId();
        Map<String, Object> updates = Map.of("name", "Updated Direktorat");
        when(direktoratRepository.findById(updatedId)).thenReturn(Optional.of(direktorat));
        when(direktoratRepository.save(direktorat)).thenReturn(direktorat);
        assertThat(direktoratService.patchDirektorat(updatedId, updates).getName()).isEqualTo(direktorat.getName());
    }
}