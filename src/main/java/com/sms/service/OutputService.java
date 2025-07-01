/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

import com.sms.dto.OutputDto;

/**
 *
 * @author pinaa
 */
public interface OutputService {
    List<OutputDto> ambilDaftarOutput();

    void perbaruiDataOutput(OutputDto outputDto);

    void hapusDataOutput(Long outputId);

    void simpanDataOutput(OutputDto outputDto);

    OutputDto cariOutputById(Long id);

    OutputDto patchOutput(Long outputId, Map<String, Object> updates);
}