/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.OutputDto;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.mapper.OutputMapper;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.service.OutputService;

/**
 *
 * @author pinaa
 */
@Service
public class OutputServiceImpl implements OutputService {
    private OutputRepository outputRepository;

    private final ProgramRepository programRepository;

    public OutputServiceImpl(OutputRepository outputRepository, ProgramRepository programRepository) {
        this.outputRepository = outputRepository;
        this.programRepository = programRepository;
    }

    @Override
    public List<OutputDto> ambilDaftarOutput() {
        List<Output> outputs = this.outputRepository.findAll();
        List<OutputDto> outputDtos = outputs.stream()
                .map((output) -> (OutputMapper.mapToOutputDto(output)))
                .collect(Collectors.toList());
        return outputDtos;
    }

    @Override
    public void hapusDataOutput(Long outputId) {
        outputRepository.deleteById(outputId);
    }

    @Override
    public void perbaruiDataOutput(OutputDto outputDto) {
        Output output = OutputMapper.mapToOutput(outputDto);
        System.out.println(outputDto);
        outputRepository.save(output);
    }

    @Override
    public void simpanDataOutput(OutputDto outputDto) {
        Output output = OutputMapper.mapToOutput(outputDto);
        outputRepository.save(output);
    }

    @Override
    public OutputDto cariOutputById(Long id) {
        Output output = outputRepository.findById(id).get();
        return OutputMapper.mapToOutputDto(output);
    }

    @Override
    public OutputDto patchOutput(Long outputId, Map<String, Object> updates) {
        final Output[] outputHolder = new Output[1];
        outputHolder[0] = outputRepository.findById(outputId)
                .orElseThrow(() -> new RuntimeException("Output not found with id: " + outputId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        outputHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        outputHolder[0].setCode((String) value);
                }
                case "year" -> {
                    if (value != null)
                        outputHolder[0].setYear((String) value);
                }
                case "program" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> programData = (Map<String, Object>) value;
                        Long programId = Long.valueOf(programData.get("id").toString());
                        Program program = programRepository.findById(programId)
                                .orElseThrow(() -> new RuntimeException("Program not found with id: " + programId));
                        outputHolder[0].setProgram(program);
                    }
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        outputHolder[0] = outputRepository.save(outputHolder[0]);
        return OutputMapper.mapToOutputDto(outputHolder[0]);
    }
}