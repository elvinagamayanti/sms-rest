package com.sms.controller;

import com.sms.dto.ProgramDto;
import com.sms.service.ProgramService;
import com.sms.payload.ApiErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    @Operation(summary = "Menampilkan Daftar Aktivitas", description = "Menampilkan daftar seluruh aktivitas yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar aktivitas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @Operation(summary = "Menampilkan Aktivitas berdasarkan ID", description = "Menampilkan detail aktivitas berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan aktivitas berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class))),
            @ApiResponse(responseCode = "404", description = "Aktivitas tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Membuat Aktivitas Baru", description = "Membuat aktivitas baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Berhasil membuat aktivitas baru", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Memperbarui Aktivitas", description = "Memperbarui aktivitas yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui aktivitas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class))),
            @ApiResponse(responseCode = "404", description = "Aktivitas tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Menghapus Aktivitas", description = "Menghapus aktivitas berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menghapus aktivitas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aktivitas tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProgram(@PathVariable("id") Long id) {
        programService.hapusDataProgram(id);
        return ResponseEntity.ok(Map.of("message", "Program with ID " + id + " deleted successfully"));
    }
}