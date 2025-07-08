package com.sms.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.payload.ApiErrorResponse;
import com.sms.payload.ChangePasswordRequest;
import com.sms.repository.UserRepository;
import com.sms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * Controller untuk menangani perubahan password user
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class ChangePasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public ChangePasswordController(UserService userService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Mengubah password user yang sedang login
     * 
     * @param changePasswordRequest request yang berisi old password dan new
     *                              password
     * @return response berhasil atau error
     */
    @LogActivity(description = "User changed password", activityType = ActivityType.UPDATE, entityType = EntityType.USER, severity = LogSeverity.HIGH)
    @Operation(summary = "Mengubah Password", description = "Mengubah password user yang sedang login dengan memverifikasi password lama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password berhasil diubah", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Password lama tidak valid atau request tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User tidak terautentikasi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // Mendapatkan user yang sedang login
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                logger.warn("Attempt to change password without authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "User tidak terautentikasi",
                                "message", "Silakan login terlebih dahulu"));
            }

            logger.info("Change password request from user: {}", currentUser.getEmail());

            // Verifikasi password lama
            if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), currentUser.getPassword())) {
                logger.warn("Invalid old password for user: {}", currentUser.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Password lama tidak valid",
                                "message", "Password lama yang Anda masukkan salah"));
            }

            // Validasi password baru tidak sama dengan password lama
            if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), currentUser.getPassword())) {
                logger.warn("New password same as old password for user: {}", currentUser.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Password baru tidak boleh sama dengan password lama",
                                "message", "Silakan gunakan password yang berbeda"));
            }

            // Validasi panjang password baru (minimal 6 karakter)
            if (changePasswordRequest.getNewPassword().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error", "Password terlalu pendek",
                                "message", "Password harus memiliki minimal 6 karakter"));
            }

            // Encrypt password baru dan simpan
            String encryptedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
            currentUser.setPassword(encryptedNewPassword);
            userRepository.save(currentUser);

            logger.info("Password successfully changed for user: {}", currentUser.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "Password berhasil diubah",
                    "status", "success",
                    "timestamp", System.currentTimeMillis()));

        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Terjadi kesalahan sistem",
                            "message", "Silakan coba lagi nanti"));
        }
    }

    /**
     * Validasi kekuatan password (optional endpoint untuk frontend)
     * 
     * @param password password yang akan divalidasi
     * @return hasil validasi password
     */
    @LogActivity(description = "User validated password strength", activityType = ActivityType.VIEW, entityType = EntityType.USER, severity = LogSeverity.LOW)
    @Operation(summary = "Validasi Kekuatan Password", description = "Memvalidasi kekuatan password sebelum mengubah password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hasil validasi password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/validate-password")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "isValid", false,
                    "message", "Password tidak boleh kosong"));
        }

        // Validasi panjang minimal
        if (password.length() < 6) {
            return ResponseEntity.ok(Map.of(
                    "isValid", false,
                    "message", "Password harus memiliki minimal 6 karakter"));
        }

        // Validasi kompleksitas (optional)
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int strength = 0;
        if (hasUpperCase)
            strength++;
        if (hasLowerCase)
            strength++;
        if (hasDigit)
            strength++;
        if (hasSpecialChar)
            strength++;

        String strengthText;
        if (strength <= 1) {
            strengthText = "Lemah";
        } else if (strength <= 2) {
            strengthText = "Sedang";
        } else if (strength <= 3) {
            strengthText = "Kuat";
        } else {
            strengthText = "Sangat Kuat";
        }

        return ResponseEntity.ok(Map.of(
                "isValid", true,
                "strength", strengthText,
                "score", strength,
                "hasUpperCase", hasUpperCase,
                "hasLowerCase", hasLowerCase,
                "hasDigit", hasDigit,
                "hasSpecialChar", hasSpecialChar,
                "message", "Password valid"));
    }
}