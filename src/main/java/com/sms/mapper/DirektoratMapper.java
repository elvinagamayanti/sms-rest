package com.sms.mapper;

import com.sms.dto.DirektoratDto;
import com.sms.entity.Direktorat;

/**
 * Mapper for Direktorat
 * 
 * @author pinaa
 */
public class DirektoratMapper {

    public static DirektoratDto mapToDirektoratDto(Direktorat direktorat) {
        DirektoratDto direktoratDto = DirektoratDto.builder()
                .id(direktorat.getId())
                .code(direktorat.getCode())
                .name(direktorat.getName())
                .deputi(direktorat.getDeputi())
                .build();
        return direktoratDto;
    }

    public static Direktorat mapToDirektorat(DirektoratDto direktoratDto) {
        Direktorat direktorat = Direktorat.builder()
                .id(direktoratDto.getId())
                .code(direktoratDto.getCode())
                .name(direktoratDto.getName())
                .deputi(direktoratDto.getDeputi())
                .build();
        return direktorat;
    }
}