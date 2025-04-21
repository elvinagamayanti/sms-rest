/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
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
    public void hapusDataRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public void perbaruiDataRole(RoleDto roleDto) {
        Role role = RoleMapper.mapToRole(roleDto);
        System.out.println(roleDto);
        roleRepository.save(role);
    }

    @Override
    public void simpanDataRole(RoleDto roleDto) {
        Role role = RoleMapper.mapToRole(roleDto);
        roleRepository.save(role);
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
}