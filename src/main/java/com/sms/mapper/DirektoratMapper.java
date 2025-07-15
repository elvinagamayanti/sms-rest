package com.sms.mapper;

import com.sms.dto.DirektoratDto;
import com.sms.dto.SimpleDirektoratDto;
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

    public static SimpleDirektoratDto mapToSimpleDirektoratDto(Direktorat direktorat) {
        return SimpleDirektoratDto.builder()
                .id(direktorat.getId())
                .name(direktorat.getName())
                .code(direktorat.getCode())
                .deputiId(direktorat.getDeputi() != null ? direktorat.getDeputi().getId() : null)
                .deputiName(direktorat.getDeputi() != null ? direktorat.getDeputi().getName() : null)
                .deputiCode(direktorat.getDeputi() != null ? direktorat.getDeputi().getCode() : null)
                .build();
    }

    public static SimpleDirektoratDto mapDirektoratDtoToSimpleDirektoratDto(DirektoratDto direktoratDto) {
        return SimpleDirektoratDto.builder()
                .id(direktoratDto.getId())
                .name(direktoratDto.getName())
                .code(direktoratDto.getCode())
                .deputiId(direktoratDto.getDeputi() != null ? direktoratDto.getDeputi().getId() : null)
                .deputiName(direktoratDto.getDeputi() != null ? direktoratDto.getDeputi().getName() : null)
                .deputiCode(direktoratDto.getDeputi() != null ? direktoratDto.getDeputi().getCode() : null)
                .build();
    }
}