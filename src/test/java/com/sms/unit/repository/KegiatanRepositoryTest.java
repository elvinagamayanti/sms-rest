package com.sms.unit.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Kegiatan;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.KegiatanRepository;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;

@DataJpaTest
public class KegiatanRepositoryTest {

    @Autowired
    private KegiatanRepository kegiatanRepository;

    @Autowired
    private SatkerRepository satkerRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private DeputiRepository deputiRepository;

    @Autowired
    private DirektoratRepository direktoratRepository;

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    Kegiatan kegiatan;
    Satker satker;
    Program program;
    Direktorat direktorat;
    Output output;
    Province province;
    Deputi deputi;

    @BeforeEach
    void setUp() {
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
        satker.setProvince(province);
        satker.setIsProvince(true);
        satkerRepository.save(satker);

        program = new Program();
        program.setCode("TP01");
        program.setName("Test Program");
        program.setYear("2025");
        programRepository.save(program);

        output = new Output();
        output.setName("Test Output");
        output.setCode("O01");
        output.setYear("2025");
        output.setProgram(program);
        outputRepository.save(output);

        deputi = new Deputi();
        deputi.setName("Test Deputi");
        deputi.setCode("D01");
        deputiRepository.save(deputi);

        direktorat = new Direktorat();
        direktorat.setName("Test Direktorat");
        direktorat.setCode("D0101");
        direktorat.setDeputi(deputi);
        direktoratRepository.save(direktorat);

        kegiatan = new Kegiatan();
        kegiatan.setName("Test Kegiatan");
        kegiatan.setCode("K01");
        kegiatan.setBudget(new java.math.BigDecimal("1000000000"));
        kegiatan.setDirektoratPenanggungJawab(direktorat);
        kegiatan.setProgram(program);
        kegiatan.setOutput(output);
        kegiatan.setSatker(satker);
        kegiatan.setStartDate(new Date());
        kegiatanRepository.save(kegiatan);
    }

    @AfterEach
    void tearDown() {
        kegiatan = null;
        direktorat = null;
        deputi = null;
        output = null;
        program = null;
        satker = null;
        province = null;
        kegiatanRepository.deleteAll();
        direktoratRepository.deleteAll();
        deputiRepository.deleteAll();
        outputRepository.deleteAll();
        programRepository.deleteAll();
        satkerRepository.deleteAll();
        provinceRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByCode_Found() {
        Optional<Kegiatan> foundKegiatan = kegiatanRepository.findByCode(kegiatan.getCode());
        assertThat(foundKegiatan).isPresent();
        assertThat(foundKegiatan.get().getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByDirektoratPenanggungJawabId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByDirektoratPenanggungJawabId(direktorat.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByDirektoratPenanggungJawabCode_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByDirektoratPenanggungJawabCode(direktorat.getCode());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByDeputiPenanggungJawabId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByDeputiPenanggungJawabId(deputi.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByDeputiPenanggungJawabCode_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByDeputiPenanggungJawabCode(deputi.getCode());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByYearAndDirektoratPenanggungJawabId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByYearAndDirektoratPenanggungJawabId(2025,
                direktorat.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testCountKegiatanByDirektorat() {
        List<Object[]> results = kegiatanRepository.countKegiatanByDirektorat();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)[0]).isEqualTo(direktorat.getName());
        assertThat(results.get(0)[1]).isEqualTo(1L); // Should match the single kegiatan created
    }

    @Test
    public void testCountKegiatanByDeputi() {
        List<Object[]> results = kegiatanRepository.countKegiatanByDeputi();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)[0]).isEqualTo(deputi.getName());
        assertThat(results.get(0)[1]).isEqualTo(1L); // Should match the single kegiatan created
    }

    @Test
    public void testFindKegiatanWithoutDirektoratPJ() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findKegiatanWithoutDirektoratPJ();
        assertThat(foundKegiatan).isEmpty(); // All created kegiatan have a direktorat PJ
    }

    @Test
    public void testFindByUserIdAndYear_Found() {
        // Assuming a user with ID 1 exists and has activities in 2025
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByUserIdAndYear(1L, 2025);
        assertThat(foundKegiatan).isEmpty(); // No user activities created in this test
    }

    @Test
    public void testFindWithFilters_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findWithFilters(direktorat.getId(), 2025, program.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testGetMonthlyStatistics_Found() {
        List<Object[]> results = kegiatanRepository.getMonthlyStatistics(2025, direktorat.getId());
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)[0]).isEqualTo(7);
        assertThat(results.get(0)[1]).isEqualTo(1L);
    }

    @Test
    public void testGetTotalBudgetByDirektorat_Found() {
        List<Object[]> results = kegiatanRepository.getTotalBudgetByDirektorat();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)[0]).isEqualTo(direktorat.getName());
        assertThat(results.get(0)[1].toString()).isEqualTo("1000000000.00");
    }

    @Test
    public void testGetTotalBudgetByDeputi_Found() {
        List<Object[]> results = kegiatanRepository.getTotalBudgetByDeputi();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)[0]).isEqualTo(deputi.getName());
        assertThat(results.get(0)[1].toString()).isEqualTo("1000000000.00");
    }

    @Test
    public void testSearchKegiatan_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.searchKegiatan("Test Kegiatan");
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindRecentKegiatanByDirektoratPenanggungJawab_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findRecentByDirektoratPenanggungJawab(direktorat.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testExistsByNameAndSatkerId_Found() {
        boolean exists = kegiatanRepository.existsByNameAndSatkerId(kegiatan.getName(), satker.getId());
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindBySatkerId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findBySatkerId(satker.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindUnassignedKegiatanBySatkerId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findUnassignedKegiatanBySatkerId(satker.getId());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindByUserId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByUserId(1L); // Assuming user ID 1 exists
        assertThat(foundKegiatan).isEmpty(); // No user activities created in this test
    }

    @Test
    public void testFindAssignedKegiatanBySatkerId_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findAssignedKegiatanBySatkerId(satker.getId());
        assertThat(foundKegiatan).isEmpty(); // No assigned activities created in this test
    }

    @Test
    public void testCountUnassignedKegiatanBySatkerId_Found() {
        Long count = kegiatanRepository.countUnassignedKegiatanBySatkerId(satker.getId());
        assertThat(count).isEqualTo(1L); // Only the created kegiatan is unassigned
    }

    @Test
    public void testCountByUserId_Found() {
        Long count = kegiatanRepository.countByUserId(1L); // Assuming user ID 1 exists
        assertThat(count).isEqualTo(0L); // No user activities created in this test
    }

    @Test
    public void testFindByProvinceCode_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findByProvinceCode(province.getCode());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindMasterKegiatan_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findMasterKegiatan();
        assertThat(foundKegiatan).isEmpty(); // All created kegiatan are assigned to satkers
    }

    @Test
    public void testFindBySatkerInProvince_Found() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findBySatkerInProvince(province.getCode());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindWithGeographicScope_National() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findWithGeographicScope("NATIONAL", null);
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindWithGeographicScope_Province() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findWithGeographicScope("PROVINCE", province.getCode());
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testFindWithGeographicScope_Satker() {
        List<Kegiatan> foundKegiatan = kegiatanRepository.findWithGeographicScope("SATKER",
                String.valueOf(satker.getId()));
        assertThat(foundKegiatan).isNotEmpty();
        assertThat(foundKegiatan.get(0).getName()).isEqualTo(kegiatan.getName());
    }

    @Test
    public void testCountByScope_Found() {
        Long count = kegiatanRepository.countByScope(province.getCode(), satker.getId());
        assertThat(count).isEqualTo(1L);
    }

    // Test Failure
    @Test
    public void testFindByCode_NotFound() {
        Optional<Kegiatan> foundKegiatan = kegiatanRepository.findByCode("NON_EXISTENT_CODE");
        assertThat(foundKegiatan).isNotPresent();
    }
}
