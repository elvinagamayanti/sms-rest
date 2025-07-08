package com.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.dto.ProgramDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.ProgramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Program operations
 * 
 * @author pinaa
 */
@CrossOrigin
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
    @LogActivity(description = "Retrieved all programs list", activityType = ActivityType.VIEW, entityType = EntityType.PROGRAM, severity = LogSeverity.LOW)
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
    @LogActivity(description = "Retrieved program by ID", activityType = ActivityType.VIEW, entityType = EntityType.PROGRAM, severity = LogSeverity.LOW)
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
    @LogActivity(description = "Created a new program", activityType = ActivityType.CREATE, entityType = EntityType.PROGRAM, severity = LogSeverity.MEDIUM)
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
    @LogActivity(description = "Updated program by ID", activityType = ActivityType.UPDATE, entityType = EntityType.PROGRAM, severity = LogSeverity.MEDIUM)
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
    @LogActivity(description = "Deleted program by ID", activityType = ActivityType.DELETE, entityType = EntityType.PROGRAM, severity = LogSeverity.HIGH)
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

    /**
     * Partially update existing program
     * 
     * @param id      program id
     * @param updates map of fields to update
     * @return updated program
     */
    @LogActivity(description = "Partially updated program by ID", activityType = ActivityType.UPDATE, entityType = EntityType.PROGRAM, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Program", description = "Memperbarui sebagian field program tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data program", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class))),
            @ApiResponse(responseCode = "404", description = "Program tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProgramDto> patchProgram(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        ProgramDto programDto = programService.patchProgram(id, updates);
        return ResponseEntity.ok(programDto);
    }
}