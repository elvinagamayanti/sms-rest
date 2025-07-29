// package com.sms.unit.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.sms.dto.UserDto;
// import com.sms.controller.UserController;
// import com.sms.dto.SimpleUserDto;
// import com.sms.entity.User;
// import com.sms.entity.Satker;
// import com.sms.entity.Role;
// import com.sms.service.UserService;
// import com.sms.service.SatkerService;
// import com.sms.repository.RoleRepository;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import com.sms.entity.Direktorat;
// import com.sms.mapper.UserMapper;
// import com.sms.repository.UserRepository;

// @ExtendWith(MockitoExtension.class)
// class UserControllerTest {

// @Mock
// UserRepository userRepository;

// @InjectMocks
// UserService userService;

// private static User user = null;

// @BeforeAll
// public static void init() {
// System.out.println("BeforeAll");
// user = new User();
// user.setId(1L);
// user.setName("John Doe");
// user.setEmail("johndoe@bps.go.id");
// user.setNip("1234567890");
// user.setPassword("password");
// user.setIsActive(true);
// user.setRoles(Arrays.asList(
// new Role(1L, "ROLE_USER", java.util.Collections.emptyList()),
// new Role(2L, "ROLE_ADMIN", java.util.Collections.emptyList())));

// Satker satker = new Satker();
// satker.setId(1L);
// satker.setName("Satker 1");
// user.setSatker(satker);

// Direktorat direktorat = new Direktorat();
// direktorat.setId(1L);
// direktorat.setName("Direktorat 1");
// user.setDirektorat(direktorat);
// }

// @BeforeEach
// public void initEachTest() {
// System.out.println("BeforeEach");
// }

// @Test
// void testGetUserById() throws Exception {
// when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

// UserDto userDto = UserMapper.mapToUserDto(user);
// assertNotNull(userDto);
// assertEquals("John Doe", userDto.getFirstName() + " " +
// userDto.getLastName());
// }

// @Test
// void testGetAllUsers() throws Exception {
// when(userRepository.findAll()).thenReturn(Arrays.asList(user));

// List<UserDto> userDtos =
// userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
// assertNotNull(userDtos);
// assertEquals(1, userDtos.size());
// assertEquals("John Doe", userDtos.get(0).getFirstName() + " " +
// userDtos.get(0).getLastName());
// }

// @Test
// void testCreateUser() throws Exception {
// UserDto userDto = UserMapper.mapToUserDto(user);
// when(userRepository.save(any(User.class))).thenReturn(user);

// userService.saveUser(userDto);
// verify(userRepository, times(1)).save(any(User.class));
// }

// }
