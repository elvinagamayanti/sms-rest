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

    /**
     * Assign direktorat to user
     * 
     * @param userId       user id
     * @param direktoratId direktorat id
     * @return success message
     */
    @Operation(summary = "Memberikan Direktorat kepada Pengguna", description = "Memberikan direktorat tertentu kepada pengguna berdasarkan ID pengguna dan ID direktorat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direktorat assigned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User or direktorat not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{userId}/direktorat/{direktoratId}")
    public ResponseEntity<?> assignDirektoratToUser(
            @PathVariable("userId") Long userId,
            @PathVariable("direktoratId") Long direktoratId) {

        try {
            userService.assignDirektoratToUser(userId, direktoratId);
            return ResponseEntity.ok(Map.of(
                    "message", "Direktorat assigned successfully to user with ID " + userId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remove direktorat from user
     * 
     * @param userId user id
     * @return success message
     */
    @Operation(summary = "Menghapus Direktorat dari Pengguna", description = "Menghapus direktorat dari pengguna berdasarkan ID pengguna")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direktorat removed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{userId}/direktorat")
    public ResponseEntity<?> removeDirektoratFromUser(@PathVariable("userId") Long userId) {
        try {
            userService.removeDirektoratFromUser(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Direktorat removed successfully from user with ID " + userId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get users by direktorat ID
     * 
     * @param direktoratId direktorat id
     * @return list of users
     */
    @Operation(summary = "Menampilkan Pengguna Berdasarkan Direktorat", description = "Menampilkan daftar pengguna berdasarkan ID direktorat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna berdasarkan direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/direktorat/{direktoratId}")
    public ResponseEntity<List<UserDto>> getUsersByDirektoratId(@PathVariable("direktoratId") Long direktoratId) {
        List<UserDto> users = userService.findUsersByDirektoratId(direktoratId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by deputi ID
     * 
     * @param deputiId deputi id
     * @return list of users
     */
    @Operation(summary = "Menampilkan Pengguna Berdasarkan Deputi", description = "Menampilkan daftar pengguna berdasarkan ID deputi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna berdasarkan deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/deputi/{deputiId}")
    public ResponseEntity<List<UserDto>> getUsersByDeputiId(@PathVariable("deputiId") Long deputiId) {
        List<UserDto> users = userService.findUsersByDeputiId(deputiId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get active users by direktorat ID
     * 
     * @param direktoratId direktorat id
     * @return list of active users
     */
    @Operation(summary = "Menampilkan Pengguna Aktif Berdasarkan Direktorat", description = "Menampilkan daftar pengguna aktif berdasarkan ID direktorat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna aktif berdasarkan direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/direktorat/{direktoratId}/active")
    public ResponseEntity<List<UserDto>> getActiveUsersByDirektoratId(@PathVariable("direktoratId") Long direktoratId) {
        List<UserDto> users = userService.findActiveUsersByDirektoratId(direktoratId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get active users by deputi ID
     * 
     * @param deputiId deputi id
     * @return list of active users
     */
    @Operation(summary = "Menampilkan Pengguna Aktif Berdasarkan Deputi", description = "Menampilkan daftar pengguna aktif berdasarkan ID deputi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna aktif berdasarkan deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/deputi/{deputiId}/active")
    public ResponseEntity<List<UserDto>> getActiveUsersByDeputiId(@PathVariable("deputiId") Long deputiId) {
        List<UserDto> users = userService.findActiveUsersByDeputiId(deputiId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by direktorat code
     * 
     * @param code direktorat code
     * @return list of users
     */
    @Operation(summary = "Menampilkan Pengguna Berdasarkan Kode Direktorat", description = "Menampilkan daftar pengguna berdasarkan kode direktorat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna berdasarkan kode direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/direktorat/code/{code}")
    public ResponseEntity<List<UserDto>> getUsersByDirektoratCode(@PathVariable("code") String code) {
        List<UserDto> users = userService.findUsersByDirektoratCode(code);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by deputi code
     * 
     * @param code deputi code
     * @return list of users
     */
    @Operation(summary = "Menampilkan Pengguna Berdasarkan Kode Deputi", description = "Menampilkan daftar pengguna berdasarkan kode deputi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar pengguna berdasarkan kode deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/deputi/code/{code}")
    public ResponseEntity<List<UserDto>> getUsersByDeputiCode(@PathVariable("code") String code) {
        List<UserDto> users = userService.findUsersByDeputiCode(code);
        return ResponseEntity.ok(users);
    }

    /**
     * Count users by direktorat
     * 
     * @param direktoratId direktorat id
     * @return count of users
     */
    @Operation(summary = "Menghitung Jumlah Pengguna Berdasarkan Direktorat", description = "Menghitung jumlah pengguna berdasarkan ID direktorat")
    @GetMapping("/direktorat/{direktoratId}/count")
    public ResponseEntity<Map<String, Long>> countUsersByDirektoratId(@PathVariable("direktoratId") Long direktoratId) {
        Long count = userService.countUsersByDirektoratId(direktoratId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Count users by deputi
     * 
     * @param deputiId deputi id
     * @return count of users
     */
    @Operation(summary = "Menghitung Jumlah Pengguna Berdasarkan Deputi", description = "Menghitung jumlah pengguna berdasarkan ID deputi")
    @GetMapping("/deputi/{deputiId}/count")
    public ResponseEntity<Map<String, Long>> countUsersByDeputiId(@PathVariable("deputiId") Long deputiId) {
        Long count = userService.countUsersByDeputiId(deputiId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Reset password user (untuk admin)
     * 
     * @param userId  ID user yang akan direset passwordnya
     * @param request berisi password baru
     * @return success message
     */
    @Operation(summary = "Reset Password User", description = "Reset password user oleh admin (tidak memerlukan password lama)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password berhasil direset", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk reset password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetUserPassword(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> request) {

        try {
            String newPassword = request.get("newPassword");

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Password baru tidak boleh kosong"));
            }

            userService.resetUserPassword(userId, newPassword);

            return ResponseEntity.ok(Map.of(
                    "message", "Password user berhasil direset",
                    "userId", userId,
                    "timestamp", System.currentTimeMillis()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }

    /**
     * Reset password user ke NIP (default password)
     * 
     * @param userId ID user yang akan direset passwordnya
     * @return success message
     */
    @Operation(summary = "Reset Password ke NIP", description = "Reset password user ke NIP sebagai password default")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password berhasil direset ke NIP", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{userId}/reset-to-nip")
    public ResponseEntity<?> resetPasswordToNip(@PathVariable("userId") Long userId) {
        try {
            User user = userService.findUserById(userId);
            userService.resetUserPassword(userId, user.getNip());

            return ResponseEntity.ok(Map.of(
                    "message", "Password berhasil direset ke NIP",
                    "userId", userId,
                    "hint", "Password sekarang sama dengan NIP user",
                    "timestamp", System.currentTimeMillis()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }
}