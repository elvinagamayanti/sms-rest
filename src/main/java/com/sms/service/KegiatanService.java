/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Kegiatan;

/**
 *
 * @author pinaa
 */
public interface KegiatanService {
    List<KegiatanDto> ambilDaftarKegiatan();

    void perbaruiDataKegiatan(KegiatanDto kegiatanDto);

    void hapusDataKegiatan(Long kegiatanId);

    KegiatanDto simpanDataKegiatan(KegiatanDto kegiatanDto);

    KegiatanDto cariKegiatanById(Long id);

    Kegiatan findKegiatanById(Long id);
}