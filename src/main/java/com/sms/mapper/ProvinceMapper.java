/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.ProvinceDto;
import com.sms.entity.Province;

/**
 *
 * @author pinaa
 */
public class ProvinceMapper {

    public static ProvinceDto mapToProvinceDto(Province province) {
        ProvinceDto provinceDto = ProvinceDto.builder()
                .id(province.getId())
                .name(province.getName())
                .code(province.getCode())
                .build();
        return provinceDto;
    }

    public static Province mapToProvince(ProvinceDto provinceDto) {
        Province province = Province.builder()
                .id(provinceDto.getId())
                .name(provinceDto.getName())
                .code(provinceDto.getCode())
                .build();
        return province;
    }
}