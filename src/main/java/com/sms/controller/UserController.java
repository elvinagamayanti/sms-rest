package com.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.dto.UserDto;
import com.sms.entity.User;
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
 * REST API for User operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SatkerService satkerService;

    @Autowired
    private RoleRepository roleRepository;

    public UserController(UserService userService, SatkerService satkerService) {
        this.userService = userService;
        this.satkerService = satkerService;
    }

    /**
     * Get all users
     * 
     * @return list of users
     */
    @Operation(summary = "Menampilkan Daftar Pengguna", description = "Menampilkan daftar seluruh pengguna yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @Operation(summary = "Menampilkan Detail Pengguna", description = "Menampilkan detail pengguna berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @Operation(summary = "Membuat Pengguna Baru", description = "Membuat pengguna baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Menampilkan Pengguna yang Sedang Masuk", description = "Menampilkan detail pengguna yang sedang masuk ke dalam sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @Operation(summary = "Memberikan Peran kepada Pengguna", description = "Memberikan peran tertentu kepada pengguna berdasarkan ID pengguna dan ID peran")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Menghapus Peran dari Pengguna", description = "Menghapus peran tertentu dari pengguna berdasarkan ID pengguna dan ID peran")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role removed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Memeriksa Apakah Pengguna Memiliki Peran Tertentu", description = "Memeriksa apakah pengguna memiliki peran tertentu berdasarkan ID pengguna dan nama peran")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role check successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{userId}/hasRole")
    public ResponseEntity<Map<String, Boolean>> checkUserHasRole(
            @PathVariable("userId") Long userId,
            @RequestParam("roleName") String roleName) {

        User user = userService.findUserById(userId);
        boolean hasRole = userService.hasRole(user, roleName);

        return ResponseEntity.ok(Map.of("hasRole", hasRole));
    }

    @Operation(summary = "Menampilkan Pengguna Berdasarkan Satuan Kerja", description = "Menampilkan daftar pengguna berdasarkan ID satuan kerja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna berdasarkan satuan kerja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/satker/{satkerId}")
    public ResponseEntity<List<User>> getUsersBySatkerId(@PathVariable("satkerId") Long id) {
        List<User> users = satkerService.getUsersBySatkerId(id);
        return ResponseEntity.ok(users);
    }
}