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

    /**
     * Assign kegiatan survei ke multiple satker
     * Method ini akan menduplikasi kegiatan yang dibuat di pusat ke satker-satker
     * daerah
     * 
     * @param kegiatanId ID kegiatan master yang akan di-assign
     * @param satkerIds  List ID satker yang akan menerima kegiatan
     * @return Map berisi informasi hasil assign
     */
    Map<String, Object> assignKegiatanToSatkers(Long kegiatanId, List<Long> satkerIds);

    /**
     * Assign kegiatan survei ke satker berdasarkan kode provinsi
     * 
     * @param kegiatanId    ID kegiatan master
     * @param provinceCodes List kode provinsi
     * @return Map berisi informasi hasil assign
     */
    Map<String, Object> assignKegiatanToProvinces(Long kegiatanId, List<String> provinceCodes);

    /**
     * Get kegiatan yang telah di-assign ke satker tertentu
     * 
     * @param satkerId ID satker
     * @return List kegiatan yang di-assign ke satker
     */
    List<KegiatanDto> getAssignedKegiatanBySatker(Long satkerId);

    /**
     * Assign user ke kegiatan yang sudah di-assign ke satker
     * Method ini digunakan oleh satker daerah untuk menugaskan user ke kegiatan
     * 
     * @param kegiatanId ID kegiatan yang akan ditugaskan
     * @param userId     ID user yang akan ditugaskan
     * @return Response dengan informasi hasil assign
     */
    Map<String, Object> assignUserToKegiatan(Long kegiatanId, Long userId);

    /**
     * User claim kegiatan untuk dirinya sendiri
     * 
     * @param kegiatanId ID kegiatan yang akan di-claim
     * @return Response dengan informasi hasil claim
     */
    Map<String, Object> claimKegiatan(Long kegiatanId);

    /**
     * Lepas assignment user dari kegiatan (set user_id = null)
     * 
     * @param kegiatanId ID kegiatan
     * @return Response dengan informasi hasil unassign
     */
    Map<String, Object> unassignUserFromKegiatan(Long kegiatanId);

    /**
     * Get daftar kegiatan yang belum ditugaskan (user_id = null) di satker tertentu
     * 
     * @param satkerId ID satker
     * @return List kegiatan yang belum ditugaskan
     */
    List<KegiatanDto> getUnassignedKegiatanBySatker(Long satkerId);

    /**
     * Get daftar kegiatan yang sudah ditugaskan ke user tertentu
     * 
     * @param userId ID user
     * @return List kegiatan yang sudah ditugaskan ke user
     */
    List<KegiatanDto> getKegiatanByAssignedUser(Long userId);

    /**
     * Transfer kegiatan dari satu user ke user lain dalam satker yang sama
     * 
     * @param kegiatanId ID kegiatan
     * @param fromUserId ID user asal
     * @param toUserId   ID user tujuan
     * @return Response dengan informasi hasil transfer
     */
    Map<String, Object> transferKegiatanToUser(Long kegiatanId, Long fromUserId, Long toUserId);

    /**
     * Get filtered kegiatan list based on current user's scope and role
     * 
     * @return List kegiatan sesuai dengan scope akses
     */
    List<KegiatanDto> findAllKegiatanFiltered();

    /**
     * Check if current user can access specific kegiatan
     * 
     * @param kegiatanId ID kegiatan yang akan diakses
     * @return true jika boleh akses, false jika tidak
     */
    boolean canAccessKegiatan(Long kegiatanId);

    /**
     * Check if current user can modify (edit/delete) specific kegiatan
     * 
     * @param kegiatanId ID kegiatan yang akan dimodifikasi
     * @return true jika boleh modify, false jika tidak
     */
    boolean canModifyKegiatan(Long kegiatanId);

    /**
     * Get kegiatan statistics for current user's scope
     * 
     * @return Map berisi statistik kegiatan
     */
    Map<String, Object> getKegiatanStatisticsForCurrentScope();

    /**
     * Find kegiatan by province scope
     * 
     * @param provinceCode kode provinsi
     * @return List kegiatan dalam provinsi tersebut
     */
    List<KegiatanDto> findKegiatanByProvinceScope(String provinceCode);

    /**
     * Find kegiatan by satker scope
     * 
     * @param satkerId ID satker
     * @return List kegiatan dalam satker tersebut
     */
    List<KegiatanDto> findKegiatanBySatkerScope(Long satkerId);
}