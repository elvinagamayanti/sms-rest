package com.sms.controller;

import com.sms.dto.RoleDto;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.service.RoleService;

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
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getUsersByRoleId(@PathVariable("id") Long id) {
        List<User> users = roleService.getUsersByRoleId(id);
        return ResponseEntity.ok(users);
    }
}