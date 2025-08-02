package com.sms.unit.controller;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.config.JwtUtils;
import com.sms.controller.AuthController;
import com.sms.dto.UserDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.payload.AuthRequest;
import com.sms.payload.AuthResponse;
import com.sms.service.TokenBlacklistService;
import com.sms.service.UserService;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtils jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthController authController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private UserDto userDto;
    private User user;
    private Authentication authentication;

    // Supporting entities
    private Province province;
    private Satker satker;
    private Deputi deputi;
    private Direktorat direktorat;
    private Role role;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        // Setup supporting entities
        province = Province.builder()
                .id(1L)
                .name("DKI Jakarta")
                .code("31")
                .build();

        satker = Satker.builder()
                .id(1L)
                .name("Test Satker")
                .code("3101")
                .address("Test Address")
                .number("08123456789")
                .email("test@satker.com")
                .province(province)
                .isProvince(false)
                .build();

        deputi = Deputi.builder()
                .id(1L)
                .name("Test Deputi")
                .code("D01")
                .build();

        direktorat = Direktorat.builder()
                .id(1L)
                .name("Test Direktorat")
                .code("D0101")
                .deputi(deputi)
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        // Setup user
        user = User.builder()
                .id(1L)
                .name("Test User")
                .nip("1234567890")
                .email("test@email.com")
                .password("password123")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .roles(Arrays.asList(role))
                .build();

        // Setup UserDto with authorities
        userDto = UserDto.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .nip("1234567890")
                .email("test@email.com")
                .isActive(true)
                .satker(satker)
                .direktorat(direktorat)
                .build();

        // Add authorities to userDto
        userDto.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        // Setup test data
        authRequest = new AuthRequest("test@email.com", "password123");
        authResponse = new AuthResponse("test@email.com", "mock-jwt-token", Arrays.asList("ROLE_USER"));

        // Setup mock authentication
        authentication = new UsernamePasswordAuthenticationToken(userDto, null, userDto.getAuthorities());
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed
    }

    // ===============================================
    // Test Cases for POST /login
    // ===============================================

    @Test
    void testLogin_Success() throws Exception {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateAccessToken(authentication)).thenReturn("mock-jwt-token");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateAccessToken(authentication);
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_InvalidEmailFormat() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest("invalid-email", "password123");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_EmptyEmail() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest("", "password123");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_EmptyPassword() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest("test@email.com", "");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_NullCredentials() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest(null, null);

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_UserWithMultipleRoles() throws Exception {
        // Given
        UserDto adminUserDto = UserDto.builder()
                .id(2L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@email.com")
                .authorities(Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                adminUserDto, null, adminUserDto.getAuthorities());

        AuthRequest adminRequest = new AuthRequest("admin@email.com", "password123");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(adminAuth);
        when(jwtUtil.generateAccessToken(adminAuth)).thenReturn("admin-jwt-token");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@email.com"))
                .andExpect(jsonPath("$.accessToken").value("admin-jwt-token"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.roles[1]").value("ROLE_ADMIN"));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateAccessToken(adminAuth);
    }

    // ===============================================
    // Test Cases for POST /logout
    // ===============================================

    @Test
    void testLogout_WithAuthorizationHeader_Success() throws Exception {
        // Given & When & Then
        mockMvc.perform(post("/logout")
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testLogout_WithCookie_Success() throws Exception {
        // Given - Setup JWT cookie
        Cookie jwtCookie = new Cookie("jwt_token", "cookie-jwt-token");

        // When & Then
        mockMvc.perform(post("/logout")
                .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testLogout_NoToken_Success() throws Exception {
        // Given & When & Then
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testLogout_TokenBlacklistServiceUnavailable() throws Exception {
        // Given - Temporarily set tokenBlacklistService to null using reflection
        ReflectionTestUtils.setField(authController, "tokenBlacklistService", null);

        // When & Then - Logout should still succeed even without blacklist service
        mockMvc.perform(post("/logout")
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"));

        // Restore the mock service for other tests
        ReflectionTestUtils.setField(authController, "tokenBlacklistService", tokenBlacklistService);
    }

    @Test
    void testLogout_BlacklistServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Blacklist service error"))
                .when(tokenBlacklistService).blacklistToken("mock-jwt-token");

        // When & Then
        mockMvc.perform(post("/logout")
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk()) // Should still succeed even if blacklist fails
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.note").exists()); // Should contain error note

        verify(tokenBlacklistService).blacklistToken("mock-jwt-token");
    }

    @Test
    void testLogout_WithInvalidAuthorizationHeader() throws Exception {
        // Given & When & Then
        mockMvc.perform(post("/logout")
                .header("Authorization", "Invalid token format"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    // ===============================================
    // Edge Cases and Additional Tests
    // ===============================================

    @Test
    void testLogin_AuthenticationManagerException() throws Exception {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication service error"));

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().is5xxServerError());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_JwtUtilException() throws Exception {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateAccessToken(authentication))
                .thenThrow(new RuntimeException("JWT generation error"));

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().is5xxServerError());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateAccessToken(authentication);
    }

    @Test
    void testLogin_EmptyRolesList() throws Exception {
        // Given
        UserDto userWithoutRoles = UserDto.builder()
                .id(1L)
                .email("test@email.com")
                .authorities(Arrays.asList()) // Empty authorities
                .build();

        Authentication authWithoutRoles = new UsernamePasswordAuthenticationToken(
                userWithoutRoles, null, userWithoutRoles.getAuthorities());

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authWithoutRoles);
        when(jwtUtil.generateAccessToken(authWithoutRoles)).thenReturn("no-roles-token");

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.accessToken").value("no-roles-token"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").isEmpty());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateAccessToken(authWithoutRoles);
    }

    @Test
    void testLogin_MalformedJson() throws Exception {
        // Given
        String malformedJson = "{\"email\":\"test@email.com\",\"password\":}"; // Missing password value

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogout_MultipleCookies() throws Exception {
        // Given - Setup cookies in the request
        Cookie jwtCookie = new Cookie("jwt_token", "cookie-jwt-token");
        Cookie otherCookie = new Cookie("other_cookie", "other-value");

        // When & Then
        mockMvc.perform(post("/logout")
                .cookie(jwtCookie)
                .cookie(otherCookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout berhasil"))
                .andExpect(jsonPath("$.status").value("success"));
    }
}