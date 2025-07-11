/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.service;

import java.util.List;
import java.util.Map;

import com.sms.dto.ProgramDto;

/**
 *
 * @author pinaa
 */
public interface ProgramService {
    List<ProgramDto> ambilDaftarProgram();

    void perbaruiDataProgram(ProgramDto programDto);

    void hapusDataProgram(Long programId);

    void simpanDataProgram(ProgramDto programDto);

    ProgramDto cariProgramById(Long id);

    ProgramDto patchProgram(Long programId, Map<String, Object> updates);
}