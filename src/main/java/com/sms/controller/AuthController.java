package com.sms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.config.JwtUtils;
import com.sms.dto.UserDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.payload.ApiErrorResponse;
import com.sms.payload.AuthRequest;
import com.sms.payload.AuthResponse;
import com.sms.service.TokenBlacklistService;
import com.sms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/") // Eksplisit mapping untuk memastikan routing
public class AuthController {

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        @Autowired
        AuthenticationManager authManager;
        @Autowired
        JwtUtils jwtUtil;
        @Autowired
        UserService userService;
        @Autowired(required = false)
        TokenBlacklistService tokenBlacklistService;

        /**
         * Endpoint untuk otentikasi user dan mendapatkan token JWT.
         * 
         * @param request berisi email dan password
         * @return token JWT jika otentikasi berhasil
         */
        @LogActivity(description = "User authentication attempt", activityType = ActivityType.LOGIN, entityType = EntityType.USER, severity = LogSeverity.MEDIUM)
        @Operation(summary = "Otentikasi user untuk mendapatkan token jwt.", description = "Menggunakan email dan password untuk mendapatkan token jwt yang digunakan untuk mengakses endpoint yang dilindungi.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "otentikasi berhasil", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
                        }),
                        @ApiResponse(responseCode = "401", description = "email atau password salah", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
                        })
        })
        @PostMapping("login")
        public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
                logger.info("Login attempt for email: {}", request.getEmail());
                try {
                        Authentication authentication = authManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(), request.getPassword()));
                        String accessToken = jwtUtil.generateAccessToken(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        UserDto userDetails = (UserDto) authentication.getPrincipal();
                        List<String> roles = userDetails.getAuthorities().stream()
                                        .map(item -> item.getAuthority())
                                        .collect(Collectors.toList());
                        AuthResponse response = new AuthResponse(request.getEmail(), accessToken, roles);
                        logger.info("Login successful for email: {}", request.getEmail());
                        return ResponseEntity.ok().body(response);
                } catch (BadCredentialsException ex) {
                        logger.warn("Login failed for email: {}", request.getEmail());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
        }

        /**
         * Endpoint untuk logout user, menghapus token dari security context dan
         * blacklist token jika service tersedia.
         * 
         * @param request  HttpServletRequest untuk mendapatkan token dari header atau
         *                 cookie
         * @param response HttpServletResponse untuk menghapus cookie jika ada
         * @return ResponseEntity dengan status OK dan pesan logout berhasil
         */
        @LogActivity(description = "User logout attempt", activityType = ActivityType.LOGOUT, entityType = EntityType.USER, severity = LogSeverity.MEDIUM)
        @Operation(summary = "Logout user", description = "Logout user dan blacklist token")
        @PostMapping("logout")
        public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
                logger.info("Logout request received");

                try {
                        String token = extractToken(request);
                        logger.info("Token extracted: {}", token != null ? "Present" : "Not found");

                        if (token != null) {
                                // Blacklist token jika service tersedia
                                if (tokenBlacklistService != null) {
                                        tokenBlacklistService.blacklistToken(token);
                                        logger.info("Token blacklisted successfully");
                                } else {
                                        logger.warn("TokenBlacklistService not available");
                                }
                        }

                        // Clear security context
                        SecurityContextHolder.clearContext();

                        // Clear cookie jika ada
                        clearCookieIfExists(request, response);

                        logger.info("Logout completed successfully");
                        return ResponseEntity.ok()
                                        .body(Map.of(
                                                        "message", "Logout berhasil",
                                                        "status", "success",
                                                        "timestamp", System.currentTimeMillis()));

                } catch (Exception ex) {
                        logger.error("Error during logout: {}", ex.getMessage(), ex);
                        // Tetap clear context meskipun ada error
                        SecurityContextHolder.clearContext();
                        return ResponseEntity.ok()
                                        .body(Map.of(
                                                        "message", "Logout berhasil",
                                                        "status", "success",
                                                        "note",
                                                        "Some cleanup operations failed but user is logged out"));
                }
        }

        private String extractToken(HttpServletRequest request) {
                // Prioritas: Authorization header, lalu cookie
                if (hasAuthorizationBearer(request)) {
                        return getAccessToken(request);
                } else if (hasCookieToken(request)) {
                        return getCookieToken(request);
                }
                return null;
        }

        private void clearCookieIfExists(HttpServletRequest request, HttpServletResponse response) {
                if (hasCookieToken(request)) {
                        Cookie cookie = new Cookie("jwt_token", null);
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        response.addCookie(cookie);
                        logger.info("JWT cookie cleared");
                }
        }

        // Helper methods
        private boolean hasAuthorizationBearer(HttpServletRequest request) {
                String header = request.getHeader("Authorization");
                return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
        }

        private String getAccessToken(HttpServletRequest request) {
                String header = request.getHeader("Authorization");
                return header.split(" ")[1].trim();
        }

        private boolean hasCookieToken(HttpServletRequest request) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                        for (Cookie cookie : cookies) {
                                if ("jwt_token".equals(cookie.getName())) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        private String getCookieToken(HttpServletRequest request) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                        for (Cookie cookie : cookies) {
                                if ("jwt_token".equals(cookie.getName())) {
                                        return cookie.getValue();
                                }
                        }
                }
                return null;
        }
}