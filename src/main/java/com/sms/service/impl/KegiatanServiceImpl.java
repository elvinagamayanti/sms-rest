/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import com.sms.entity.Kegiatan;
import com.sms.dto.KegiatanDto;
import com.sms.mapper.KegiatanMapper;
import com.sms.repository.KegiatanRepository;
import com.sms.service.KegiatanService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author pinaa
 */
@Service
public class KegiatanServiceImpl implements KegiatanService {
    private KegiatanRepository kegiatanRepository;

    public KegiatanServiceImpl(KegiatanRepository kegiatanRepository) {
        this.kegiatanRepository = kegiatanRepository;
    }

    @Override
    public List<KegiatanDto> ambilDaftarKegiatan() {
        List<Kegiatan> kegiatans = this.kegiatanRepository.findAll();
        List<KegiatanDto> kegiatanDtos = kegiatans.stream()
                .map((kegiatan) -> (KegiatanMapper.mapToKegiatanDto(kegiatan)))
                .collect(Collectors.toList());
        return kegiatanDtos;
    }

    @Override
    public void hapusDataKegiatan(Long kegiatanId) {
        kegiatanRepository.deleteById(kegiatanId);
    }

    @Override
    public void perbaruiDataKegiatan(KegiatanDto kegiatanDto) {
        Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);
        System.out.println(kegiatanDto);
        kegiatanRepository.save(kegiatan);
    }

    @Override
    public void simpanDataKegiatan(KegiatanDto kegiatanDto) {
        Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);
        kegiatanRepository.save(kegiatan);
    }

    @Override
    public KegiatanDto cariKegiatanById(Long id) {
        Kegiatan kegiatan = kegiatanRepository.findById(id).get();
        return KegiatanMapper.mapToKegiatanDto(kegiatan);
    }

    @Override
    public Kegiatan findKegiatanById(Long id) {
        return kegiatanRepository.findById(id).orElseThrow(() -> new RuntimeException("Survey not found"));
    }
}