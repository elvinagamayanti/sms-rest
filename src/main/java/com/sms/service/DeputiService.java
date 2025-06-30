package com.sms.service;

import java.util.List;

import com.sms.dto.DeputiDto;
import com.sms.entity.User;

/**
 * Service interface for Deputi
 * 
 * @author pinaa
 */
public interface DeputiService {
    List<DeputiDto> ambilDaftarDeputi();

    void perbaruiDataDeputi(DeputiDto deputiDto);

    void hapusDataDeputi(Long deputiId);

    void simpanDataDeputi(DeputiDto deputiDto);

    DeputiDto cariDeputiById(Long id);

    DeputiDto cariDeputiByCode(String code);

    List<User> getUsersByDeputiId(Long deputiId);
}