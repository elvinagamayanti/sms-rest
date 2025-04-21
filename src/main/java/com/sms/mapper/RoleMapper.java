/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.RoleDto;
import com.sms.entity.Role;

/**
 *
 * @author pinaa
 */
public class RoleMapper {

    public static RoleDto mapToRoleDto(Role role) {
        RoleDto roleDto = RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
        return roleDto;
    }

    public static Role mapToRole(RoleDto roleDto) {
        Role role = Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
        return role;
    }
}