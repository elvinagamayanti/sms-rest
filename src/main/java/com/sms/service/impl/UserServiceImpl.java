package com.sms.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sms.dto.UserDto;
import com.sms.entity.Direktorat;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SatkerRepository satkerRepository;
    private DirektoratRepository direktoratRepository;
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            SatkerRepository satkerRepository,
            DirektoratRepository direktoratRepository) {
        this.satkerRepository = satkerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.direktoratRepository = direktoratRepository;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);

        Satker satker = satkerRepository.findById(userDto.getSatker().getId())
                .orElseThrow(() -> new RuntimeException("Satker not found with id: " + userDto.getSatker().getId()));

        user.setSatker(satker);
        user.setPassword(passwordEncoder.encode(userDto.getNip()));
        // Set status - default true jika tidak ada
        user.setIsActive(userDto.getIsActive() != null ? userDto.getIsActive() : true);

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("User belum login atau anonymous");
            return null;
        }

        String currentPrincipalName = authentication.getName();
        logger.info("User login dengan email: {}", currentPrincipalName);

        User user = userRepository.findByEmail(currentPrincipalName);
        if (user == null) {
            logger.error("User dengan email {} tidak ditemukan di database!", currentPrincipalName);
        }

        return user;
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = findUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Avoid duplicate roles
        if (user.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = findUserById(userId);

        if (user.getRoles().size() <= 1) {
            throw new RuntimeException("Cannot remove the last role. User must have at least one role.");
        }

        boolean removed = user.getRoles().removeIf(role -> role.getId().equals(roleId));

        if (removed) {
            userRepository.save(user);
        }
    }

    @Override
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    // Implementation method baru untuk status management
    @Override
    public void activateUser(Long userId) {
        User user = findUserById(userId);
        user.activate();
        userRepository.save(user);
        logger.info("User dengan ID {} telah diaktifkan", userId);
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = findUserById(userId);
        user.deactivate();
        userRepository.save(user);
        logger.info("User dengan ID {} telah dinonaktifkan", userId);
    }

    @Override
    public void updateUserStatus(Long userId, Boolean isActive) {
        User user = findUserById(userId);
        user.setIsActive(isActive);
        userRepository.save(user);
        logger.info("Status user dengan ID {} telah diupdate menjadi {}", userId, isActive ? "aktif" : "non-aktif");
    }

    @Override
    public List<UserDto> findActiveUsers() {
        return findUsersByStatus(true);
    }

    @Override
    public List<UserDto> findInactiveUsers() {
        return findUsersByStatus(false);
    }

    @Override
    public List<UserDto> findUsersByStatus(Boolean isActive) {
        List<User> users = userRepository.findByIsActive(isActive);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void assignDirektoratToUser(Long userId, Long direktoratId) {
        User user = findUserById(userId);
        Direktorat direktorat = direktoratRepository.findById(direktoratId)
                .orElseThrow(() -> new RuntimeException("Direktorat not found with ID: " + direktoratId));

        user.setDirektorat(direktorat);
        userRepository.save(user);
        logger.info("Direktorat {} assigned to user dengan ID {}", direktorat.getName(), userId);
    }

    @Override
    public void removeDirektoratFromUser(Long userId) {
        User user = findUserById(userId);
        user.setDirektorat(null);
        userRepository.save(user);
        logger.info("Direktorat removed from user dengan ID {}", userId);
    }

    @Override
    public List<UserDto> findUsersByDirektoratId(Long direktoratId) {
        List<User> users = userRepository.findAllUsersByDirektoratId(direktoratId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByDeputiId(Long deputiId) {
        List<User> users = userRepository.findAllUsersByDeputiId(deputiId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findActiveUsersByDirektoratId(Long direktoratId) {
        List<User> users = userRepository.findActiveByDirektoratId(direktoratId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findActiveUsersByDeputiId(Long deputiId) {
        List<User> users = userRepository.findActiveByDeputiId(deputiId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByDirektoratCode(String direktoratCode) {
        List<User> users = userRepository.findAllUsersByDirektoratCode(direktoratCode);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByDeputiCode(String deputiCode) {
        List<User> users = userRepository.findAllUsersByDeputiCode(deputiCode);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public Long countUsersByDirektoratId(Long direktoratId) {
        return userRepository.countUsersByDirektoratId(direktoratId);
    }

    @Override
    public Long countUsersByDeputiId(Long deputiId) {
        return userRepository.countUsersByDeputiId(deputiId);
    }

    @Override
    public List<UserDto> findUsersByStatusAndDirektoratId(Boolean isActive, Long direktoratId) {
        List<User> users = userRepository.findByIsActiveAndDirektoratId(isActive, direktoratId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByStatusAndDeputiId(Boolean isActive, Long deputiId) {
        List<User> users = userRepository.findByIsActiveAndDeputiId(isActive, deputiId);
        return users.stream()
                .map((user) -> UserMapper.mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findUserById(userId);

        // Verifikasi password lama
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Password lama tidak valid");
        }

        // Validasi password baru tidak sama dengan password lama
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Password baru tidak boleh sama dengan password lama");
        }

        // Validasi panjang password baru
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password harus memiliki minimal 6 karakter");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed successfully for user ID: {}", userId);
    }

    @Override
    public void changeCurrentUserPassword(String oldPassword, String newPassword) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User tidak terautentikasi");
        }

        changePassword(currentUser.getId(), oldPassword, newPassword);
    }

    @Override
    public void resetUserPassword(Long userId, String newPassword) {
        User user = findUserById(userId);

        // Validasi panjang password baru
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password harus memiliki minimal 6 karakter");
        }

        // Update password tanpa verifikasi password lama (untuk reset oleh admin)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password reset successfully for user ID: {} by admin", userId);
    }

    @Override
    public Map<String, Object> validatePasswordStrength(String password) {
        Map<String, Object> result = new HashMap<>();

        if (password == null || password.trim().isEmpty()) {
            result.put("isValid", false);
            result.put("message", "Password tidak boleh kosong");
            return result;
        }

        // Validasi panjang minimal
        if (password.length() < 6) {
            result.put("isValid", false);
            result.put("message", "Password harus memiliki minimal 6 karakter");
            return result;
        }

        // Validasi kompleksitas
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

        result.put("isValid", true);
        result.put("strength", strengthText);
        result.put("score", strength);
        result.put("hasUpperCase", hasUpperCase);
        result.put("hasLowerCase", hasLowerCase);
        result.put("hasDigit", hasDigit);
        result.put("hasSpecialChar", hasSpecialChar);
        result.put("message", "Password valid");

        return result;
    }

    @Override
    public UserDto patchUser(Long userId, Map<String, Object> updates) {
        final User[] userHolder = new User[1];
        userHolder[0] = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        userHolder[0].setName((String) value);
                }
                case "nip" -> {
                    if (value != null)
                        userHolder[0].setNip((String) value);
                }
                case "email" -> {
                    if (value != null) {
                        // Check if email already exists for another user
                        User existingUser = userRepository.findByEmail((String) value);
                        if (existingUser != null && !existingUser.getId().equals(userId)) {
                            throw new RuntimeException("Email already exists for another user");
                        }
                        userHolder[0].setEmail((String) value);
                    }
                }
                case "isActive" -> {
                    if (value != null)
                        userHolder[0].setIsActive((Boolean) value);
                }
                case "satker" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> satkerData = (Map<String, Object>) value;
                        Long satkerId = Long.valueOf(satkerData.get("id").toString());
                        Satker satker = satkerRepository.findById(satkerId)
                                .orElseThrow(() -> new RuntimeException("Satker not found with id: " + satkerId));
                        userHolder[0].setSatker(satker);
                    }
                }
                case "direktorat" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> direktoratData = (Map<String, Object>) value;
                        Long direktoratId = Long.valueOf(direktoratData.get("id").toString());
                        Direktorat direktorat = direktoratRepository.findById(direktoratId)
                                .orElseThrow(
                                        () -> new RuntimeException("Direktorat not found with id: " + direktoratId));
                        userHolder[0].setDirektorat(direktorat);
                    }
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        userHolder[0] = userRepository.save(userHolder[0]);
        return UserMapper.mapToUserDto(userHolder[0]);
    }
}
