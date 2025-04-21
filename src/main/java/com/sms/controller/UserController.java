package com.sms.controller;

import com.sms.dto.UserDto;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.RoleRepository;
import com.sms.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for User operations
 * 
 * @author rest-api
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users
     * 
     * @return list of users
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtos = this.userService.findAllUsers();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Get user by id
     * 
     * @param id user id
     * @return user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Create new user
     * 
     * @param userDto user data
     * @return created user
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        // Check if user already exists
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User with email " + userDto.getEmail() + " already exists"));
        }

        userService.saveUser(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    /**
     * Get current authenticated user
     * 
     * @return current user details
     */
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Assign role to user
     * 
     * @param userId user id
     * @param roleId role id
     * @return success message
     */
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> assignRoleToUser(
            @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId) {

        try {
            userService.assignRoleToUser(userId, roleId);
            return ResponseEntity.ok(Map.of(
                    "message", "Role assigned successfully to user with ID " + userId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remove role from user
     * 
     * @param userId user id
     * @param roleId role id
     * @return success message
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(
            @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId) {

        try {
            userService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok(Map.of(
                    "message", "Role removed successfully from user with ID " + userId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check if user has role
     * 
     * @param userId   user id
     * @param roleName role name
     * @return true if user has role, false otherwise
     */
    @GetMapping("/{userId}/hasRole")
    public ResponseEntity<Map<String, Boolean>> checkUserHasRole(
            @PathVariable("userId") Long userId,
            @RequestParam("roleName") String roleName) {

        User user = userService.findUserById(userId);
        boolean hasRole = userService.hasRole(user, roleName);

        return ResponseEntity.ok(Map.of("hasRole", hasRole));
    }
}