package com.sms.controller;

import com.sms.dto.ProgramDto;
import com.sms.service.ProgramService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for Program operations
 * 
 * @author rest-api
 */
@RestController
@RequestMapping("/api/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    /**
     * Get all programs
     * 
     * @return list of programs
     */
    @GetMapping
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        List<ProgramDto> programDtos = this.programService.ambilDaftarProgram();
        return ResponseEntity.ok(programDtos);
    }

    /**
     * Get program by id
     * 
     * @param id program id
     * @return program details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgramById(@PathVariable("id") Long id) {
        ProgramDto programDto = programService.cariProgramById(id);
        return ResponseEntity.ok(programDto);
    }

    /**
     * Create new program
     * 
     * @param programDto program data
     * @return created program
     */
    @PostMapping
    public ResponseEntity<ProgramDto> createProgram(@Valid @RequestBody ProgramDto programDto) {
        programService.simpanDataProgram(programDto);
        return new ResponseEntity<>(programDto, HttpStatus.CREATED);
    }

    /**
     * Update existing program
     * 
     * @param id         program id
     * @param programDto program data
     * @return updated program
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProgramDto> updateProgram(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProgramDto programDto) {

        // Set the ID from the path variable
        programDto.setId(id);
        programService.perbaruiDataProgram(programDto);
        return ResponseEntity.ok(programDto);
    }

    /**
     * Delete program by id
     * 
     * @param id program id
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProgram(@PathVariable("id") Long id) {
        programService.hapusDataProgram(id);
        return ResponseEntity.ok(Map.of("message", "Program with ID " + id + " deleted successfully"));
    }
}