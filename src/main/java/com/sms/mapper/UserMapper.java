/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.SimpleUserDto;
import com.sms.dto.UserDto;
import com.sms.entity.User;

/**
 *
 * @author pinaa
 */
public class UserMapper {
        public static UserDto mapToUserDto(User user) {
                String[] str = user.getName().split(" ");
                UserDto userDto = UserDto.builder()
                                .id(user.getId())
                                .firstName(str[0])
                                .lastName(str[1])
                                .nip(user.getNip())
                                .email(user.getEmail())
                                .isActive(user.getIsActive())
                                .statusText(user.getStatusText())
                                .satker(user.getSatker())
                                .direktorat(user.getDirektorat())
                                .build();
                return userDto;
        }

        public static User mapToUser(UserDto userDto) {
                User user = User.builder()
                                .id(userDto.getId())
                                .name(userDto.getFirstName() + " " + userDto.getLastName())
                                .nip(userDto.getNip())
                                .email(userDto.getEmail())
                                .isActive(userDto.getIsActive() != null ? userDto.getIsActive() : true)
                                .satker(userDto.getSatker())
                                .direktorat(userDto.getDirektorat())
                                .build();
                return user;
        }

        public static SimpleUserDto mapToSimpleUserDto(User user) {
                return SimpleUserDto.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .nip(user.getNip())
                                .email(user.getEmail())
                                .isActive(user.getIsActive())
                                .satkerId(user.getSatker() != null ? user.getSatker().getId() : null)
                                .satkerName(user.getSatker() != null ? user.getSatker().getName() : null)
                                .direktoratId(user.getDirektorat() != null ? user.getDirektorat().getId() : null)
                                .direktoratName(user.getDirektorat() != null ? user.getDirektorat().getName() : null)
                                .deputiName(user.getDirektorat() != null && user.getDirektorat().getDeputi() != null
                                                ? user.getDirektorat().getDeputi().getName()
                                                : null)
                                .build();
        }

        public static SimpleUserDto mapUserDtoToSimpleUserDto(UserDto userDto) {
                return SimpleUserDto.builder()
                                .id(userDto.getId())
                                .name(userDto.getFirstName() + " " + userDto.getLastName())
                                .nip(userDto.getNip())
                                .email(userDto.getEmail())
                                .isActive(userDto.getIsActive())
                                .satkerId(userDto.getSatker() != null ? userDto.getSatker().getId() : null)
                                .satkerName(userDto.getSatker() != null ? userDto.getSatker().getName() : null)
                                .direktoratId(userDto.getDirektorat() != null ? userDto.getDirektorat().getId() : null)
                                .direktoratName(userDto.getDirektorat() != null ? userDto.getDirektorat().getName()
                                                : null)
                                .deputiName(userDto.getDirektorat() != null
                                                && userDto.getDirektorat().getDeputi() != null
                                                                ? userDto.getDirektorat().getDeputi().getName()
                                                                : null)
                                .build();
        }
}