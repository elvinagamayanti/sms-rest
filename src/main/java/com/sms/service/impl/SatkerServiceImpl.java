/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
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

        Province province = provinceRepository.findById(satkerDto.getProvince().getId())
                .orElseThrow(
                        () -> new RuntimeException("Province not found with id: " + satkerDto.getProvince().getId()));

        satker.setProvince(province);

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
}