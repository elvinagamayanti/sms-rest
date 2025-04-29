/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.OutputDto;
import com.sms.entity.Output;

/**
 *
 * @author pinaa
 */
public class OutputMapper {

    public static OutputDto mapToOutputDto(Output output) {
        OutputDto outputDto = OutputDto.builder()
                .id(output.getId())
                .name(output.getName())
                .code(output.getCode())
                .year(output.getYear())
                .program(output.getProgram())
                .build();
        return outputDto;
    }

    public static Output mapToOutput(OutputDto outputDto) {
        Output output = Output.builder()
                .id(outputDto.getId())
                .name(outputDto.getName())
                .code(outputDto.getCode())
                .year(outputDto.getYear())
                .program(outputDto.getProgram())
                .build();
        return output;
    }
}
