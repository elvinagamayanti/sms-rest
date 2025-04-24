package com.sms.controller;

import com.sms.dto.RoleDto;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.service.RoleService;
import com.sms.payload.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for Role operations
 * 
 * @author rest-api
 */
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
    @Operation(summary = "Menampilkan Pengguna berdasarkan ID Role", description = "Menampilkan daftar pengguna yang memiliki role tertentu berdasarkan ID role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan pengguna berdasarkan ID role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Role tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable("id") Long id) {
        List<User> users = roleService.getUsersByRoleId(id);
        return ResponseEntity.ok(users);
    }
}