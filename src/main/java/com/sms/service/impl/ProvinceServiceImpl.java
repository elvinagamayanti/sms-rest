/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.ProvinceDto;
import com.sms.entity.Province;
import com.sms.mapper.ProvinceMapper;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;
import com.sms.service.ProvinceService;

/**
 *
 * @author pinaa
 */
@Service
public class ProvinceServiceImpl implements ProvinceService {
    private ProvinceRepository provinceRepository;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, SatkerRepository satkerRepository) {
        this.provinceRepository = provinceRepository;
    }

    @Override
    public List<ProvinceDto> ambilDaftarProvinsi() {
        List<Province> provinces = this.provinceRepository.findAll();
        List<ProvinceDto> provinceDtos = provinces.stream()
                .map(province -> ProvinceMapper.mapToProvinceDto(province))
                .collect(Collectors.toList());
        return provinceDtos;
    }

    @Override
    public void hapusDataProvinsi(Long provinceId) {
        provinceRepository.deleteById(provinceId);
    }

    @Override
    public void perbaruiDataProvinsi(ProvinceDto provinceDto) {
        Province province = ProvinceMapper.mapToProvince(provinceDto);
        System.out.println(provinceDto);
        provinceRepository.save(province);
    }

    @Override
    public void simpanDataProvinsi(ProvinceDto provinceDto) {
        Province province = ProvinceMapper.mapToProvince(provinceDto);
        provinceRepository.save(province);
    }

    @Override
    public ProvinceDto cariProvinceById(Long id) {
        Province province = provinceRepository.findById(id).get();
        return ProvinceMapper.mapToProvinceDto(province);
    }

    @Override
    public ProvinceDto cariProvinceByCode(String code) {
        Province province = provinceRepository.findByCode(code).get();
        return ProvinceMapper.mapToProvinceDto(province);
    }

    @Override
    public ProvinceDto patchProvince(Long provinceId, Map<String, Object> updates) {
        final Province[] provinceHolder = new Province[1];
        provinceHolder[0] = provinceRepository.findById(provinceId)
                .orElseThrow(() -> new RuntimeException("Province not found with id: " + provinceId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        provinceHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        provinceHolder[0].setCode((String) value);
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        provinceHolder[0] = provinceRepository.save(provinceHolder[0]);
        return ProvinceMapper.mapToProvinceDto(provinceHolder[0]);
    }

}