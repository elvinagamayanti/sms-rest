/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;

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
}