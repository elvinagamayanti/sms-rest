package com.sms.unit.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Role;
import com.sms.repository.RoleRepository;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    Role role1;
    Role role2;

    @BeforeEach
    void setUp() {
        role1 = new Role();
        role1.setName("ROLE_ADMIN");
        roleRepository.save(role1);

        role2 = new Role();
        role2.setName("ROLE_USER");
        roleRepository.save(role2);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test if necessary
        role1 = null;
        role2 = null;
        roleRepository.deleteAll();
    }

    // Test Success
    @Test
    public void testFindByName_Found() {
        Role foundRole = roleRepository.findByName("ROLE_ADMIN");
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo(role1.getName());
    }

    @Test
    public void testFindByNameIn_Found() {
        List<Role> foundRoles = roleRepository.findByNameIn(List.of("ROLE_ADMIN", "ROLE_USER"));
        assertThat(foundRoles).isNotEmpty();
        assertThat(foundRoles.size()).isEqualTo(2);
        assertThat(foundRoles.get(0).getName()).isEqualTo(role1.getName());
        assertThat(foundRoles.get(1).getName()).isEqualTo(role2.getName());
    }

    @Test
    public void testExistsByName_True() {
        boolean exists = roleRepository.existsByName("ROLE_ADMIN");
        assertThat(exists).isTrue();
    }

    // Test Failure
    @Test
    public void testFindByName_NotFound() {
        Role foundRole = roleRepository.findByName("ROLE_NON_EXISTENT");
        assertThat(foundRole).isNull();
    }

    @Test
    public void testFindByNameIn_NotFound() {
        List<Role> foundRoles = roleRepository.findByNameIn(List.of("ROLE_NON_EXISTENT"));
        assertThat(foundRoles).isEmpty();
    }

    @Test
    public void testExistsByName_False() {
        boolean exists = roleRepository.existsByName("ROLE_NON_EXISTENT");
        assertThat(exists).isFalse();
    }

}
