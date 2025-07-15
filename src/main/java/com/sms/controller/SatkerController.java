package com.sms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.sms.dto.SatkerDto;
import com.sms.dto.SimpleOutputDto;
import com.sms.dto.SimpleSatkerDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.mapper.OutputMapper;
import com.sms.mapper.SatkerMapper;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.SatkerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Satker (Work Unit) operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/satkers")
public class SatkerController {
    private final SatkerService satkerService;

    public SatkerController(SatkerService satkerService) {
        this.satkerService = satkerService;
    }

    /**
     * Get all satkers
     * 
     * @return list of satkers
     */
    @LogActivity(description = "Retrieved all satkers list", activityType = ActivityType.VIEW, entityType = EntityType.SATKER, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Satuan Kerja", description = "Menampilkan daftar seluruh satuan kerja yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar satuan kerja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    // public ResponseEntity<List<SatkerDto>> getAllSatkers() {
    // List<SatkerDto> satkerDtos = this.satkerService.ambilDaftarSatker();
    // return ResponseEntity.ok(satkerDtos);
    // }
    public ResponseEntity<List<SimpleSatkerDto>> getAllSatkers() {
        List<SatkerDto> satkerDtos = this.satkerService.ambilDaftarSatker();
        List<SimpleSatkerDto> simpleSatkerDtos = satkerDtos.stream()
                .map(SatkerMapper::mapSatkerDtoToSimpleSatkerDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleSatkerDtos);
    }

    /**
     * Get satker by id
     * 
     * @param id satker id
     * @return satker details
     */
    @LogActivity(description = "Retrieved satker by ID", activityType = ActivityType.VIEW, entityType = EntityType.SATKER, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Satuan Kerja berdasarkan ID", description = "Menampilkan detail satuan kerja berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan satuan kerja berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    // public ResponseEntity<SatkerDto> getSatkerById(@PathVariable("id") Long id) {
    // SatkerDto satkerDto = satkerService.cariSatkerById(id);
    // return ResponseEntity.ok(satkerDto);
    // }
    public ResponseEntity<SimpleSatkerDto> getSatkerById(@PathVariable("id") Long id) {
        SatkerDto satkerDto = satkerService.cariSatkerById(id);
        SimpleSatkerDto simpleSatkerDto = SatkerMapper.mapSatkerDtoToSimpleSatkerDto(satkerDto);
        return ResponseEntity.ok(simpleSatkerDto);
    }

    /**
     * Create new satker
     * 
     * @param satkerDto satker data
     * @return created satker
     */
    @LogActivity(description = "Created a new satker", activityType = ActivityType.CREATE, entityType = EntityType.SATKER, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Satuan Kerja Baru", description = "Membuat satuan kerja baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Satuan kerja berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SatkerDto> createSatker(@Valid @RequestBody SatkerDto satkerDto) {
        satkerService.simpanDataSatker(satkerDto);
        return new ResponseEntity<>(satkerDto, HttpStatus.CREATED);
    }

    /**
     * Update existing satker
     * 
     * @param id        satker id
     * @param satkerDto satker data
     * @return updated satker
     */
    @LogActivity(description = "Updated satker by ID", activityType = ActivityType.UPDATE, entityType = EntityType.SATKER, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Satuan Kerja", description = "Memperbarui data satuan kerja yang sudah ada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Satuan kerja berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SimpleSatkerDto> updateSatker(
            @PathVariable("id") Long id,
            @Valid @RequestBody SatkerDto satkerDto) {

        // Set the ID from the path variable
        satkerDto.setId(id);
        satkerService.perbaruiDataSatker(satkerDto);
        SimpleSatkerDto simpleSatkerDto = SatkerMapper.mapSatkerDtoToSimpleSatkerDto(satkerDto);
        return ResponseEntity.ok(simpleSatkerDto);
    }

    /**
     * Delete satker by id
     * 
     * @param id satker id
     * @return success message
     */
    @LogActivity(description = "Deleted satker by ID", activityType = ActivityType.DELETE, entityType = EntityType.SATKER, severity = LogSeverity.HIGH)
    @Operation(summary = "Menghapus Satuan Kerja", description = "Menghapus satuan kerja berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Satuan kerja berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSatker(@PathVariable("id") Long id) {
        satkerService.hapusDataSatker(id);
        return ResponseEntity.ok(Map.of("message", "Satker with ID " + id + " deleted successfully"));
    }

    /**
     * Update partial satker data
     * 
     * @param id      satker id
     * @param updates fields to update
     * @return updated satker
     */
    @LogActivity(description = "Partially updated satker by ID", activityType = ActivityType.UPDATE, entityType = EntityType.SATKER, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Satuan Kerja", description = "Memperbarui sebagian field satuan kerja tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data satuan kerja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<SimpleSatkerDto> patchSatker(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        SatkerDto satkerDto = satkerService.patchSatker(id, updates);
        SimpleSatkerDto simpleSatkerDto = SatkerMapper.mapSatkerDtoToSimpleSatkerDto(satkerDto);
        return ResponseEntity.ok(simpleSatkerDto);
    }
}