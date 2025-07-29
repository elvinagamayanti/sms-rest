package com.sms.unit.repository;

import java.util.List;
import java.util.Optional;

import com.sms.repository.UserRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.DeputiRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.sms.entity.User;
import com.sms.entity.Satker;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Deputi;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SatkerRepository satkerRepository;

    @Autowired
    private DirektoratRepository direktoratRepository;

    @Autowired
    private DeputiRepository deputiRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    User user;
    Satker satker;
    Direktorat direktorat;
    Deputi deputi;
    Role role;
    Province province;

    @BeforeEach
    void setUp() {

        role = new Role();
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
    void tearDown() {
        user = null;
        satker = null;
        direktorat = null;
        deputi = null;
        role = null;
        province = null;
        userRepository.deleteAll();
        satkerRepository.deleteAll();
        direktoratRepository.deleteAll();
        deputiRepository.deleteAll();
        roleRepository.deleteAll();
        provinceRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByEmail_Found() {
        User foundUser = userRepository.findByEmail("test@email.com");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getSatker().getName()).isEqualTo(satker.getName());
        assertThat(foundUser.getDirektorat().getName()).isEqualTo(direktorat.getName());
    }

    @Test
    public void testFindByName_Found() {
        Optional<User> foundUser = userRepository.findByName("Test User");
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.get().getSatker().getName()).isEqualTo(satker.getName());
        assertThat(foundUser.get().getDirektorat().getName()).isEqualTo(direktorat.getName());
    }

    @Test
    public void testExistsByEmail_True() {
        boolean exists = userRepository.existsByEmail("test@email.com");
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindAllUsersByRoleId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findAllUsersByRoleId(roleId);
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindAllUsersBySatkerId_Found() {
        List<User> foundUsers = userRepository.findAllUsersBySatkerId(satker.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindAllUsersByDirektoratId_Found() {
        List<User> foundUsers = userRepository.findAllUsersByDirektoratId(direktorat.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindAllUsersByDeputiId_Found() {
        List<User> foundUsers = userRepository.findAllUsersByDeputiId(deputi.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindAllUsersByDirektoratCode_Found() {
        List<User> foundUsers = userRepository.findAllUsersByDirektoratCode(direktorat.getCode());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindAllUsersByDeputiCode_Found() {
        List<User> foundUsers = userRepository.findAllUsersByDeputiCode(deputi.getCode());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindByIsActive_Found() {
        List<User> foundUsers = userRepository.findByIsActive(true);
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveByDirektoratId_Found() {
        List<User> foundUsers = userRepository.findActiveByDirektoratId(direktorat.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveByDeputiId_Found() {
        List<User> foundUsers = userRepository.findActiveByDeputiId(deputi.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveBySatkerId_Found() {
        List<User> foundUsers = userRepository.findActiveBySatkerId(satker.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindByIsActiveAndDirektoratId_Found() {
        List<User> foundUsers = userRepository.findByIsActiveAndDirektoratId(true, direktorat.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindByIsActiveAndDeputiId_Found() {
        List<User> foundUsers = userRepository.findByIsActiveAndDeputiId(true, deputi.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindByIsActiveAndSatkerId_Found() {
        List<User> foundUsers = userRepository.findByIsActiveAndSatkerId(true, satker.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testCountActiveUsers() {
        Long count = userRepository.countActiveUsers();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void testCountInactiveUsers() {
        Long count = userRepository.countInactiveUsers();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testCountUsersByDirektoratId() {
        Long count = userRepository.countUsersByDirektoratId(direktorat.getId());
        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void testCountUsersByDeputiId() {
        Long count = userRepository.countUsersByDeputiId(deputi.getId());
        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void testSearchActiveUsersByKeyword_Found() {
        List<User> foundUsers = userRepository.findActiveUsersByKeyword("Test");
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveUsersByRoleIdAndDirektoratId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findActiveUsersByRoleIdAndDirektoratId(roleId, direktorat.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveUsersByRoleIdAndDeputiId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findActiveUsersByRoleIdAndDeputiId(roleId, deputi.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindUsersByRoleIdAndDirektoratId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findUsersByRoleIdAndDirektoratId(roleId, direktorat.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindUsersByRoleIdAndDeputiId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findUsersByRoleIdAndDeputiId(roleId, deputi.getId());
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindActiveUsersByRoleId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findActiveUsersByRoleId(roleId);
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindUsersByStatusAndRoleId_Found() {
        Long roleId = role.getId();
        List<User> foundUsers = userRepository.findUsersByStatusAndRoleId(true, roleId);
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
    }

    @Test
    public void testFindByProvinceCode_Found() {
        List<User> foundUsers = userRepository.findByProvinceCode("01");
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.get(0).getName()).isEqualTo(user.getName());
        assertThat(foundUsers.get(0).getSatker().getProvince().getCode()).isEqualTo(satker.getProvince().getCode());
    }

    // Test Failure

    @Test
    public void testFindByEmail_NotFound() {
        User foundUser = userRepository.findByEmail("test1@email.com");
        assertThat(foundUser).isNull();
    }

    @Test
    public void testFindByName_NotFound() {
        Optional<User> foundUser = userRepository.findByName("Non Existent User");
        assertThat(foundUser.isPresent()).isFalse();
    }

    @Test
    public void testExistsByEmail_False() {
        boolean exists = userRepository.existsByEmail("test1@email.com");
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindAllUsersByRoleId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findAllUsersByRoleId(roleId);
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindAllUsersBySatkerId_NotFound() {
        List<User> foundUsers = userRepository.findAllUsersBySatkerId(999L); // Non-existent satker ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindAllUsersByDirektoratId_NotFound() {
        List<User> foundUsers = userRepository.findAllUsersByDirektoratId(999L); // Non-existent direktorat ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindAllUsersByDeputiId_NotFound() {
        List<User> foundUsers = userRepository.findAllUsersByDeputiId(999L); // Non-existent deputi ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindAllUsersByDirektoratCode_NotFound() {
        List<User> foundUsers = userRepository.findAllUsersByDirektoratCode("D9999"); // Non-existent direktorat code
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindAllUsersByDeputiCode_NotFound() {
        List<User> foundUsers = userRepository.findAllUsersByDeputiCode("D9999"); // Non-existent deputi code
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindByIsActive_NotFound() {
        List<User> foundUsers = userRepository.findByIsActive(false);
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveByDirektoratId_NotFound() {
        List<User> foundUsers = userRepository.findActiveByDirektoratId(999L); // Non-existent direktorat ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveByDeputiId_NotFound() {
        List<User> foundUsers = userRepository.findActiveByDeputiId(999L); // Non-existent deputi ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveBySatkerId_NotFound() {
        List<User> foundUsers = userRepository.findActiveBySatkerId(999L); // Non-existent satker ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindByIsActiveAndDirektoratId_NotFound() {
        List<User> foundUsers = userRepository.findByIsActiveAndDirektoratId(false, 999L); // Non-existent direktorat ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindByIsActiveAndDeputiId_NotFound() {
        List<User> foundUsers = userRepository.findByIsActiveAndDeputiId(false, 999L); // Non-existent deputi ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindByIsActiveAndSatkerId_NotFound() {
        List<User> foundUsers = userRepository.findByIsActiveAndSatkerId(false, 999L); // Non-existent satker ID
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testCountActiveUsers_Zero() {
        userRepository.deleteAll(); // Ensure no active users
        Long count = userRepository.countActiveUsers();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testCountInactiveUsers_Zero() {
        userRepository.deleteAll(); // Ensure no inactive users
        Long count = userRepository.countInactiveUsers();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testCountUsersByDirektoratId_Zero() {
        Long count = userRepository.countUsersByDirektoratId(999L); // Non-existent direktorat ID
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testCountUsersByDeputiId_Zero() {
        Long count = userRepository.countUsersByDeputiId(999L); // Non-existent deputi ID
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testSearchActiveUsersByKeyword_NotFound() {
        List<User> foundUsers = userRepository.findActiveUsersByKeyword("NonExistent");
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveUsersByRoleIdAndDirektoratId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findActiveUsersByRoleIdAndDirektoratId(roleId, direktorat.getId());
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveUsersByRoleIdAndDeputiId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findActiveUsersByRoleIdAndDeputiId(roleId, deputi.getId());
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindUsersByRoleIdAndDirektoratId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findUsersByRoleIdAndDirektoratId(roleId, direktorat.getId());
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindUsersByRoleIdAndDeputiId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findUsersByRoleIdAndDeputiId(roleId, deputi.getId());
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindActiveUsersByRoleId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findActiveUsersByRoleId(roleId);
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindUsersByStatusAndRoleId_NotFound() {
        Long roleId = 999L; // Non-existent role ID
        List<User> foundUsers = userRepository.findUsersByStatusAndRoleId(false, roleId);
        assertThat(foundUsers).isEmpty();
    }

    @Test
    public void testFindByProvinceCode_NotFound() {
        List<User> foundUsers = userRepository.findByProvinceCode("99");
        assertThat(foundUsers).isEmpty();
    }

}
