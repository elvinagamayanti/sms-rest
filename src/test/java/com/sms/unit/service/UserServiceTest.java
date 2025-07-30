package com.sms.unit.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sms.dto.UserDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.UserService;
import com.sms.service.impl.UserServiceImpl;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SatkerRepository satkerRepository;

    @Mock
    private DirektoratRepository direktoratRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    AutoCloseable autoCloseable;
    User user;
    UserDto userDto;
    Role role;
    Satker satker;
    Direktorat direktorat;
    Deputi deputi;
    Province province;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder,
                satkerRepository, direktoratRepository);

        // Setup test data
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

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .nip("1234567890")
                .email("test@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .nip("1234567890")
                .email("test@email.com")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for saveUser()
    // ===============================================

    @Test
    public void testSaveUser_Success() {
        // Given
        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));
        when(passwordEncoder.encode("1234567890")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.saveUser(userDto);

        // Then
        verify(satkerRepository).findById(1L);
        verify(passwordEncoder).encode("1234567890");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testSaveUser_SatkerNotFound() {
        // Given
        when(satkerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.saveUser(userDto);
        });
        verify(satkerRepository).findById(1L);
    }

    @Test
    public void testSaveUser_WithNullStatus() {
        // Given
        userDto.setIsActive(null);
        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));
        when(passwordEncoder.encode("1234567890")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.saveUser(userDto);

        // Then
        verify(satkerRepository).findById(1L);
        verify(passwordEncoder).encode("1234567890");
        verify(userRepository).save(any(User.class));
    }

    // ===============================================
    // Test Cases for findUserByEmail()
    // ===============================================

    @Test
    public void testFindUserByEmail_Found() {
        // Given
        when(userRepository.findByEmail("test@email.com")).thenReturn(user);

        // When
        User result = userService.findUserByEmail("test@email.com");

        // Then
        assertNotNull(result);
        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userRepository).findByEmail("test@email.com");
    }

    @Test
    public void testFindUserByEmail_NotFound() {
        // Given
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(null);

        // When
        User result = userService.findUserByEmail("notfound@email.com");

        // Then
        assertThat(result).isNull();
        verify(userRepository).findByEmail("notfound@email.com");
    }

    // ===============================================
    // Test Cases for findAllUsers()
    // ===============================================

    @Test
    public void testFindAllUsers_Success() {
        // Given
        List<User> userList = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<UserDto> result = userService.findAllUsers();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Test");
        assertThat(result.get(0).getLastName()).isEqualTo("User");
        assertThat(result.get(0).getEmail()).isEqualTo("test@email.com");
        verify(userRepository).findAll();
    }

    @Test
    public void testFindAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserDto> result = userService.findAllUsers();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // ===============================================
    // Test Cases for findUserById()
    // ===============================================

    @Test
    public void testFindUserById_Found() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.findUserById(1L);

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
        verify(userRepository).findById(1L);
    }

    @Test
    public void testFindUserById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.findUserById(1L);
        });
        verify(userRepository).findById(1L);
    }

    // ===============================================
    // Test Cases for assignRoleToUser()
    // ===============================================

    @Test
    public void testAssignRoleToUser_Success() {
        // Given
        Role newRole = Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.assignRoleToUser(1L, 2L);

        // Then
        verify(userRepository).findById(1L);
        verify(roleRepository).findById(2L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testAssignRoleToUser_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.assignRoleToUser(1L, 2L);
        });
        verify(userRepository).findById(1L);
    }

    @Test
    public void testAssignRoleToUser_RoleNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.assignRoleToUser(1L, 2L);
        });
        verify(userRepository).findById(1L);
        verify(roleRepository).findById(2L);
    }

    // ===============================================
    // Test Cases for removeRoleFromUser()
    // ===============================================

    @Test
    public void testRemoveRoleFromUser_Success() {
        // Given
        Role additionalRole = Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();

        User userWithMultipleRoles = User.builder()
                .id(1L)
                .name("Test User")
                .nip("1234567890")
                .email("test@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(new ArrayList<>(Arrays.asList(role, additionalRole)))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithMultipleRoles));
        when(userRepository.save(any(User.class))).thenReturn(userWithMultipleRoles);

        // When
        userService.removeRoleFromUser(1L, 2L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRemoveRoleFromUser_LastRole() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.removeRoleFromUser(1L, 1L);
        });
        verify(userRepository).findById(1L);
    }

    // ===============================================
    // Test Cases for hasRole()
    // ===============================================

    @Test
    public void testHasRole_True() {
        // When
        boolean result = userService.hasRole(user, "ROLE_USER");

        // Then
        assertTrue(result);
    }

    @Test
    public void testHasRole_False() {
        // When
        boolean result = userService.hasRole(user, "ROLE_ADMIN");

        // Then
        assertFalse(result);
    }

    // ===============================================
    // Test Cases for activateUser()
    // ===============================================

    @Test
    public void testActivateUser_Success() {
        // Given
        user.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.activateUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    // ===============================================
    // Test Cases for deactivateUser()
    // ===============================================

    @Test
    public void testDeactivateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.deactivateUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    // ===============================================
    // Test Cases for updateUserStatus()
    // ===============================================

    @Test
    public void testUpdateUserStatus_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.updateUserStatus(1L, false);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    // ===============================================
    // Test Cases for findActiveUsers()
    // ===============================================

    @Test
    public void testFindActiveUsers_Success() {
        // Given
        List<User> activeUsers = Arrays.asList(user);
        when(userRepository.findByIsActive(true)).thenReturn(activeUsers);

        // When
        List<UserDto> result = userService.findActiveUsers();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(userRepository).findByIsActive(true);
    }

    // ===============================================
    // Test Cases for findInactiveUsers()
    // ===============================================

    @Test
    public void testFindInactiveUsers_Success() {
        // Given
        User inactiveUser = User.builder()
                .id(2L)
                .name("Inactive User")
                .nip("0987654321")
                .email("inactive@email.com")
                .password("password123")
                .isActive(false)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        List<User> inactiveUsers = Arrays.asList(inactiveUser);
        when(userRepository.findByIsActive(false)).thenReturn(inactiveUsers);

        // When
        List<UserDto> result = userService.findInactiveUsers();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isFalse();
        verify(userRepository).findByIsActive(false);
    }

    // ===============================================
    // Test Cases for assignDirektoratToUser()
    // ===============================================

    @Test
    public void testAssignDirektoratToUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(direktoratRepository.findById(1L)).thenReturn(Optional.of(direktorat));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.assignDirektoratToUser(1L, 1L);

        // Then
        verify(userRepository).findById(1L);
        verify(direktoratRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testAssignDirektoratToUser_DirektoratNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(direktoratRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.assignDirektoratToUser(1L, 1L);
        });
        verify(userRepository).findById(1L);
        verify(direktoratRepository).findById(1L);
    }

    // ===============================================
    // Test Cases for removeDirektoratFromUser()
    // ===============================================

    @Test
    public void testRemoveDirektoratFromUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.removeDirektoratFromUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    // ===============================================
    // Test Cases for findUsersByDirektoratId()
    // ===============================================

    @Test
    public void testFindUsersByDirektoratId_Success() {
        // Given
        List<User> userList = Arrays.asList(user);
        when(userRepository.findAllUsersByDirektoratId(1L)).thenReturn(userList);

        // When
        List<UserDto> result = userService.findUsersByDirektoratId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Test");
        verify(userRepository).findAllUsersByDirektoratId(1L);
    }

    @Test
    public void testFindUsersByDirektoratId_EmptyList() {
        // Given
        when(userRepository.findAllUsersByDirektoratId(1L)).thenReturn(Collections.emptyList());

        // When
        List<UserDto> result = userService.findUsersByDirektoratId(1L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAllUsersByDirektoratId(1L);
    }

    // ===============================================
    // Test Cases for findUsersByDeputiId()
    // ===============================================

    @Test
    public void testFindUsersByDeputiId_Success() {
        // Given
        List<User> userList = Arrays.asList(user);
        when(userRepository.findAllUsersByDeputiId(1L)).thenReturn(userList);

        // When
        List<UserDto> result = userService.findUsersByDeputiId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Test");
        verify(userRepository).findAllUsersByDeputiId(1L);
    }

    // ===============================================
    // Test Cases for findActiveUsersByDirektoratId()
    // ===============================================

    @Test
    public void testFindActiveUsersByDirektoratId_Success() {
        // Given
        List<User> activeUsers = Arrays.asList(user);
        when(userRepository.findActiveByDirektoratId(1L)).thenReturn(activeUsers);

        // When
        List<UserDto> result = userService.findActiveUsersByDirektoratId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(userRepository).findActiveByDirektoratId(1L);
    }

    // ===============================================
    // Test Cases for findActiveUsersByDeputiId()
    // ===============================================

    @Test
    public void testFindActiveUsersByDeputiId_Success() {
        // Given
        List<User> activeUsers = Arrays.asList(user);
        when(userRepository.findActiveByDeputiId(1L)).thenReturn(activeUsers);

        // When
        List<UserDto> result = userService.findActiveUsersByDeputiId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(userRepository).findActiveByDeputiId(1L);
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testSaveUser_WithSpecialCharacters() {
        // Given
        UserDto specialUserDto = UserDto.builder()
                .firstName("Special@User")
                .lastName("Test#123")
                .nip("1234567890")
                .email("special@email.com")
                .isActive(true)
                .satker(satker)
                .build();

        when(satkerRepository.findById(1L)).thenReturn(Optional.of(satker));
        when(passwordEncoder.encode("1234567890")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.saveUser(specialUserDto);

        // Then
        verify(satkerRepository).findById(1L);
        verify(passwordEncoder).encode("1234567890");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testFindUserByEmail_WithMultipleUsers() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .name("Another User")
                .nip("0987654321")
                .email("another@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        when(userRepository.findByEmail("another@email.com")).thenReturn(user2);

        // When
        User result = userService.findUserByEmail("another@email.com");

        // Then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Another User");
        verify(userRepository).findByEmail("another@email.com");
    }

    @Test
    public void testHasRole_WithMultipleRoles() {
        // Given
        Role adminRole = Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .build();

        User userWithMultipleRoles = User.builder()
                .id(1L)
                .name("Test User")
                .roles(Arrays.asList(role, adminRole))
                .build();

        // When
        boolean hasUserRole = userService.hasRole(userWithMultipleRoles, "ROLE_USER");
        boolean hasAdminRole = userService.hasRole(userWithMultipleRoles, "ROLE_ADMIN");
        boolean hasSuperAdminRole = userService.hasRole(userWithMultipleRoles, "ROLE_SUPERADMIN");

        // Then
        assertTrue(hasUserRole);
        assertTrue(hasAdminRole);
        assertFalse(hasSuperAdminRole);
    }

    @Test
    public void testFindAllUsers_WithLargeDataset() {
        // Given
        List<User> largeUserList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            largeUserList.add(User.builder()
                    .id((long) i)
                    .name("User " + i)
                    .nip("123456789" + i)
                    .email("user" + i + "@email.com")
                    .isActive(true)
                    .satker(satker)
                    .direktorat(direktorat)
                    .roles(Arrays.asList(role))
                    .build());
        }
        when(userRepository.findAll()).thenReturn(largeUserList);

        // When
        List<UserDto> result = userService.findAllUsers();

        // Then
        assertThat(result).hasSize(100);
        assertThat(result.get(0).getFirstName()).isEqualTo("User");
        assertThat(result.get(0).getLastName()).isEqualTo("1");
        assertThat(result.get(99).getFirstName()).isEqualTo("User");
        assertThat(result.get(99).getLastName()).isEqualTo("100");
        verify(userRepository).findAll();
    }
}