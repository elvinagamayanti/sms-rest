package com.sms.unit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Kegiatan;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.KegiatanMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.KegiatanRepository;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.KegiatanService;
import com.sms.service.UserService;
import com.sms.service.impl.KegiatanServiceImpl;

public class KegiatanServiceTest {

    @Mock
    private KegiatanRepository kegiatanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SatkerRepository satkerRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private OutputRepository outputRepository;

    @Mock
    private DirektoratRepository direktoratRepository;

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DeputiRepository deputiRepository;

    @Mock
    private UserService userService;

    private KegiatanService kegiatanService;

    AutoCloseable autoCloseable;
    Kegiatan kegiatan;
    KegiatanDto kegiatanDto;
    User user;
    Role role;
    Province province;
    Satker satker;
    Deputi deputi;
    Direktorat direktorat;
    Program program;
    Output output;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        kegiatanService = new KegiatanServiceImpl(kegiatanRepository, userRepository, satkerRepository,
                programRepository, outputRepository, direktoratRepository, userService);

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

        program = new Program();
        program.setId(1L);
        program.setName("Test Program");
        program.setCode("P001");
        programRepository.save(program);

        output = new Output();
        output.setId(1L);
        output.setName("Test Output");
        output.setCode("O001");
        outputRepository.save(output);

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

        kegiatan = new Kegiatan();
        kegiatan.setId(1L);
        kegiatan.setName("Test Kegiatan");
        kegiatan.setCode("K001");
        kegiatan.setBudget(new BigDecimal("1000000"));
        kegiatan.setStartDate(java.sql.Date.valueOf(LocalDate.now()));
        kegiatan.setEndDate(Date.valueOf(LocalDate.now().plusDays(30)));
        kegiatan.setUser(user);
        kegiatan.setSatker(satker);
        kegiatan.setProgram(program);
        kegiatan.setOutput(output);
        kegiatan.setDirektoratPenanggungJawab(direktorat);

        kegiatanDto = KegiatanMapper.mapToKegiatanDto(kegiatan);

        kegiatanRepository.save(kegiatan);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testAmbilDaftarKegiatan() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findAll()).thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.ambilDaftarKegiatan().get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    void testSimpanDataKegiatan() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);
        mock(UserService.class);
        mock(UserRepository.class);

        when(userService.getUserLogged()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(kegiatanRepository.save(Mockito.any(Kegiatan.class))).thenReturn(kegiatan);

        KegiatanDto result = kegiatanService.simpanDataKegiatan(kegiatanDto);
        assertThat(result.getName()).isEqualTo(kegiatanDto.getName());
    }

    @Test
    void testPerbaruiDataKegiatan() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.save(kegiatan)).thenReturn(kegiatan);
        kegiatanService.perbaruiDataKegiatan(kegiatanDto);
        // Method is void, so we just verify it doesn't throw exception
    }

    @Test
    void testHapusDataKegiatan() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class, Mockito.CALLS_REAL_METHODS);

        when(kegiatanRepository.findById(kegiatan.getId())).thenReturn(Optional.of(kegiatan));
        doNothing().when(kegiatanRepository).deleteById(Mockito.any());
        Long delId = kegiatan.getId();
        kegiatanService.hapusDataKegiatan(delId);
        // Method is void, so we just verify it doesn't throw exception
    }

    @Test
    void testCariKegiatanById() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findById(kegiatan.getId())).thenReturn(Optional.of(kegiatan));
        Long searchId = kegiatan.getId();
        assertThat(kegiatanService.cariKegiatanById(searchId).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    void testFindKegiatanById() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findById(kegiatan.getId())).thenReturn(Optional.of(kegiatan));
        Long searchId = kegiatan.getId();
        assertThat(kegiatanService.findKegiatanById(searchId).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanByDirektoratPJ() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findByDirektoratPenanggungJawabId(direktorat.getId()))
                .thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.getKegiatanByDirektoratPJ(direktorat.getId()).get(0).getName())
                .isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanByDirektoratPJCode() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findByDirektoratPenanggungJawabCode(direktorat.getCode()))
                .thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.getKegiatanByDirektoratPJCode(direktorat.getCode()).get(0).getName())
                .isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanByDeputiPJ() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findByDeputiPenanggungJawabId(deputi.getId()))
                .thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.getKegiatanByDeputiPJ(deputi.getId()).get(0).getName())
                .isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanByDeputiPJCode() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findByDeputiPenanggungJawabCode(deputi.getCode()))
                .thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.getKegiatanByDeputiPJCode(deputi.getCode()).get(0).getName())
                .isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanByYearAndDirektoratPJ() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);

        when(kegiatanRepository.findByYearAndDirektoratPenanggungJawabId(2024, direktorat.getId()))
                .thenReturn(new ArrayList<Kegiatan>(Collections.singleton(kegiatan)));
        assertThat(kegiatanService.getKegiatanByYearAndDirektoratPJ(2024, direktorat.getId()).get(0).getName())
                .isEqualTo(kegiatan.getName());
    }

    @Test
    void testGetKegiatanStatisticsByDirektorat() {
        mock(KegiatanRepository.class);

        Object[] result = { "Test Direktorat", 5L };
        when(kegiatanRepository.countKegiatanByDirektorat())
                .thenReturn(new ArrayList<>(Collections.singleton(result)));

        var statistics = kegiatanService.getKegiatanStatisticsByDirektorat();
        assertThat(statistics.get("Test Direktorat")).isEqualTo(5L);
    }

    @Test
    void testGetKegiatanStatisticsByDeputi() {
        mock(KegiatanRepository.class);

        Object[] result = { "Test Deputi", 3L };
        when(kegiatanRepository.countKegiatanByDeputi())
                .thenReturn(new ArrayList<>(Collections.singleton(result)));

        var statistics = kegiatanService.getKegiatanStatisticsByDeputi();
        assertThat(statistics.get("Test Deputi")).isEqualTo(3L);
    }

    @Test
    void testGetBudgetStatisticsByDirektorat() {
        mock(KegiatanRepository.class);

        Object[] result = { "Test Direktorat", new BigDecimal("5000000") };
        when(kegiatanRepository.getTotalBudgetByDirektorat())
                .thenReturn(new ArrayList<>(Collections.singleton(result)));

        var statistics = kegiatanService.getBudgetStatisticsByDirektorat();
        assertThat(statistics.get("Test Direktorat")).isEqualTo(new BigDecimal("5000000"));
    }

    @Test
    void testAssignDirektoratPJ() {
        mock(Kegiatan.class);
        mock(KegiatanRepository.class);
        mock(DirektoratRepository.class);

        when(kegiatanRepository.findById(kegiatan.getId())).thenReturn(Optional.of(kegiatan));
        when(direktoratRepository.findById(direktorat.getId())).thenReturn(Optional.of(direktorat));
        when(kegiatanRepository.save(Mockito.any(Kegiatan.class))).thenReturn(kegiatan);

        kegiatanService.assignDirektoratPJ(kegiatan.getId(), direktorat.getId());
        // Method is void, so we just verify it doesn't throw exception
    }
}