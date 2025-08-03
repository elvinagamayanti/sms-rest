package com.sms.unit.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.entity.Role;
import com.sms.repository.RoleRepository;
import com.sms.service.RoleInitializationService;

public class RoleInitializationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleInitializationService roleInitializationService;

    AutoCloseable autoCloseable;
    Role role;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        roleInitializationService = new RoleInitializationService(roleRepository);

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testInitializeRoles_AllRolesCreated() {
        mock(RoleRepository.class);

        // Mock all roles as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(false);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify all roles are checked for existence
        verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        verify(roleRepository).existsByName("ROLE_ADMIN_PUSAT");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PUSAT");
        verify(roleRepository).existsByName("ROLE_ADMIN_PROVINSI");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PROVINSI");
        verify(roleRepository).existsByName("ROLE_ADMIN_SATKER");
        verify(roleRepository).existsByName("ROLE_OPERATOR_SATKER");
        verify(roleRepository).existsByName("ROLE_USER");

        // Verify all roles are created (8 roles)
        verify(roleRepository, times(8)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_SomeRolesAlreadyExist() {
        mock(RoleRepository.class);

        // Mock some roles as existing, some as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(false);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify all roles are checked for existence
        verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        verify(roleRepository).existsByName("ROLE_ADMIN_PUSAT");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PUSAT");
        verify(roleRepository).existsByName("ROLE_ADMIN_PROVINSI");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PROVINSI");
        verify(roleRepository).existsByName("ROLE_ADMIN_SATKER");
        verify(roleRepository).existsByName("ROLE_OPERATOR_SATKER");
        verify(roleRepository).existsByName("ROLE_USER");

        // Verify only non-existing roles are created (4 roles)
        verify(roleRepository, times(4)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_AllRolesAlreadyExist() {
        mock(RoleRepository.class);

        // Mock all roles as existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        roleInitializationService.initializeRoles();

        // Verify all roles are checked for existence
        verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        verify(roleRepository).existsByName("ROLE_ADMIN_PUSAT");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PUSAT");
        verify(roleRepository).existsByName("ROLE_ADMIN_PROVINSI");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PROVINSI");
        verify(roleRepository).existsByName("ROLE_ADMIN_SATKER");
        verify(roleRepository).existsByName("ROLE_OPERATOR_SATKER");
        verify(roleRepository).existsByName("ROLE_USER");

        // Verify no roles are created since all exist
        verify(roleRepository, never()).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_SuperAdminRoleCreation() {
        mock(RoleRepository.class);

        // Mock only ROLE_SUPERADMIN as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify ROLE_SUPERADMIN is checked and created
        verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        verify(roleRepository, times(1)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_AdminRolesCreation() {
        mock(RoleRepository.class);

        // Mock admin roles as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify admin roles are checked and created (3 admin roles)
        verify(roleRepository).existsByName("ROLE_ADMIN_PUSAT");
        verify(roleRepository).existsByName("ROLE_ADMIN_PROVINSI");
        verify(roleRepository).existsByName("ROLE_ADMIN_SATKER");
        verify(roleRepository, times(3)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_OperatorRolesCreation() {
        mock(RoleRepository.class);

        // Mock operator roles as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify operator roles are checked and created (3 operator roles)
        verify(roleRepository).existsByName("ROLE_OPERATOR_PUSAT");
        verify(roleRepository).existsByName("ROLE_OPERATOR_PROVINSI");
        verify(roleRepository).existsByName("ROLE_OPERATOR_SATKER");
        verify(roleRepository, times(3)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_UserRoleCreation() {
        mock(RoleRepository.class);

        // Mock only ROLE_USER as not existing
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(false);

        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify ROLE_USER is checked and created
        verify(roleRepository).existsByName("ROLE_USER");
        verify(roleRepository, times(1)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_RepositoryExceptionHandling() {
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenReturn(false);
        when(roleRepository.existsByName("ROLE_ADMIN_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PUSAT")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_PROVINSI")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_ADMIN_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_OPERATOR_SATKER")).thenReturn(true);
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        when(roleRepository.save(Mockito.any(Role.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            roleInitializationService.initializeRoles();
        });

        verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        verify(roleRepository, times(1)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_CheckRoleExistenceExceptionHandling() {
        when(roleRepository.existsByName("ROLE_SUPERADMIN")).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleInitializationService.initializeRoles();
        });

        verify(roleRepository).existsByName("ROLE_SUPERADMIN");

        verify(roleRepository, never()).existsByName("ROLE_ADMIN_PUSAT");
        verify(roleRepository, never()).existsByName("ROLE_OPERATOR_PUSAT");
    }

    @Test
    void testInitializeRoles_VerifyAllRequiredRoles() {
        mock(RoleRepository.class);

        // Mock all roles as not existing to verify all required roles are handled
        when(roleRepository.existsByName(Mockito.anyString())).thenReturn(false);
        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify all 8 required roles are checked
        String[] requiredRoles = {
                "ROLE_SUPERADMIN",
                "ROLE_ADMIN_PUSAT",
                "ROLE_OPERATOR_PUSAT",
                "ROLE_ADMIN_PROVINSI",
                "ROLE_OPERATOR_PROVINSI",
                "ROLE_ADMIN_SATKER",
                "ROLE_OPERATOR_SATKER",
                "ROLE_USER"
        };

        for (String roleName : requiredRoles) {
            verify(roleRepository).existsByName(roleName);
        }

        // Verify all 8 roles are created
        verify(roleRepository, times(8)).save(Mockito.any(Role.class));
    }

    @Test
    void testInitializeRoles_RoleCreationOrder() {
        mock(RoleRepository.class);

        // Mock all roles as not existing
        when(roleRepository.existsByName(Mockito.anyString())).thenReturn(false);
        when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        roleInitializationService.initializeRoles();

        // Verify roles are created in expected order
        var inOrder = Mockito.inOrder(roleRepository);
        inOrder.verify(roleRepository).existsByName("ROLE_SUPERADMIN");
        inOrder.verify(roleRepository).existsByName("ROLE_ADMIN_PUSAT");
        inOrder.verify(roleRepository).existsByName("ROLE_OPERATOR_PUSAT");
        inOrder.verify(roleRepository).existsByName("ROLE_ADMIN_PROVINSI");
        inOrder.verify(roleRepository).existsByName("ROLE_OPERATOR_PROVINSI");
        inOrder.verify(roleRepository).existsByName("ROLE_ADMIN_SATKER");
        inOrder.verify(roleRepository).existsByName("ROLE_OPERATOR_SATKER");
        inOrder.verify(roleRepository).existsByName("ROLE_USER");
    }
}