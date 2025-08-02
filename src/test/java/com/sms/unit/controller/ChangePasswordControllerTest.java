package com.sms.unit.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.ChangePasswordController;
import com.sms.entity.User;
import com.sms.payload.ChangePasswordRequest;
import com.sms.repository.UserRepository;
import com.sms.service.UserService;

/**
 * Unit Tests for ChangePasswordController
 * 
 * @author generated
 */
public class ChangePasswordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangePasswordController changePasswordController;

    private ObjectMapper objectMapper;
    private ChangePasswordRequest changePasswordRequest;
    private User currentUser;

    AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(changePasswordController).build();
        objectMapper = new ObjectMapper();

        // Setup ChangePasswordRequest
        changePasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword("newPassword456")
                .build();

        // Setup User
        currentUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .nip("1234567890")
                .password("$2a$10$encodedOldPassword") // BCrypt encoded password
                .isActive(true)
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    // ===============================================
    // Test Cases for POST /api/auth/change-password
    // ===============================================

    @Test
    public void testChangePassword_Success() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword456", currentUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword456")).thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Password berhasil diubah")))
                .andExpect(jsonPath("$.status", is("success")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).encode("newPassword456");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_UserNotAuthenticated() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("User tidak terautentikasi")))
                .andExpect(jsonPath("$.message", is("Silakan login terlebih dahulu")));

        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testChangePassword_InvalidOldPassword() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Password lama tidak valid")))
                .andExpect(jsonPath("$.message", is("Password lama yang Anda masukkan salah")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).matches("oldPassword123", currentUser.getPassword());
    }

    @Test
    public void testChangePassword_SamePasswordAsOld() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword456", currentUser.getPassword())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Password baru tidak boleh sama dengan password lama")))
                .andExpect(jsonPath("$.message", is("Silakan gunakan password yang berbeda")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).matches("oldPassword123", currentUser.getPassword());
        verify(passwordEncoder, times(1)).matches("newPassword456", currentUser.getPassword());
    }

    @Test
    public void testChangePassword_NewPasswordTooShort() throws Exception {
        // Given
        ChangePasswordRequest shortPasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword("123") // Too short (< 6 characters)
                .build();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("123", currentUser.getPassword())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Password terlalu pendek")))
                .andExpect(jsonPath("$.message", is("Password harus memiliki minimal 6 karakter")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).matches("oldPassword123", currentUser.getPassword());
        verify(passwordEncoder, times(1)).matches("123", currentUser.getPassword());
    }

    @Test
    public void testChangePassword_InvalidInput_EmptyOldPassword() throws Exception {
        // Given
        ChangePasswordRequest invalidRequest = ChangePasswordRequest.builder()
                .oldPassword("") // Empty old password
                .newPassword("newPassword456")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangePassword_InvalidInput_EmptyNewPassword() throws Exception {
        // Given
        ChangePasswordRequest invalidRequest = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword("") // Empty new password
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangePassword_GetCurrentUserException() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenThrow(new RuntimeException("Authentication error"));

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Terjadi kesalahan sistem")))
                .andExpect(jsonPath("$.message", is("Silakan coba lagi nanti")));

        verify(userService, times(1)).getCurrentUser();
    }

    // ===============================================
    // Test Cases for POST /api/auth/validate-password
    // ===============================================

    @Test
    public void testValidatePassword_ValidPassword() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "validPassword123");

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(true)));
    }

    @Test
    public void testValidatePassword_EmptyPassword() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "");

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(false)))
                .andExpect(jsonPath("$.message", is("Password tidak boleh kosong")));
    }

    @Test
    public void testValidatePassword_NullPassword() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", null);

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(false)))
                .andExpect(jsonPath("$.message", is("Password tidak boleh kosong")));
    }

    @Test
    public void testValidatePassword_TooShort() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "123"); // Less than 6 characters

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(false)))
                .andExpect(jsonPath("$.message", is("Password harus memiliki minimal 6 karakter")));
    }

    @Test
    public void testValidatePassword_ExactMinLength() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "123456"); // Exactly 6 characters

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(true)));
    }

    @Test
    public void testValidatePassword_WithSpecialCharacters() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "Test@123!");

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(true)));
    }

    @Test
    public void testValidatePassword_OnlyWhitespace() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "   "); // Only whitespace

        // When & Then
        mockMvc.perform(post("/api/auth/validate-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isValid", is(false)))
                .andExpect(jsonPath("$.message", is("Password tidak boleh kosong")));
    }

    // ===============================================
    // Additional Edge Case Tests
    // ===============================================

    @Test
    public void testChangePassword_ValidMinimumLengthPassword() throws Exception {
        // Given
        ChangePasswordRequest minLengthRequest = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword("123456") // Exactly 6 characters
                .build();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("123456", currentUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minLengthRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Password berhasil diubah")))
                .andExpect(jsonPath("$.status", is("success")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).encode("123456");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_LongPassword() throws Exception {
        // Given
        String longPassword = "thisIsAVeryLongPasswordWithMoreThan50CharactersToTestLongPasswords123";
        ChangePasswordRequest longPasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("oldPassword123")
                .newPassword(longPassword)
                .build();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(longPassword, currentUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(longPassword)).thenReturn("$2a$10$encodedLongPassword");
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Password berhasil diubah")))
                .andExpect(jsonPath("$.status", is("success")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).encode(longPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_PasswordEncoderException() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.matches("oldPassword123", currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword456", currentUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword456")).thenThrow(new RuntimeException("Encoding error"));

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Terjadi kesalahan sistem")))
                .andExpect(jsonPath("$.message", is("Silakan coba lagi nanti")));

        verify(userService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).matches("oldPassword123", currentUser.getPassword());
        verify(passwordEncoder, times(1)).matches("newPassword456", currentUser.getPassword());
        verify(passwordEncoder, times(1)).encode("newPassword456");
    }
}