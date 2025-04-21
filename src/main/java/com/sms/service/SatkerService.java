/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import com.sms.dto.SatkerDto;
import java.util.List;

/**
 *
 * @author pinaa
 */
public interface SatkerService {
    List<SatkerDto> ambilDaftarSatker();

    void perbaruiDataSatker(SatkerDto satkerDto);

    void hapusDataSatker(Long satkerId);

    void simpanDataSatker(SatkerDto satkerDto);

    SatkerDto cariSatkerById(Long id);
}