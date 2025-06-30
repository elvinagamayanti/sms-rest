package com.sms.mapper;

import com.sms.dto.DeputiDto;
import com.sms.entity.Deputi;

/**
 * Mapper for Deputi
 * 
 * @author pinaa
 */
public class DeputiMapper {

    public static DeputiDto mapToDeputiDto(Deputi deputi) {
        DeputiDto deputiDto = DeputiDto.builder()
                .id(deputi.getId())
                .code(deputi.getCode())
                .name(deputi.getName())
                .build();
        return deputiDto;
    }

    public static Deputi mapToDeputi(DeputiDto deputiDto) {
        Deputi deputi = Deputi.builder()
                .id(deputiDto.getId())
                .code(deputiDto.getCode())
                .name(deputiDto.getName())
                .build();
        return deputi;
    }
}