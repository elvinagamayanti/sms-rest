package com.sms.service;

import java.util.List;

import com.sms.dto.DirektoratDto;
import com.sms.entity.User;

/**
 * Service interface for Direktorat
 * 
 * @author pinaa
 */
public interface DirektoratService {
    List<DirektoratDto> ambilDaftarDirektorat();

    void perbaruiDataDirektorat(DirektoratDto direktoratDto);

    void hapusDataDirektorat(Long direktoratId);

    void simpanDataDirektorat(DirektoratDto direktoratDto);

    DirektoratDto cariDirektoratById(Long id);

    DirektoratDto cariDirektoratByCode(String code);

    List<DirektoratDto> getDirektoratsByDeputiId(Long deputiId);

    List<User> getUsersByDirektoratId(Long direktoratId);
}