package com.sms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.dto.RoleDto;
import com.sms.dto.SimpleUserDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Role operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Get all roles
     * 
     * @return list of roles
     */
    @LogActivity(description = "Retrieved all roles list", activityType = ActivityType.VIEW, entityType = EntityType.ROLE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roleDtos = this.roleService.ambilDaftarRole();
        return ResponseEntity.ok(roleDtos);
    }

    /**
     * Get role by id
     * 
     * @param id role id
     * @return role details
     */
    @LogActivity(description = "Retrieved role by ID", activityType = ActivityType.VIEW, entityType = EntityType.ROLE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Role berdasarkan ID", description = "Menampilkan detail role berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan role berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable("id") Long id) {
        RoleDto roleDto = roleService.cariRoleById(id);
        return ResponseEntity.ok(roleDto);
    }

    /**
     * Create new role
     * 
     * @param roleDto role data
     * @return created role
     */

    @LogActivity(description = "Created a new role", activityType = ActivityType.CREATE, entityType = EntityType.ROLE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Role Baru", description = "Membuat role baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        roleService.simpanDataRole(roleDto);
        return new ResponseEntity<>(roleDto, HttpStatus.CREATED);
    }

    /**
     * Update existing role
     * 
     * @param id      role id
     * @param roleDto role data
     * @return updated role
     */
    @LogActivity(description = "Updated an existing role", activityType = ActivityType.UPDATE, entityType = EntityType.ROLE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Role", description = "Memperbarui role yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleDto roleDto) {

        // Set the ID from the path variable
        roleDto.setId(id);
        roleService.perbaruiDataRole(roleDto);
        return ResponseEntity.ok(roleDto);
    }

    /**
     * Delete role by id
     * 
     * @param id role id
     * @return success message
     */
    @LogActivity(description = "Deleted a role by ID", activityType = ActivityType.DELETE, entityType = EntityType.ROLE, severity = LogSeverity.HIGH)
    @Operation(summary = "Menghapus Role", description = "Menghapus role berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable("id") Long id) {
        roleService.hapusDataRole(id);
        return ResponseEntity.ok(Map.of("message", "Role with ID " + id + " deleted successfully"));
    }

    /**
     * Get users by role id
     * 
     * @param id role id
     * @return list of users
     */
    @LogActivity(description = "Retrieved users by role ID", activityType = ActivityType.VIEW, entityType = EntityType.ROLE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Pengguna berdasarkan ID Role", description = "Menampilkan daftar pengguna yang memiliki role tertentu berdasarkan ID role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan pengguna berdasarkan ID role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}/users")
    // public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable("id") Long
    // id) {
    // List<User> users = roleService.getUsersByRoleId(id);
    // return ResponseEntity.ok(users);
    // }
    public ResponseEntity<List<SimpleUserDto>> getUsersByRoleId(@PathVariable("id") Long id) {
        List<User> users = roleService.getUsersByRoleId(id);
        List<SimpleUserDto> userDtos = users.stream()
                .map(UserMapper::mapToSimpleUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Update partial role data
     * 
     * @param id      role id
     * @param updates map of fields to update
     * @return updated role
     */
    @LogActivity(description = "Partially updated role data", activityType = ActivityType.UPDATE, entityType = EntityType.ROLE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Role", description = "Memperbarui sebagian field role tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RoleDto> patchRole(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        RoleDto roleDto = roleService.patchRole(id, updates);
        return ResponseEntity.ok(roleDto);
    }
}