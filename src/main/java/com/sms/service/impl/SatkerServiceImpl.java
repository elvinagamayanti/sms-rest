/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.SatkerDto;
import com.sms.entity.Province;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.SatkerMapper;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.SatkerService;

/**
 *
 * @author pinaa
 */
@Service
public class SatkerServiceImpl implements SatkerService {
    private SatkerRepository satkerRepository;
    private UserRepository userRepository;
    private ProvinceRepository provinceRepository;

    public SatkerServiceImpl(SatkerRepository satkerRepository, UserRepository userRepository,
            ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
        this.satkerRepository = satkerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<SatkerDto> ambilDaftarSatker() {
        List<Satker> satkers = this.satkerRepository.findAll();
        List<SatkerDto> satkerDtos = satkers.stream()
                .map((satker) -> (SatkerMapper.mapToSatkerDto(satker)))
                .collect(Collectors.toList());
        return satkerDtos;
    }

    @Override
    public void hapusDataSatker(Long satkerId) {
        satkerRepository.deleteById(satkerId);
    }

    @Override
    public void perbaruiDataSatker(SatkerDto satkerDto) {
        Satker satker = SatkerMapper.mapToSatker(satkerDto);
        System.out.println(satkerDto);
        satkerRepository.save(satker);
    }

    @Override
    public void simpanDataSatker(SatkerDto satkerDto) {
        Satker satker = SatkerMapper.mapToSatker(satkerDto);

        // Extract the first 2 digits of the satker code if code has at least 2
        // characters
        if (satkerDto.getCode() != null && satkerDto.getCode().length() >= 2) {
            String provinceCode = satkerDto.getCode().substring(0, 2);

            // Find the province by code
            Province province = provinceRepository.findByCode(provinceCode)
                    .orElseThrow(() -> new RuntimeException("Province not found with code: " + provinceCode));

            // Set the province to the satker
            satker.setProvince(province);
        }

        satkerRepository.save(satker);
    }

    @Override
    public SatkerDto cariSatkerById(Long id) {
        Satker satker = satkerRepository.findById(id).get();
        return SatkerMapper.mapToSatkerDto(satker);
    }

    @Override
    public List<User> getUsersBySatkerId(Long satkerId) {
        return userRepository.findAllUsersBySatkerId(satkerId);
    }

    @Override
    public SatkerDto patchSatker(Long satkerId, Map<String, Object> updates) {
        final Satker[] satkerHolder = new Satker[1];
        satkerHolder[0] = satkerRepository.findById(satkerId)
                .orElseThrow(() -> new RuntimeException("Satker not found with id: " + satkerId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        satkerHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null) {
                        satkerHolder[0].setCode((String) value);
                        // Update province based on new code
                        String newCode = (String) value;
                        if (newCode.length() >= 2) {
                            String provinceCode = newCode.substring(0, 2);
                            Province province = provinceRepository.findByCode(provinceCode)
                                    .orElse(null);
                            satkerHolder[0].setProvince(province);
                        }
                    }
                }
                case "address" -> {
                    if (value != null)
                        satkerHolder[0].setAddress((String) value);
                }
                case "number" -> {
                    if (value != null)
                        satkerHolder[0].setNumber((String) value);
                }
                case "email" -> {
                    if (value != null)
                        satkerHolder[0].setEmail((String) value);
                }
                case "isProvince" -> {
                    if (value != null)
                        satkerHolder[0].setIsProvince((Boolean) value);
                }
                case "province" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> provinceData = (Map<String, Object>) value;
                        String provinceCode = (String) provinceData.get("code");
                        Province province = provinceRepository.findByCode(provinceCode)
                                .orElseThrow(
                                        () -> new RuntimeException("Province not found with code: " + provinceCode));
                        satkerHolder[0].setProvince(province);
                    }
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        satkerHolder[0] = satkerRepository.save(satkerHolder[0]);
        return SatkerMapper.mapToSatkerDto(satkerHolder[0]);
    }
}