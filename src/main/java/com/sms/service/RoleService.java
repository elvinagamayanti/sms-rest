/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

import com.sms.dto.RoleDto;
import com.sms.entity.User;

/**
 *
 * @author pinaa
 */
public interface RoleService {
    List<RoleDto> ambilDaftarRole();

    void perbaruiDataRole(RoleDto roleDto);

    void hapusDataRole(Long roleId);

    void simpanDataRole(RoleDto roleDto);

    RoleDto cariRoleById(Long id);

    List<User> getUsersByRoleId(Long roleId);

    RoleDto patchRole(Long roleId, Map<String, Object> updates);

}