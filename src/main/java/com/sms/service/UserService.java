/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

import com.sms.dto.UserDto;
import com.sms.entity.User;

/**
 *
 * @author pinaa
 */
public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    User findUserById(Long id);

    User getCurrentUser();

    User getUserLogged();

    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);

    boolean hasRole(User user, String roleName);

    void activateUser(Long userId);

    void deactivateUser(Long userId);

    void updateUserStatus(Long userId, Boolean isActive);

    List<UserDto> findActiveUsers();

    List<UserDto> findInactiveUsers();

    List<UserDto> findUsersByStatus(Boolean isActive);

    // Assign direktorat to user
    void assignDirektoratToUser(Long userId, Long direktoratId);

    // Remove direktorat from user
    void removeDirektoratFromUser(Long userId);

    // Find users by direktorat
    List<UserDto> findUsersByDirektoratId(Long direktoratId);

    // Find users by deputi
    List<UserDto> findUsersByDeputiId(Long deputiId);

    // Find active users by direktorat
    List<UserDto> findActiveUsersByDirektoratId(Long direktoratId);

    // Find active users by deputi
    List<UserDto> findActiveUsersByDeputiId(Long deputiId);

    // Find users by direktorat code
    List<UserDto> findUsersByDirektoratCode(String direktoratCode);

    // Find users by deputi code
    List<UserDto> findUsersByDeputiCode(String deputiCode);

    // Count users by direktorat
    Long countUsersByDirektoratId(Long direktoratId);

    // Count users by deputi
    Long countUsersByDeputiId(Long deputiId);

    // Find users by status and direktorat
    List<UserDto> findUsersByStatusAndDirektoratId(Boolean isActive, Long direktoratId);

    // Find users by status and deputi
    List<UserDto> findUsersByStatusAndDeputiId(Boolean isActive, Long deputiId);

    // change password methods
    void changePassword(Long userId, String oldPassword, String newPassword);

    // change current user password
    void changeCurrentUserPassword(String oldPassword, String newPassword);

    // reset user password
    void resetUserPassword(Long userId, String newPassword);

    // Validate password strength
    Map<String, Object> validatePasswordStrength(String password);

    // Patch user data
    UserDto patchUser(Long userId, Map<String, Object> updates);

    /**
     * Find users by satker ID (untuk geographic filtering)
     * 
     * @param satkerId ID satker
     * @return List user yang terkait dengan satker tersebut
     */
    List<UserDto> findUsersBySatkerId(Long satkerId);

    /**
     * Find users by province code (untuk provincial filtering)
     * 
     * @param provinceCode 2-digit province code
     * @return List user dalam provinsi tersebut
     */
    List<UserDto> findUsersByProvinceCode(String provinceCode);

    /**
     * Get filtered users based on current user's scope and role
     * Method ini akan otomatis filter berdasarkan geographic scope user yang login
     * 
     * @return List user sesuai dengan scope akses
     */
    List<UserDto> findAllUsersFiltered();

    /**
     * Check if current user can manage (create/edit/delete) target user
     * 
     * @param targetUserId ID user yang akan dimanage
     * @return true jika boleh manage, false jika tidak
     */
    boolean canManageUser(Long targetUserId);

    /**
     * Check if current user can create user in specific satker
     * 
     * @param targetSatkerId ID satker target untuk pembuatan user
     * @return true jika boleh create, false jika tidak
     */
    boolean canCreateUserInSatker(Long targetSatkerId);

    /**
     * Get available satkers for user creation based on current user's role
     * 
     * @return List satker yang bisa digunakan untuk create user
     */
    List<Long> getAvailableSatkerIdsForUserCreation();

    /**
     * Get user's highest role (berdasarkan hierarki priority)
     * 
     * @param user User entity
     * @return String nama role tertinggi
     */
    String getUserHighestRole(User user);

    /**
     * Get current logged user's highest role
     * 
     * @return String nama role tertinggi user yang login
     */
    String getCurrentUserHighestRole();

    /**
     * Check if user has any admin role (ADMIN_PUSAT, ADMIN_PROVINSI, ADMIN_SATKER)
     * 
     * @param user User entity
     * @return true jika user adalah admin, false jika tidak
     */
    boolean isUserAdmin(User user);

    /**
     * Check if user has any operator role (OPERATOR_PUSAT, OPERATOR_PROVINSI,
     * OPERATOR_SATKER)
     * 
     * @param user User entity
     * @return true jika user adalah operator, false jika tidak
     */
    boolean isUserOperator(User user);

    /**
     * Get users under current user's management scope
     * 
     * @return List user yang bisa dikelola oleh current user
     */
    List<UserDto> getUsersUnderManagement();

    /**
     * Validate role assignment permission
     * 
     * @param targetUserId ID user yang akan diberi role
     * @param roleId       ID role yang akan diberikan
     * @return true jika boleh assign role, false jika tidak
     */
    boolean canAssignRole(Long targetUserId, Long roleId);

    /**
     * Get manageable roles for current user
     * 
     * @return List role yang bisa di-assign oleh current user
     */
    List<String> getManageableRoles();

    /**
     * Count users by role name
     * 
     * @param roleName nama role
     * @return jumlah user dengan role tersebut
     */
    Long countUsersByRole(String roleName);

    /**
     * Find users by multiple role names
     * 
     * @param roleNames list nama role
     * @return List user yang memiliki salah satu role tersebut
     */
    List<UserDto> findUsersByRoles(List<String> roleNames);

    /**
     * Get user statistics for current user's scope
     * 
     * @return Map berisi statistik user (total, active, by role, etc.)
     */
    Map<String, Object> getUserStatisticsForCurrentScope();

    /**
     * Bulk activate/deactivate users
     * 
     * @param userIds  List ID user yang akan diupdate
     * @param isActive status yang akan di-set
     * @return Map result dengan success/failed info
     */
    Map<String, Object> bulkUpdateUserStatus(List<Long> userIds, Boolean isActive);

    /**
     * Transfer user management (pindah user ke satker lain)
     * 
     * @param userId      ID user yang akan dipindah
     * @param newSatkerId ID satker tujuan
     * @return Map result operation
     */
    Map<String, Object> transferUserToSatker(Long userId, Long newSatkerId);

    /**
     * Validate if current user can access specific user data
     * 
     * @param targetUserId ID user yang akan diakses
     * @return true jika boleh akses, false jika tidak
     */
    boolean canAccessUserData(Long targetUserId);

    // =======================================
    // UTILITY METHODS untuk Geographic Scope
    // =======================================

    /**
     * Extract province code from satker code
     * 
     * @param satkerCode kode satker (format: 3201)
     * @return province code (format: 32)
     */
    String extractProvinceCodeFromSatker(String satkerCode);

    /**
     * Check if two satkers are in same province
     * 
     * @param satkerCode1 kode satker pertama
     * @param satkerCode2 kode satker kedua
     * @return true jika sama provinsi, false jika tidak
     */
    boolean isSameProvince(String satkerCode1, String satkerCode2);

    /**
     * Check if satker is BPS Pusat
     * 
     * @param satkerCode kode satker
     * @return true jika BPS Pusat (0000), false jika tidak
     */
    boolean isBPSPusat(String satkerCode);

    /**
     * Check if satker is BPS Provinsi
     * 
     * @param satkerCode kode satker
     * @return true jika BPS Provinsi (xx00, tapi bukan 0000), false jika tidak
     */
    boolean isBPSProvinsi(String satkerCode);

    /**
     * Check if satker is BPS Kabupaten/Kota
     * 
     * @param satkerCode kode satker
     * @return true jika BPS Kab/Kota (xxxx, tidak berakhir 00), false jika tidak
     */
    boolean isBPSKabKota(String satkerCode);

}