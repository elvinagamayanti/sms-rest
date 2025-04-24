package com.sms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sms.config.JwtUtils;
import com.sms.dto.UserDto;
import com.sms.payload.ApiErrorResponse;
import com.sms.payload.AuthRequest;
import com.sms.payload.AuthResponse;
import com.sms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
public class AuthController {
        @Autowired
        AuthenticationManager authManager;
        @Autowired
        JwtUtils jwtUtil;
        @Autowired
        UserService userService;

        @Operation(summary = "Otentikasi user untuk mendapatkan token jwt.", description = "Menggunakan email dan password untuk mendapatkan token jwt yang digunakan untuk mengakses endpoint yang dilindungi.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "otentikasi berhasil", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
                        }),
                        @ApiResponse(responseCode = "401", description = "email atau password salah", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
                        })
        })
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
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
                        return ResponseEntity.ok().body(response);
                } catch (BadCredentialsException ex) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
        }

        // @Operation(summary = "register new user")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "201", description = "User registered", content =
        // {
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // UserDto.class)) }) })
        // @PostMapping("/register")
        // public ResponseEntity<?> register(@RequestBody UserDto request) {
        // UserDto user = userService.createUser(request);
        // return ResponseEntity.ok().body(user);
        // }
}
