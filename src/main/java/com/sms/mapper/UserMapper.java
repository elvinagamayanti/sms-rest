/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

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
                .satker(user.getSatker())
                .build();
        return userDto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .name(userDto.getFirstName() + " " + userDto.getLastName())
                .nip(userDto.getNip())
                .email(userDto.getEmail())
                .satker(userDto.getSatker())
                .build();
        return user;
    }
}