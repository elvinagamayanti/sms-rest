package com.sms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.dto.SimpleUserDto;
import com.sms.dto.UserDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.payload.ApiErrorResponse;
import com.sms.repository.RoleRepository;
import com.sms.service.SatkerService;
import com.sms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for User operations with Hierarchical Role Assignment
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

        private final Logger logger = LoggerFactory.getLogger(UserController.class);

        private final UserService userService;
        private final SatkerService satkerService;

        @Autowired
        private RoleRepository roleRepository;

        public UserController(UserService userService, SatkerService satkerService) {
                this.userService = userService;
                this.satkerService = satkerService;
        }

        // ====================================
        // CORE USER MANAGEMENT ENDPOINTS
        // ====================================

        /**
         * Get all users with automatic filtering based on current user's scope
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get all users", severity = LogSeverity.LOW)
        @Operation(summary = "Menampilkan Daftar Pengguna", description = "Menampilkan daftar pengguna berdasarkan scope akses")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
        public ResponseEntity<List<SimpleUserDto>> getAllUsers() {
                try {
                        // Using filtered method based on user scope
                        List<UserDto> users = userService.findAllUsersFiltered();
                        List<SimpleUserDto> userDtos = users.stream()
                                        .map(UserMapper::mapUserDtoToSimpleUserDto)
                                        .collect(Collectors.toList());

                        return ResponseEntity.ok(userDtos);
                } catch (SecurityException e) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(null);
                } catch (Exception e) {
                        logger.error("Error getting all users: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(null);
                }
        }

        /**
         * Get user by ID with access validation
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get user by ID", severity = LogSeverity.LOW)
        @Operation(summary = "Menampilkan Detail Pengguna", description = "Menampilkan detail pengguna berdasarkan ID")
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
        public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
                try {
                        // Validate access permission
                        if (!userService.canAccessUserData(id)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error",
                                                                "Tidak memiliki akses untuk melihat data user ini"));
                        }

                        User user = userService.findUserById(id);
                        SimpleUserDto userDto = UserMapper.mapToSimpleUserDto(user);
                        return ResponseEntity.ok(userDto);

                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(Map.of("error", "User tidak ditemukan"));
                } catch (Exception e) {
                        logger.error("Error getting user by ID {}: {}", id, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Terjadi kesalahan sistem"));
                }
        }

        /**
         * Create new user with hierarchical role assignment
         */
        @LogActivity(activityType = ActivityType.CREATE, entityType = EntityType.USER, description = "Create new user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Membuat Pengguna Baru", description = "Membuat pengguna baru dengan automatic role assignment")
        @PostMapping
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
                try {
                        // Check if user already exists
                        User existingUser = userService.findUserByEmail(userDto.getEmail());
                        if (existingUser != null) {
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "User dengan email " + userDto.getEmail()
                                                                + " sudah ada"));
                        }

                        // Validate current user can create user in target satker
                        if (!userService.canCreateUserInSatker(userDto.getSatker().getId())) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error",
                                                                "Tidak memiliki akses untuk membuat user di satker ini"));
                        }

                        // Save user (with automatic hierarchical role assignment)
                        userService.saveUser(userDto);

                        User currentUser = userService.getUserLogged();
                        logger.info("User {} created user {} for satker {}",
                                        currentUser.getName(), userDto.getFullName(), userDto.getSatker().getId());

                        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);

                } catch (Exception e) {
                        logger.error("Error creating user: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal membuat user: " + e.getMessage()));
                }
        }

        /**
         * Update user data
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Update user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Update Data Pengguna", description = "Memperbarui data pengguna")
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
                try {
                        // Validate current user can manage target user
                        if (!userService.canManageUser(id)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error", "Tidak memiliki akses untuk mengubah user ini"));
                        }

                        userDto.setId(id);
                        UserDto updatedUser = userService.patchUser(id, Map.of(
                                        "name", userDto.getFullName(),
                                        "email", userDto.getEmail(),
                                        "nip", userDto.getNip(),
                                        "isActive", userDto.getIsActive()));

                        return ResponseEntity.ok(updatedUser);

                } catch (Exception e) {
                        logger.error("Error updating user {}: {}", id, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal mengupdate user: " + e.getMessage()));
                }
        }

        /**
         * Get current authenticated user
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get current user", severity = LogSeverity.LOW)
        @Operation(summary = "Menampilkan Pengguna Login", description = "Menampilkan detail pengguna yang sedang login")
        @GetMapping("/current")
        public ResponseEntity<SimpleUserDto> getCurrentUser() {
                User currentUser = userService.getCurrentUser();
                if (currentUser == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
                SimpleUserDto userDto = UserMapper.mapToSimpleUserDto(currentUser);
                return ResponseEntity.ok(userDto);
        }

        // ====================================
        // USER STATUS MANAGEMENT
        // ====================================

        /**
         * Activate user
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Activate user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Aktifkan Pengguna", description = "Mengaktifkan pengguna yang tidak aktif")
        @PostMapping("/{id}/activate")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> activateUser(@PathVariable("id") Long id) {
                try {
                        if (!userService.canManageUser(id)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error",
                                                                "Tidak memiliki akses untuk mengaktifkan user ini"));
                        }

                        userService.activateUser(id);
                        return ResponseEntity.ok(Map.of("message", "User berhasil diaktifkan"));

                } catch (Exception e) {
                        logger.error("Error activating user {}: {}", id, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal mengaktifkan user"));
                }
        }

        /**
         * Deactivate user
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Deactivate user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Nonaktifkan Pengguna", description = "Menonaktifkan pengguna")
        @PostMapping("/{id}/deactivate")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> deactivateUser(@PathVariable("id") Long id) {
                try {
                        if (!userService.canManageUser(id)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error",
                                                                "Tidak memiliki akses untuk menonaktifkan user ini"));
                        }

                        userService.deactivateUser(id);
                        return ResponseEntity.ok(Map.of("message", "User berhasil dinonaktifkan"));

                } catch (Exception e) {
                        logger.error("Error deactivating user {}: {}", id, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal menonaktifkan user"));
                }
        }

        /**
         * Bulk update user status
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Bulk update user status", severity = LogSeverity.HIGH)
        @Operation(summary = "Update Status Massal", description = "Mengupdate status beberapa pengguna sekaligus")
        @PostMapping("/bulk-status")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<Map<String, Object>> bulkUpdateUserStatus(
                        @RequestBody Map<String, Object> request) {
                try {
                        @SuppressWarnings("unchecked")
                        List<Long> userIds = (List<Long>) request.get("userIds");
                        Boolean isActive = (Boolean) request.get("isActive");

                        if (userIds == null || userIds.isEmpty()) {
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Daftar user ID tidak boleh kosong"));
                        }

                        Map<String, Object> result = userService.bulkUpdateUserStatus(userIds, isActive);
                        return ResponseEntity.ok(result);

                } catch (Exception e) {
                        logger.error("Error bulk updating user status: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal mengupdate status user"));
                }
        }

        // ====================================
        // ROLE MANAGEMENT
        // ====================================

        /**
         * Assign role to user
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Assign role to user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Assign Role ke User", description = "Memberikan role tertentu kepada user")
        @PostMapping("/{userId}/roles/{roleId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> assignRoleToUser(
                        @PathVariable("userId") Long userId,
                        @PathVariable("roleId") Long roleId) {
                try {
                        // Validate permission to assign this role
                        if (!userService.canAssignRole(userId, roleId)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error", "Tidak memiliki akses untuk assign role ini"));
                        }

                        userService.assignRoleToUser(userId, roleId);
                        return ResponseEntity.ok(Map.of("message", "Role berhasil di-assign ke user"));

                } catch (Exception e) {
                        logger.error("Error assigning role {} to user {}: {}", roleId, userId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("error", e.getMessage()));
                }
        }

        /**
         * Remove role from user
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Remove role from user", severity = LogSeverity.MEDIUM)
        @Operation(summary = "Remove Role dari User", description = "Menghapus role tertentu dari user")
        @DeleteMapping("/{userId}/roles/{roleId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<?> removeRoleFromUser(
                        @PathVariable("userId") Long userId,
                        @PathVariable("roleId") Long roleId) {
                try {
                        if (!userService.canManageUser(userId)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error", "Tidak memiliki akses untuk manage user ini"));
                        }

                        userService.removeRoleFromUser(userId, roleId);
                        return ResponseEntity.ok(Map.of("message", "Role berhasil dihapus dari user"));

                } catch (Exception e) {
                        logger.error("Error removing role {} from user {}: {}", roleId, userId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("error", e.getMessage()));
                }
        }

        /**
         * Get manageable roles for current user
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get manageable roles", severity = LogSeverity.LOW)
        @Operation(summary = "Dapatkan Role yang Bisa Dikelola", description = "Menampilkan daftar role yang bisa di-assign oleh user saat ini")
        @GetMapping("/manageable-roles")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<List<String>> getManageableRoles() {
                try {
                        List<String> roles = userService.getManageableRoles();
                        return ResponseEntity.ok(roles);
                } catch (Exception e) {
                        logger.error("Error getting manageable roles: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Arrays.asList());
                }
        }

        @Operation(summary = "Update Sebagian Data User", description = "Memperbarui sebagian field user tanpa harus mengisi semua field")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                        @ApiResponse(responseCode = "404", description = "User tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
        })
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Partially update user by ID", severity = LogSeverity.MEDIUM)
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        @PatchMapping("/{id}")
        public ResponseEntity<UserDto> patchUser(
                        @PathVariable("id") Long id,
                        @RequestBody Map<String, Object> updates) {

                UserDto userDto = userService.patchUser(id, updates);
                return ResponseEntity.ok(userDto);
        }

        // ====================================
        // SATKER & GEOGRAPHIC MANAGEMENT
        // ====================================

        /**
         * Get available satkers for user creation
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get available satkers for user creation", severity = LogSeverity.LOW)
        @Operation(summary = "Dapatkan Satker Tersedia", description = "Menampilkan daftar satker yang bisa digunakan untuk membuat user")
        @GetMapping("/available-satkers")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<List<Long>> getAvailableSatkersForUserCreation() {
                try {
                        List<Long> satkerIds = userService.getAvailableSatkerIdsForUserCreation();
                        return ResponseEntity.ok(satkerIds);
                } catch (Exception e) {
                        logger.error("Error getting available satkers: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Arrays.asList());
                }
        }

        /**
         * Transfer user to different satker
         */
        @LogActivity(activityType = ActivityType.UPDATE, entityType = EntityType.USER, description = "Transfer user to different satker", severity = LogSeverity.HIGH)
        @Operation(summary = "Transfer User ke Satker Lain", description = "Memindahkan user ke satker yang berbeda")
        @PostMapping("/{userId}/transfer")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI')")
        public ResponseEntity<Map<String, Object>> transferUserToSatker(
                        @PathVariable("userId") Long userId,
                        @RequestBody Map<String, Long> request) {
                try {
                        Long newSatkerId = request.get("newSatkerId");
                        if (newSatkerId == null) {
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "newSatkerId tidak boleh kosong"));
                        }

                        Map<String, Object> result = userService.transferUserToSatker(userId, newSatkerId);
                        return ResponseEntity.ok(result);

                } catch (Exception e) {
                        logger.error("Error transferring user {} to satker: {}", userId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal mentransfer user"));
                }
        }

        /**
         * Get users under management
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get users under management", severity = LogSeverity.LOW)
        @Operation(summary = "Dapatkan User yang Dikelola", description = "Menampilkan daftar user yang bisa dikelola oleh user saat ini")
        @GetMapping("/under-management")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
        public ResponseEntity<List<SimpleUserDto>> getUsersUnderManagement() {
                try {
                        List<UserDto> users = userService.getUsersUnderManagement();
                        List<SimpleUserDto> userDtos = users.stream()
                                        .map(UserMapper::mapUserDtoToSimpleUserDto)
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(userDtos);

                } catch (Exception e) {
                        logger.error("Error getting users under management: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Arrays.asList());
                }
        }

        // ====================================
        // STATISTICS & REPORTING
        // ====================================

        /**
         * Get user statistics for current scope
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get user statistics", severity = LogSeverity.LOW)
        @Operation(summary = "Statistik User", description = "Menampilkan statistik user dalam scope saat ini")
        @GetMapping("/statistics")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
        public ResponseEntity<Map<String, Object>> getUserStatistics() {
                try {
                        Map<String, Object> statistics = userService.getUserStatisticsForCurrentScope();
                        return ResponseEntity.ok(statistics);

                } catch (Exception e) {
                        logger.error("Error getting user statistics: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Gagal mengambil statistik"));
                }
        }

        /**
         * Count users by role
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Count users by role", severity = LogSeverity.LOW)
        @Operation(summary = "Hitung User berdasarkan Role", description = "Menghitung jumlah user dengan role tertentu")
        @GetMapping("/count-by-role")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
        public ResponseEntity<Map<String, Long>> countUsersByRole(@RequestParam("roleName") String roleName) {
                try {
                        Long count = userService.countUsersByRole(roleName);
                        return ResponseEntity.ok(Map.of("roleName", count));

                } catch (Exception e) {
                        logger.error("Error counting users by role {}: {}", roleName, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of());
                }
        }

        // ====================================
        // EXISTING ENDPOINTS (for backward compatibility)
        // ====================================

        /**
         * Get users by satker ID
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get users by satker ID", severity = LogSeverity.LOW)
        @Operation(summary = "User berdasarkan Satker", description = "Menampilkan user berdasarkan ID satker")
        @GetMapping("/satker/{satkerId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
        public ResponseEntity<List<SimpleUserDto>> getUsersBySatkerId(@PathVariable("satkerId") Long satkerId) {
                try {
                        List<UserDto> users = userService.findUsersBySatkerId(satkerId);
                        List<SimpleUserDto> userDtos = users.stream()
                                        .map(UserMapper::mapUserDtoToSimpleUserDto)
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(userDtos);

                } catch (Exception e) {
                        logger.error("Error getting users by satker {}: {}", satkerId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Arrays.asList());
                }
        }

        /**
         * Get users by direktorat ID
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Get users by direktorat ID", severity = LogSeverity.LOW)
        @Operation(summary = "User berdasarkan Direktorat", description = "Menampilkan user berdasarkan ID direktorat")
        @GetMapping("/direktorat/{direktoratId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
        public ResponseEntity<List<SimpleUserDto>> getUsersByDirektoratId(
                        @PathVariable("direktoratId") Long direktoratId) {
                try {
                        List<UserDto> users = userService.findUsersByDirektoratId(direktoratId);
                        List<SimpleUserDto> userDtos = users.stream()
                                        .map(UserMapper::mapUserDtoToSimpleUserDto)
                                        .collect(Collectors.toList());
                        return ResponseEntity.ok(userDtos);

                } catch (Exception e) {
                        logger.error("Error getting users by direktorat {}: {}", direktoratId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Arrays.asList());
                }
        }

        /**
         * Check if user has specific role
         */
        @LogActivity(activityType = ActivityType.VIEW, entityType = EntityType.USER, description = "Check if user has role", severity = LogSeverity.LOW)
        @Operation(summary = "Cek Role User", description = "Memeriksa apakah user memiliki role tertentu")
        @GetMapping("/{userId}/has-role")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
        public ResponseEntity<Map<String, Boolean>> checkUserHasRole(
                        @PathVariable("userId") Long userId,
                        @RequestParam("roleName") String roleName) {
                try {
                        if (!userService.canAccessUserData(userId)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("hasRole", false));
                        }

                        User user = userService.findUserById(userId);
                        boolean hasRole = userService.hasRole(user, roleName);
                        return ResponseEntity.ok(Map.of("hasRole", hasRole));

                } catch (Exception e) {
                        logger.error("Error checking role {} for user {}: {}", roleName, userId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("hasRole", false));
                }
        }
}