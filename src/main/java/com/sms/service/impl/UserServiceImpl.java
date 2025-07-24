package com.sms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.springframework.transaction.annotation.Transactional;

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

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
    @Transactional
    public void saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);

        Satker satker = satkerRepository.findById(userDto.getSatker().getId())
                .orElseThrow(() -> new RuntimeException("Satker not found with id: " + userDto.getSatker().getId()));

        user.setSatker(satker);
        user.setPassword(passwordEncoder.encode(userDto.getNip()));
        user.setIsActive(userDto.getIsActive() != null ? userDto.getIsActive() : true);

        // HIERARCHICAL ROLE ASSIGNMENT LOGIC
        Role defaultRole = determineDefaultRoleForNewUser(satker);
        if (defaultRole == null) {
            defaultRole = checkRoleExist(); // fallback to ROLE_USER
        }

        user.setRoles(Arrays.asList(defaultRole));
        userRepository.save(user);

        logger.info("User {} created with role {} for satker {}",
                user.getName(), defaultRole.getName(), satker.getName());
    }

    /**
     * Determines default role for new user based on current user's role and target
     * satker
     */
    private Role determineDefaultRoleForNewUser(Satker targetSatker) {
        User currentUser = getUserLogged();
        if (currentUser == null) {
            logger.warn("Cannot determine current user, using default role");
            return checkRoleExist();
        }

        String currentUserRole = getCurrentUserHighestRole();

        switch (currentUserRole) {
            case "ROLE_SUPERADMIN":
                return determineRoleForSuperadmin(targetSatker);
            case "ROLE_ADMIN_PUSAT":
                return findOrCreateRole("ROLE_OPERATOR_PUSAT");
            case "ROLE_ADMIN_PROVINSI":
                return findOrCreateRole("ROLE_OPERATOR_PROVINSI");
            case "ROLE_ADMIN_SATKER":
                return findOrCreateRole("ROLE_OPERATOR_SATKER");
            default:
                logger.warn("Current user role {} cannot create users, using default", currentUserRole);
                return checkRoleExist();
        }
    }

    /**
     * Specific logic for SUPERADMIN role assignment based on target satker
     */
    private Role determineRoleForSuperadmin(Satker targetSatker) {
        String satkerCode = targetSatker.getCode();
        Boolean isProvince = targetSatker.getIsProvince();

        logger.debug("Determining role for SUPERADMIN - Satker code: {}, isProvince: {}",
                satkerCode, isProvince);

        // BPS Pusat (kode 0000)
        if ("0000".equals(satkerCode)) {
            logger.info("Assigning ROLE_ADMIN_PUSAT for satker code 0000");
            return findOrCreateRole("ROLE_ADMIN_PUSAT");
        }

        // Satker Provinsi (isProvince = true dan kode bukan 0000)
        if (Boolean.TRUE.equals(isProvince) && !"0000".equals(satkerCode)) {
            logger.info("Assigning ROLE_ADMIN_PROVINSI for province satker {}", satkerCode);
            return findOrCreateRole("ROLE_ADMIN_PROVINSI");
        }

        // Satker Daerah (isProvince = false)
        if (Boolean.FALSE.equals(isProvince)) {
            logger.info("Assigning ROLE_ADMIN_SATKER for regional satker {}", satkerCode);
            return findOrCreateRole("ROLE_ADMIN_SATKER");
        }

        // Default fallback
        logger.warn("Cannot determine role for satker {} with isProvince {}, using default",
                satkerCode, isProvince);
        return findOrCreateRole("ROLE_ADMIN_SATKER");
    }

    /**
     * Find existing role or create if not exists
     */
    private Role findOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
            logger.info("Created new role: {}", roleName);
        }
        return role;
    }

    // ====================================
    // NEW METHODS for Hierarchical Role Assignment
    // ====================================

    @Override
    public List<UserDto> findUsersBySatkerId(Long satkerId) {
        List<User> users = userRepository.findAllUsersBySatkerId(satkerId);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByProvinceCode(String provinceCode) {
        List<User> users = userRepository.findByProvinceCode(provinceCode);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllUsersFiltered() {
        User currentUser = getUserLogged();
        if (currentUser == null) {
            throw new SecurityException("User tidak terautentikasi");
        }

        String userRole = getCurrentUserHighestRole();

        switch (userRole) {
            case "ROLE_SUPERADMIN":
            case "ROLE_ADMIN_PUSAT":
            case "ROLE_OPERATOR_PUSAT":
                return findAllUsers(); // No filtering

            case "ROLE_ADMIN_PROVINSI":
            case "ROLE_OPERATOR_PROVINSI":
                return findUsersByProvinceScope(currentUser);

            case "ROLE_ADMIN_SATKER":
            case "ROLE_OPERATOR_SATKER":
                return findUsersBySatkerScope(currentUser);

            default:
                throw new SecurityException("Role tidak dikenali");
        }
    }

    private List<UserDto> findUsersByProvinceScope(User currentUser) {
        if (currentUser.getSatker() == null) {
            throw new RuntimeException("User tidak memiliki satker");
        }

        String provinceCode = extractProvinceCodeFromSatker(currentUser.getSatker().getCode());
        return findUsersByProvinceCode(provinceCode);
    }

    private List<UserDto> findUsersBySatkerScope(User currentUser) {
        if (currentUser.getSatker() == null) {
            throw new RuntimeException("User tidak memiliki satker");
        }

        return findUsersBySatkerId(currentUser.getSatker().getId());
    }

    @Override
    public boolean canCreateUserInSatker(Long targetSatkerId) {
        User currentUser = getUserLogged();
        if (currentUser == null)
            return false;

        String currentUserRole = getCurrentUserHighestRole();
        Satker targetSatker = satkerRepository.findById(targetSatkerId)
                .orElseThrow(() -> new RuntimeException("Satker not found"));

        switch (currentUserRole) {
            case "ROLE_SUPERADMIN":
                return true; // Can create anywhere

            case "ROLE_ADMIN_PUSAT":
                // Can only create in pusat satkers (code starts with 00)
                return targetSatker.getCode().startsWith("00");

            case "ROLE_ADMIN_PROVINSI":
                // Can only create in same province
                return isSameProvince(currentUser.getSatker().getCode(), targetSatker.getCode());

            case "ROLE_ADMIN_SATKER":
                // Can only create in same satker
                return currentUser.getSatker().getId().equals(targetSatkerId);

            default:
                return false;
        }
    }

    @Override
    public boolean canManageUser(Long targetUserId) {
        User currentUser = getUserLogged();
        if (currentUser == null)
            return false;

        User targetUser = findUserById(targetUserId);
        String currentUserRole = getCurrentUserHighestRole();

        switch (currentUserRole) {
            case "ROLE_SUPERADMIN":
                return true; // Can manage all users

            case "ROLE_ADMIN_PUSAT":
                // Can manage users in pusat satkers
                return targetUser.getSatker().getCode().startsWith("00");

            case "ROLE_ADMIN_PROVINSI":
                // Can manage users in same province
                return isSameProvince(currentUser.getSatker().getCode(),
                        targetUser.getSatker().getCode());

            case "ROLE_ADMIN_SATKER":
                // Can manage users in same satker
                return currentUser.getSatker().getId().equals(targetUser.getSatker().getId());

            default:
                return false;
        }
    }

    @Override
    public String getUserHighestRole(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .filter(roleName -> roleName.startsWith("ROLE_"))
                .min(Comparator.comparing(this::getRolePriority))
                .orElse("ROLE_USER");
    }

    @Override
    public String getCurrentUserHighestRole() {
        User currentUser = getUserLogged();
        if (currentUser == null)
            return "ROLE_USER";

        return getUserHighestRole(currentUser);
    }

    @Override
    public List<String> getManageableRoles() {
        String currentUserRole = getCurrentUserHighestRole();
        Integer currentPriority = getRolePriority(currentUserRole);

        return Arrays.asList(
                "ROLE_SUPERADMIN", "ROLE_ADMIN_PUSAT", "ROLE_OPERATOR_PUSAT",
                "ROLE_ADMIN_PROVINSI", "ROLE_OPERATOR_PROVINSI",
                "ROLE_ADMIN_SATKER", "ROLE_OPERATOR_SATKER", "ROLE_USER").stream()
                .filter(role -> getRolePriority(role) >= currentPriority)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().contains("ADMIN"));
    }

    @Override
    public boolean isUserOperator(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().contains("OPERATOR"));
    }

    // ====================================
    // GEOGRAPHIC UTILITY METHODS
    // ====================================

    @Override
    public String extractProvinceCodeFromSatker(String satkerCode) {
        if (satkerCode == null || satkerCode.length() < 2) {
            throw new IllegalArgumentException("Invalid satker code");
        }
        return satkerCode.substring(0, 2);
    }

    @Override
    public boolean isSameProvince(String satkerCode1, String satkerCode2) {
        try {
            return extractProvinceCodeFromSatker(satkerCode1)
                    .equals(extractProvinceCodeFromSatker(satkerCode2));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isBPSPusat(String satkerCode) {
        return "0000".equals(satkerCode);
    }

    @Override
    public boolean isBPSProvinsi(String satkerCode) {
        return satkerCode != null &&
                satkerCode.endsWith("00") &&
                !satkerCode.equals("0000");
    }

    @Override
    public boolean isBPSKabKota(String satkerCode) {
        return satkerCode != null &&
                !satkerCode.endsWith("00") &&
                !satkerCode.startsWith("00");
    }

    /**
     * Role priority for hierarchy (lower number = higher priority)
     */
    private Integer getRolePriority(String roleName) {
        switch (roleName) {
            case "ROLE_SUPERADMIN":
                return 1;
            case "ROLE_ADMIN_PUSAT":
                return 2;
            case "ROLE_OPERATOR_PUSAT":
                return 3;
            case "ROLE_ADMIN_PROVINSI":
                return 4;
            case "ROLE_OPERATOR_PROVINSI":
                return 5;
            case "ROLE_ADMIN_SATKER":
                return 6;
            case "ROLE_OPERATOR_SATKER":
                return 7;
            case "ROLE_USER":
                return 99;
            default:
                return 100;
        }
    }

    private Role checkRoleExist() {
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = new Role();
            role.setName("ROLE_USER");
            role = roleRepository.save(role);
        }
        return role;
    }

    // private Role checkRoleExist() {
    // Role role = new Role();
    // role.setName("ROLE_USER");
    // return roleRepository.save(role);
    // }

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

    @Override
    public List<Long> getAvailableSatkerIdsForUserCreation() {
        User currentUser = getUserLogged();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        String currentUserRole = getCurrentUserHighestRole();

        switch (currentUserRole) {
            case "ROLE_SUPERADMIN":
                // Superadmin bisa create user di semua satker
                return satkerRepository.findAll().stream()
                        .map(Satker::getId)
                        .collect(Collectors.toList());

            case "ROLE_ADMIN_PUSAT":
                // Admin pusat hanya bisa create di satker pusat (code starts with "00")
                return satkerRepository.findPusatSatkers().stream()
                        .map(Satker::getId)
                        .collect(Collectors.toList());

            case "ROLE_ADMIN_PROVINSI":
                // Admin provinsi hanya bisa create di satker dalam provinsi yang sama
                String provinceCode = extractProvinceCodeFromSatker(currentUser.getSatker().getCode());
                return satkerRepository.findByCodeStartingWith(provinceCode).stream()
                        .map(Satker::getId)
                        .collect(Collectors.toList());

            case "ROLE_ADMIN_SATKER":
                // Admin satker hanya bisa create di satker sendiri
                return Arrays.asList(currentUser.getSatker().getId());

            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<UserDto> getUsersUnderManagement() {
        User currentUser = getUserLogged();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        String currentUserRole = getCurrentUserHighestRole();

        switch (currentUserRole) {
            case "ROLE_SUPERADMIN":
                // Superadmin bisa manage semua user
                return findAllUsers();

            case "ROLE_ADMIN_PUSAT":
                // Admin pusat bisa manage user di satker pusat
                return findAllUsers().stream()
                        .filter(userDto -> {
                            try {
                                User user = findUserById(userDto.getId());
                                return user.getSatker().getCode().startsWith("00");
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

            case "ROLE_ADMIN_PROVINSI":
                // Admin provinsi bisa manage user dalam provinsi yang sama
                String provinceCode = extractProvinceCodeFromSatker(currentUser.getSatker().getCode());
                return findUsersByProvinceCode(provinceCode);

            case "ROLE_ADMIN_SATKER":
                // Admin satker bisa manage user dalam satker yang sama
                return findUsersBySatkerId(currentUser.getSatker().getId());

            default:
                return new ArrayList<>();
        }
    }

    @Override
    public boolean canAssignRole(Long targetUserId, Long roleId) {
        User currentUser = getUserLogged();
        if (currentUser == null)
            return false;

        User targetUser = findUserById(targetUserId);
        Role targetRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String currentUserRole = getCurrentUserHighestRole();
        String targetRoleName = targetRole.getName();

        // Check if current user can manage target user
        if (!canManageUser(targetUserId)) {
            logger.warn("User {} cannot manage user {}", currentUser.getId(), targetUserId);
            return false;
        }

        // Check if current user can assign this specific role
        Integer currentUserPriority = getRolePriority(currentUserRole);
        Integer targetRolePriority = getRolePriority(targetRoleName);

        // User can only assign roles with equal or lower priority (higher number)
        boolean canAssign = currentUserPriority <= targetRolePriority;

        if (!canAssign) {
            logger.warn("User with role {} (priority {}) cannot assign role {} (priority {})",
                    currentUserRole, currentUserPriority, targetRoleName, targetRolePriority);
        }

        return canAssign;
    }

    @Override
    public Long countUsersByRole(String roleName) {
        try {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                return 0L;
            }

            List<User> users = userRepository.findAllUsersByRoleId(role.getId());
            return (long) users.size();
        } catch (Exception e) {
            logger.error("Error counting users by role {}: {}", roleName, e.getMessage());
            return 0L;
        }
    }

    @Override
    public List<UserDto> findUsersByRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // Get all roles by names
            List<Role> roles = new ArrayList<>();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }

            if (roles.isEmpty()) {
                return new ArrayList<>();
            }

            // Get users with any of these roles
            List<User> allUsers = userRepository.findAll();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(userRole -> roles.stream()
                                    .anyMatch(targetRole -> targetRole.getId().equals(userRole.getId()))))
                    .collect(Collectors.toList());

            return filteredUsers.stream()
                    .map(UserMapper::mapToUserDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error finding users by roles {}: {}", roleNames, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getUserStatisticsForCurrentScope() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            List<UserDto> scopedUsers = findAllUsersFiltered();

            // Basic counts
            statistics.put("totalUsers", scopedUsers.size());
            statistics.put("activeUsers", scopedUsers.stream()
                    .mapToLong(user -> Boolean.TRUE.equals(user.getIsActive()) ? 1 : 0)
                    .sum());
            statistics.put("inactiveUsers", scopedUsers.stream()
                    .mapToLong(user -> Boolean.FALSE.equals(user.getIsActive()) ? 1 : 0)
                    .sum());

            // Role distribution
            Map<String, Long> roleDistribution = new HashMap<>();
            for (UserDto userDto : scopedUsers) {
                try {
                    User user = findUserById(userDto.getId());
                    String highestRole = getUserHighestRole(user);
                    roleDistribution.put(highestRole,
                            roleDistribution.getOrDefault(highestRole, 0L) + 1);
                } catch (Exception e) {
                    logger.warn("Error getting role for user {}: {}", userDto.getId(), e.getMessage());
                }
            }
            statistics.put("roleDistribution", roleDistribution);

            // Admin vs Operator count
            long adminCount = scopedUsers.stream()
                    .mapToLong(userDto -> {
                        try {
                            User user = findUserById(userDto.getId());
                            return isUserAdmin(user) ? 1 : 0;
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum();
            long operatorCount = scopedUsers.stream()
                    .mapToLong(userDto -> {
                        try {
                            User user = findUserById(userDto.getId());
                            return isUserOperator(user) ? 1 : 0;
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum();

            statistics.put("adminCount", adminCount);
            statistics.put("operatorCount", operatorCount);
            statistics.put("otherCount", scopedUsers.size() - adminCount - operatorCount);

            // Current user info
            User currentUser = getUserLogged();
            if (currentUser != null) {
                statistics.put("currentUserRole", getCurrentUserHighestRole());
                statistics.put("currentUserSatker", currentUser.getSatker().getName());
                statistics.put("currentUserScope", determineUserScope(currentUser));
            }

        } catch (Exception e) {
            logger.error("Error generating user statistics: {}", e.getMessage());
            statistics.put("error", "Unable to generate statistics");
        }

        return statistics;
    }

    @Override
    @Transactional
    public Map<String, Object> bulkUpdateUserStatus(List<Long> userIds, Boolean isActive) {
        Map<String, Object> result = new HashMap<>();
        List<Long> successfulUpdates = new ArrayList<>();
        List<Map<String, Object>> failures = new ArrayList<>();

        if (userIds == null || userIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "No user IDs provided");
            return result;
        }

        for (Long userId : userIds) {
            try {
                // Check if current user can manage this user
                if (!canManageUser(userId)) {
                    Map<String, Object> failure = new HashMap<>();
                    failure.put("userId", userId);
                    failure.put("reason", "No permission to manage this user");
                    failures.add(failure);
                    continue;
                }

                User user = findUserById(userId);
                user.setIsActive(isActive);
                userRepository.save(user);

                successfulUpdates.add(userId);
                logger.info("Bulk update: User {} status changed to {}", userId, isActive);

            } catch (Exception e) {
                Map<String, Object> failure = new HashMap<>();
                failure.put("userId", userId);
                failure.put("reason", e.getMessage());
                failures.add(failure);
                logger.error("Error updating user {} status: {}", userId, e.getMessage());
            }
        }

        result.put("success", failures.isEmpty());
        result.put("totalRequested", userIds.size());
        result.put("successfulUpdates", successfulUpdates.size());
        result.put("successfulUserIds", successfulUpdates);
        result.put("failedUpdates", failures.size());
        result.put("failures", failures);
        result.put("message", String.format("Updated %d out of %d users",
                successfulUpdates.size(), userIds.size()));

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> transferUserToSatker(Long userId, Long newSatkerId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Validate current user can manage target user
            if (!canManageUser(userId)) {
                result.put("success", false);
                result.put("message", "No permission to transfer this user");
                return result;
            }

            User user = findUserById(userId);
            Satker currentSatker = user.getSatker();

            Satker newSatker = satkerRepository.findById(newSatkerId)
                    .orElseThrow(() -> new RuntimeException("Target satker not found"));

            // Validate current user can create user in target satker
            if (!canCreateUserInSatker(newSatkerId)) {
                result.put("success", false);
                result.put("message", "No permission to transfer user to target satker");
                return result;
            }

            // Store old info for logging
            String oldSatkerName = currentSatker.getName();
            String oldSatkerCode = currentSatker.getCode();

            // Transfer user
            user.setSatker(newSatker);
            userRepository.save(user);

            result.put("success", true);
            result.put("message", "User successfully transferred");
            result.put("userId", userId);
            result.put("userName", user.getName());
            result.put("fromSatker", Map.of(
                    "id", currentSatker.getId(),
                    "name", oldSatkerName,
                    "code", oldSatkerCode));
            result.put("toSatker", Map.of(
                    "id", newSatker.getId(),
                    "name", newSatker.getName(),
                    "code", newSatker.getCode()));

            logger.info("User {} transferred from satker {} to satker {}",
                    user.getName(), oldSatkerName, newSatker.getName());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Transfer failed: " + e.getMessage());
            logger.error("Error transferring user {} to satker {}: {}", userId, newSatkerId, e.getMessage());
        }

        return result;
    }

    @Override
    public boolean canAccessUserData(Long targetUserId) {
        User currentUser = getUserLogged();
        if (currentUser == null)
            return false;

        try {
            User targetUser = findUserById(targetUserId);
            String currentUserRole = getCurrentUserHighestRole();

            switch (currentUserRole) {
                case "ROLE_SUPERADMIN":
                    return true; // Can access all user data

                case "ROLE_ADMIN_PUSAT":
                case "ROLE_OPERATOR_PUSAT":
                    // Can access all user data (pusat level)
                    return true;

                case "ROLE_ADMIN_PROVINSI":
                case "ROLE_OPERATOR_PROVINSI":
                    // Can access user data in same province
                    return isSameProvince(currentUser.getSatker().getCode(),
                            targetUser.getSatker().getCode());

                case "ROLE_ADMIN_SATKER":
                case "ROLE_OPERATOR_SATKER":
                    // Can access user data in same satker
                    return currentUser.getSatker().getId().equals(targetUser.getSatker().getId());

                default:
                    return false;
            }

        } catch (Exception e) {
            logger.error("Error checking access permission for user {}: {}", targetUserId, e.getMessage());
            return false;
        }
    }

    // ====================================
    // HELPER METHODS
    // ====================================

    /**
     * Determine user scope description for statistics
     */
    private String determineUserScope(User user) {
        String role = getCurrentUserHighestRole();

        switch (role) {
            case "ROLE_SUPERADMIN":
                return "National";
            case "ROLE_ADMIN_PUSAT":
            case "ROLE_OPERATOR_PUSAT":
                return "Central Office";
            case "ROLE_ADMIN_PROVINSI":
            case "ROLE_OPERATOR_PROVINSI":
                String provinceCode = extractProvinceCodeFromSatker(user.getSatker().getCode());
                return "Province " + provinceCode;
            case "ROLE_ADMIN_SATKER":
            case "ROLE_OPERATOR_SATKER":
                return "Satker " + user.getSatker().getCode();
            default:
                return "Limited";
        }
    }
}
