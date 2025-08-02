package com.sms.unit.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.UserController;
import com.sms.dto.SimpleUserDto;
import com.sms.dto.UserDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.service.SatkerService;
import com.sms.service.UserService;

@ExtendWith(MockitoExtension.class)
// @WebMvcTest(UserController.class)
class UserControllerTest {

        @Mock
        private UserService userService;

        @Mock
        private SatkerService satkerService;

        @InjectMocks
        private UserController userController;

        @Autowired
        private MockMvc mockMvc;

        private ObjectMapper objectMapper;
        private UserDto userDto;
        private SimpleUserDto simpleUserDto;
        User user;

        User user1;
        User user2;
        List<User> userList = new ArrayList<>();

        Role role;
        Satker satker;
        Direktorat direktorat;
        Deputi deputi;
        Province province;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
                objectMapper = new ObjectMapper();

                // Setup test data
                province = Province.builder()
                                .id(1L)
                                .name("Test Province")
                                .code("01")
                                .build();

                satker = Satker.builder()
                                .id(1L)
                                .name("Test Satker")
                                .code("0100")
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

                // userDto = UserMapper.mapToUserDto(user);

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

                // simpleUserDto = UserMapper.mapToSimpleUserDto(user);

                simpleUserDto = SimpleUserDto.builder()
                                .id(1L)
                                .name("Test User")
                                .nip("1234567890")
                                .email("test@email.com")
                                .satkerName(satker.getName())
                                .direktoratName(direktorat.getName())
                                .build();

                user1 = User.builder()
                                .id(2L)
                                .name("Test User1")
                                .nip("1234567890")
                                .email("test@email.com")
                                .password("password123")
                                .isActive(true)
                                .satker(satker)
                                .direktorat(direktorat)
                                .roles(Arrays.asList(role))
                                .build();

                user2 = User.builder()
                                .id(3L)
                                .name("Test User2")
                                .nip("1234567890")
                                .email("test@email.com")
                                .password("password123")
                                .isActive(true)
                                .satker(satker)
                                .direktorat(direktorat)
                                .roles(Arrays.asList(role))
                                .build();

                userList.add(user1);
                userList.add(user2);
        }

        @AfterEach
        void tearDown() {

        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetUserById() throws Exception {
                when(userService.canAccessUserData(2L)).thenReturn(true);
                when(userService.findUserById(2L)).thenReturn(user1);
                this.mockMvc.perform(get("/api/users/2")).andDo(print()).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetAllUsers_Success() throws Exception {
                // Given
                List<UserDto> users = Arrays.asList(userDto);
                when(userService.findAllUsersFiltered()).thenReturn(users);

                // When & Then
                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                                .andExpect(jsonPath("$[0].name").value("Test User")) // firstName + " " + lastName
                                .andExpect(jsonPath("$[0].nip").value(userDto.getNip()))
                                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()))
                                .andExpect(jsonPath("$[0].isActive").value(userDto.getIsActive()));

                verify(userService).findAllUsersFiltered();

        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetUserById_Success() throws Exception {
                // Given
                when(userService.canAccessUserData(1L)).thenReturn(true);
                when(userService.findUserById(1L)).thenReturn(user);

                // When & Then
                mockMvc.perform(get("/api/users/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Test User"));

                verify(userService).findUserById(1L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetUserById_NotFound() throws Exception {
                // Given
                when(userService.canAccessUserData(999L)).thenReturn(true); // Add this line
                when(userService.findUserById(999L)).thenThrow(new RuntimeException("User not found"));

                // When & Then
                mockMvc.perform(get("/api/users/999"))
                                .andExpect(status().isNotFound()); // Changed to isNotFound() - see below

                verify(userService).canAccessUserData(999L);
                verify(userService).findUserById(999L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testCreateUser_Success() throws Exception {
                // Given
                when(userService.findUserByEmail(userDto.getEmail())).thenReturn(null); // User doesn't exist
                when(userService.canCreateUserInSatker(userDto.getSatker().getId())).thenReturn(true); // Can create
                when(userService.getUserLogged()).thenReturn(user1); // Mock current user
                doNothing().when(userService).saveUser(any(UserDto.class));

                // When & Then
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.firstName").value("Test"));

                // Verify all methods were called
                verify(userService).findUserByEmail(userDto.getEmail());
                verify(userService).canCreateUserInSatker(userDto.getSatker().getId());
                verify(userService).saveUser(any(UserDto.class));
                verify(userService).getUserLogged();
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testCreateUser_InvalidData() throws Exception {
                // Given - Create a Satker object to avoid null pointer
                Satker mockSatker = new Satker();
                mockSatker.setId(1L);
                mockSatker.setName("Test Satker");

                UserDto invalidUser = UserDto.builder()
                                .email("invalid-email") // Invalid email format
                                .satker(mockSatker) // Add satker to avoid null pointer
                                .build();

                // When & Then
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testUpdateUser_Success() throws Exception {
                // Given
                when(userService.canManageUser(1L)).thenReturn(true);
                when(userService.patchUser(eq(1L), any(Map.class))).thenReturn(userDto);

                // When & Then
                mockMvc.perform(put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("Test"));

                verify(userService).patchUser(eq(1L), any(Map.class));
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testPatchUser_Success() throws Exception {
                // Given
                Map<String, Object> updates = Map.of("firstName", "Jane");
                when(userService.patchUser(eq(1L), any(Map.class))).thenReturn(userDto);

                // When & Then
                mockMvc.perform(patch("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updates)))
                                .andExpect(status().isOk());

                verify(userService).patchUser(eq(1L), any(Map.class));
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testDeactivateUser_Success() throws Exception {
                // Given
                when(userService.canManageUser(1L)).thenReturn(true);
                doNothing().when(userService).deactivateUser(1L);

                // When & Then
                mockMvc.perform(post("/api/users/1/deactivate"))
                                .andExpect(status().isOk());

                verify(userService).canManageUser(1L);
                verify(userService).deactivateUser(1L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testActivateUser_Success() throws Exception {
                // Given
                when(userService.canManageUser(1L)).thenReturn(true);
                doNothing().when(userService).activateUser(1L);

                // When & Then
                mockMvc.perform(post("/api/users/1/activate"))
                                .andExpect(status().isOk());

                verify(userService).canManageUser(1L);
                verify(userService).activateUser(1L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetUsersBySatkerId_Success() throws Exception {
                // Given
                List<UserDto> users = Arrays.asList(userDto);
                when(userService.findUsersBySatkerId(1L)).thenReturn(users);

                // When & Then
                mockMvc.perform(get("/api/users/satker/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

                verify(userService).findUsersBySatkerId(1L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testGetManageableRoles_Success() throws Exception {
                // Given
                List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
                when(userService.getManageableRoles()).thenReturn(roles);

                // When & Then
                mockMvc.perform(get("/api/users/manageable-roles"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0]").value("ROLE_USER"));

                verify(userService).getManageableRoles();
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testTransferUserToSatker_Success() throws Exception {
                // Given
                Map<String, Object> request = Map.of("newSatkerId", 2L);
                Map<String, Object> response = Map.of("success", true, "message", "Transfer successful");
                when(userService.transferUserToSatker(1L, 2L)).thenReturn(response);

                // When & Then
                mockMvc.perform(post("/api/users/1/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));

                verify(userService).transferUserToSatker(1L, 2L);
        }

        @Test
        @WithMockUser(roles = "SUPERADMIN")
        void testCountUsersByRole_Success() throws Exception {
                // Given
                when(userService.countUsersByRole("ROLE_USER")).thenReturn(5L);

                // When & Then
                mockMvc.perform(get("/api/users/count-by-role?roleName=ROLE_USER"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.roleName").value(5));

                verify(userService).countUsersByRole("ROLE_USER");
        }
}