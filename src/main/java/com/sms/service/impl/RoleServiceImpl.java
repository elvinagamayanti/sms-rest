/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.RoleDto;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.mapper.RoleMapper;
import com.sms.repository.RoleRepository;
import com.sms.repository.UserRepository;
import com.sms.service.RoleService;

/**
 *
 * @author pinaa
 */
@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RoleDto> ambilDaftarRole() {
        List<Role> roles = this.roleRepository.findAll();
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> RoleMapper.mapToRoleDto(role))
                .collect(Collectors.toList());
        return roleDtos;
    }

    @Override
    public String hapusDataRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        roleRepository.deleteById(roleId);
        return "Success";
    }

    @Override
    public String perbaruiDataRole(RoleDto roleDto) {
        Role role = RoleMapper.mapToRole(roleDto);
        System.out.println(roleDto);
        roleRepository.save(role);
        return "Success";
    }

    @Override
    public String simpanDataRole(RoleDto roleDto) {
        Role role = RoleMapper.mapToRole(roleDto);
        roleRepository.save(role);
        return "Success";
    }

    @Override
    public RoleDto cariRoleById(Long id) {
        Role role = roleRepository.findById(id).get();
        return RoleMapper.mapToRoleDto(role);
    }

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findAllUsersByRoleId(roleId);
    }

    @Override
    public RoleDto patchRole(Long roleId, Map<String, Object> updates) {
        final Role[] roleHolder = new Role[1];
        roleHolder[0] = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        roleHolder[0].setName((String) value);
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        roleHolder[0] = roleRepository.save(roleHolder[0]);
        return RoleMapper.mapToRoleDto(roleHolder[0]);
    }
}