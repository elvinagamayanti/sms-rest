/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.SatkerDto;
import com.sms.dto.SimpleSatkerDto;
import com.sms.entity.Satker;

/**
 *
 * @author pinaa
 */
public class SatkerMapper {

    public static SatkerDto mapToSatkerDto(Satker satker) {
        SatkerDto satkerDto = SatkerDto.builder()
                .id(satker.getId())
                .name(satker.getName())
                .code(satker.getCode())
                .address(satker.getAddress())
                .number(satker.getNumber())
                .email(satker.getEmail())
                .province(satker.getProvince())
                .isProvince(satker.getIsProvince())
                .createdOn(satker.getCreatedOn())
                .updatedOn(satker.getUpdatedOn())
                .build();
        return satkerDto;
    }

    public static Satker mapToSatker(SatkerDto satkerDto) {
        Satker satker = Satker.builder()
                .id(satkerDto.getId())
                .name(satkerDto.getName())
                .code(satkerDto.getCode())
                .address(satkerDto.getAddress())
                .number(satkerDto.getNumber())
                .email(satkerDto.getEmail())
                .province(satkerDto.getProvince())
                .isProvince(satkerDto.getIsProvince())
                .createdOn(satkerDto.getCreatedOn())
                .updatedOn(satkerDto.getUpdatedOn())
                .build();
        return satker;
    }

    public static SimpleSatkerDto mapToSimpleSatkerDto(Satker satker) {
        return SimpleSatkerDto.builder()
                .id(satker.getId())
                .name(satker.getName())
                .code(satker.getCode())
                .address(satker.getAddress())
                .number(satker.getNumber())
                .email(satker.getEmail())
                .isProvince(satker.getIsProvince())
                .createdOn(satker.getCreatedOn())
                .updatedOn(satker.getUpdatedOn())
                .provinceCode(satker.getProvince() != null ? satker.getProvince().getCode() : null)
                .provinceName(satker.getProvince() != null ? satker.getProvince().getName() : null)
                .build();
    }

    public static SimpleSatkerDto mapSatkerDtoToSimpleSatkerDto(SatkerDto satkerDto) {
        return SimpleSatkerDto.builder()
                .id(satkerDto.getId())
                .name(satkerDto.getName())
                .code(satkerDto.getCode())
                .address(satkerDto.getAddress())
                .number(satkerDto.getNumber())
                .email(satkerDto.getEmail())
                .isProvince(satkerDto.getIsProvince())
                .createdOn(satkerDto.getCreatedOn())
                .updatedOn(satkerDto.getUpdatedOn())
                .provinceCode(satkerDto.getProvince() != null ? satkerDto.getProvince().getCode() : null)
                .provinceName(satkerDto.getProvince() != null ? satkerDto.getProvince().getName() : null)
                .build();
    }
}