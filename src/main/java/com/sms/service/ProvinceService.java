/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import com.sms.dto.ProvinceDto;

import java.util.List;

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
    // List<Satker> getSatkersByProvinceCode(String provinceCode);
}