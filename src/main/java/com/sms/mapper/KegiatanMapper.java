/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Kegiatan;

/**
 *
 * @author pinaa
 */
public class KegiatanMapper {

    public static KegiatanDto mapToKegiatanDto(Kegiatan kegiatan) {
        KegiatanDto kegiatanDto = KegiatanDto.builder()
                .id(kegiatan.getId())
                .name(kegiatan.getName())
                .code(kegiatan.getCode())
                .budget(kegiatan.getBudget())
                .startDate(kegiatan.getStartDate())
                .endDate(kegiatan.getEndDate())
                .user(kegiatan.getUser())
                .satker(kegiatan.getSatker())
                .program(kegiatan.getProgram())
                .createdOn(kegiatan.getCreatedOn())
                .updatedOn(kegiatan.getUpdatedOn())
                .build();
        return kegiatanDto;
    }

    public static Kegiatan mapToKegiatan(KegiatanDto kegiatanDto) {
        Kegiatan kegiatan = Kegiatan.builder()
                .id(kegiatanDto.getId())
                .name(kegiatanDto.getName())
                .code(kegiatanDto.getCode())
                .budget(kegiatanDto.getBudget())
                .startDate(kegiatanDto.getStartDate())
                .endDate(kegiatanDto.getEndDate())
                .user(kegiatanDto.getUser())
                .satker(kegiatanDto.getSatker())
                .program(kegiatanDto.getProgram())
                .createdOn(kegiatanDto.getCreatedOn())
                .updatedOn(kegiatanDto.getUpdatedOn())
                .build();
        return kegiatan;
    }
}