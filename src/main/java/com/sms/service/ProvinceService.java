/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

import com.sms.dto.ProvinceDto;

/**
 *
 * @author pinaa
 */
public interface ProvinceService {
    List<ProvinceDto> ambilDaftarProvinsi();

    void perbaruiDataProvinsi(ProvinceDto provinceDto);

    void hapusDataProvinsi(Long provinceId);

    void simpanDataProvinsi(ProvinceDto provinceDto);

    ProvinceDto cariProvinceById(Long id);

    ProvinceDto cariProvinceByCode(String code);

    ProvinceDto patchProvince(Long provinceId, Map<String, Object> updates);
}