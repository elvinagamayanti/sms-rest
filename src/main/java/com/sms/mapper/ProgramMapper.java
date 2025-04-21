/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.mapper;

import com.sms.dto.ProgramDto;
import com.sms.entity.Program;

/**
 *
 * @author pinaa
 */
public class ProgramMapper {

    public static ProgramDto mapToProgramDto(Program program) {
        ProgramDto programDto = ProgramDto.builder()
                .id(program.getId())
                .name(program.getName())
                .code(program.getCode())
                .year(program.getYear())
                .build();
        return programDto;
    }

    public static Program mapToProgram(ProgramDto programDto) {
        Program program = Program.builder()
                .id(programDto.getId())
                .name(programDto.getName())
                .code(programDto.getCode())
                .year(programDto.getYear())
                .build();
        return program;
    }
}
