/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

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

    KegiatanDto patchKegiatan(Long kegiatanId, Map<String, Object> updates);

    // Get kegiatan by direktorat PJ
    List<KegiatanDto> getKegiatanByDirektoratPJ(Long direktoratId);

    List<KegiatanDto> getKegiatanByDirektoratPJCode(String direktoratCode);

    // Get kegiatan by deputi PJ
    List<KegiatanDto> getKegiatanByDeputiPJ(Long deputiId);

    List<KegiatanDto> getKegiatanByDeputiPJCode(String deputiCode);

    // Get kegiatan by year and direktorat PJ
    List<KegiatanDto> getKegiatanByYearAndDirektoratPJ(int year, Long direktoratId);

    // Statistics methods
    Map<String, Object> getKegiatanStatisticsByDirektorat();

    Map<String, Object> getKegiatanStatisticsByDeputi();

    Map<String, Object> getBudgetStatisticsByDirektorat();

    Map<String, Object> getBudgetStatisticsByDeputi();

    // Utility methods
    List<KegiatanDto> getKegiatanWithoutDirektoratPJ();

    List<KegiatanDto> searchKegiatan(String query);

    List<KegiatanDto> filterKegiatan(Long direktoratId, Integer year, Long programId);

    Map<String, Object> getMonthlyStatistics(int year, Long direktoratId);

    // Admin methods
    void assignDirektoratPJ(Long kegiatanId, Long direktoratId);

    Map<String, Object> syncDirektoratPJFromUser();
}