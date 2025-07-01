/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.ProgramDto;
import com.sms.entity.Program;
import com.sms.mapper.ProgramMapper;
import com.sms.repository.ProgramRepository;
import com.sms.service.ProgramService;

/**
 *
 * @author pinaa
 */
@Service
public class ProgramServiceImpl implements ProgramService {
    private ProgramRepository programRepository;

    public ProgramServiceImpl(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @Override
    public List<ProgramDto> ambilDaftarProgram() {
        List<Program> programs = this.programRepository.findAll();
        List<ProgramDto> programDtos = programs.stream()
                .map((program) -> (ProgramMapper.mapToProgramDto(program)))
                .collect(Collectors.toList());
        return programDtos;
    }

    @Override
    public void hapusDataProgram(Long programId) {
        programRepository.deleteById(programId);
    }

    @Override
    public void perbaruiDataProgram(ProgramDto programDto) {
        Program program = ProgramMapper.mapToProgram(programDto);
        System.out.println(programDto);
        programRepository.save(program);
    }

    @Override
    public void simpanDataProgram(ProgramDto programDto) {
        Program program = ProgramMapper.mapToProgram(programDto);
        programRepository.save(program);
    }

    @Override
    public ProgramDto cariProgramById(Long id) {
        Program program = programRepository.findById(id).get();
        return ProgramMapper.mapToProgramDto(program);
    }

    @Override
    public ProgramDto patchProgram(Long programId, Map<String, Object> updates) {
        final Program[] programHolder = new Program[1];
        programHolder[0] = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found with id: " + programId));

        // Update only the fields that are provided
        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        programHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        programHolder[0].setCode((String) value);
                }
                case "year" -> {
                    if (value != null)
                        programHolder[0].setYear((String) value);
                }
                default -> {
                }
            }
            // Ignore unknown fields
        });

        programHolder[0] = programRepository.save(programHolder[0]);
        return ProgramMapper.mapToProgramDto(programHolder[0]);
    }
}