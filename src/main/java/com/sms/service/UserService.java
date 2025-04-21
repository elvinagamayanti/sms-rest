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

    // New methods for role management
    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);

    boolean hasRole(User user, String roleName);
}