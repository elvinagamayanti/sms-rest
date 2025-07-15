/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.KegiatanDto;
import com.sms.dto.SimpleKegiatanDto;
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
                .output(kegiatan.getOutput())
                .direktoratPenanggungJawab(kegiatan.getDirektoratPenanggungJawab())
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
                .output(kegiatanDto.getOutput())
                .direktoratPenanggungJawab(kegiatanDto.getDirektoratPenanggungJawab())
                .createdOn(kegiatanDto.getCreatedOn())
                .updatedOn(kegiatanDto.getUpdatedOn())
                .build();
        return kegiatan;
    }

    public static SimpleKegiatanDto mapToSimpleKegiatanDto(Kegiatan kegiatan) {
        return SimpleKegiatanDto.builder()
                .id(kegiatan.getId())
                .name(kegiatan.getName())
                .code(kegiatan.getCode())
                .budget(kegiatan.getBudget())
                .startDate(kegiatan.getStartDate())
                .endDate(kegiatan.getEndDate())
                .createdOn(kegiatan.getCreatedOn())
                .updatedOn(kegiatan.getUpdatedOn())
                .userId(kegiatan.getUser() != null ? kegiatan.getUser().getId() : null)
                .userName(kegiatan.getUser() != null ? kegiatan.getUser().getName() : null)
                .satkerId(kegiatan.getSatker() != null ? kegiatan.getSatker().getId() : null)
                .satkerName(kegiatan.getSatker() != null ? kegiatan.getSatker().getName() : null)
                .programId(kegiatan.getProgram() != null ? kegiatan.getProgram().getId() : null)
                .programName(kegiatan.getProgram() != null ? kegiatan.getProgram().getName() : null)
                .outputId(kegiatan.getOutput() != null ? kegiatan.getOutput().getId() : null)
                .outputName(kegiatan.getOutput() != null ? kegiatan.getOutput().getName() : null)
                .direktoratPjId(kegiatan.getDirektoratPenanggungJawab() != null
                        ? kegiatan.getDirektoratPenanggungJawab().getId()
                        : null)
                .direktoratPjName(kegiatan.getDirektoratPenanggungJawab() != null
                        ? kegiatan.getDirektoratPenanggungJawab().getName()
                        : null)
                .build();
    }

    public static SimpleKegiatanDto mapKegiatanDtoToSimpleKegiatanDto(KegiatanDto kegiatanDto) {
        return SimpleKegiatanDto.builder()
                .id(kegiatanDto.getId())
                .name(kegiatanDto.getName())
                .code(kegiatanDto.getCode())
                .budget(kegiatanDto.getBudget())
                .startDate(kegiatanDto.getStartDate())
                .endDate(kegiatanDto.getEndDate())
                .createdOn(kegiatanDto.getCreatedOn())
                .updatedOn(kegiatanDto.getUpdatedOn())
                .userId(kegiatanDto.getUser() != null ? kegiatanDto.getUser().getId() : null)
                .userName(kegiatanDto.getUser() != null ? kegiatanDto.getUser().getName() : null)
                .satkerId(kegiatanDto.getSatker() != null ? kegiatanDto.getSatker().getId() : null)
                .satkerName(kegiatanDto.getSatker() != null ? kegiatanDto.getSatker().getName() : null)
                .programId(kegiatanDto.getProgram() != null ? kegiatanDto.getProgram().getId() : null)
                .programName(kegiatanDto.getProgram() != null ? kegiatanDto.getProgram().getName() : null)
                .outputId(kegiatanDto.getOutput() != null ? kegiatanDto.getOutput().getId() : null)
                .outputName(kegiatanDto.getOutput() != null ? kegiatanDto.getOutput().getName() : null)
                .direktoratPjId(kegiatanDto.getDirektoratPenanggungJawab() != null
                        ? kegiatanDto.getDirektoratPenanggungJawab().getId()
                        : null)
                .direktoratPjName(kegiatanDto.getDirektoratPenanggungJawab() != null
                        ? kegiatanDto.getDirektoratPenanggungJawab().getName()
                        : null)
                .build();
    }
}