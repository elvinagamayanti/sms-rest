package com.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.dto.SatkerDto;
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
    @Operation(summary = "Menampilkan Daftar Satuan Kerja", description = "Menampilkan daftar seluruh satuan kerja yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar satuan kerja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<SatkerDto>> getAllSatkers() {
        List<SatkerDto> satkerDtos = this.satkerService.ambilDaftarSatker();
        return ResponseEntity.ok(satkerDtos);
    }

    /**
     * Get satker by id
     * 
     * @param id satker id
     * @return satker details
     */
    @Operation(summary = "Menampilkan Satuan Kerja berdasarkan ID", description = "Menampilkan detail satuan kerja berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan satuan kerja berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<SatkerDto> getSatkerById(@PathVariable("id") Long id) {
        SatkerDto satkerDto = satkerService.cariSatkerById(id);
        return ResponseEntity.ok(satkerDto);
    }

    /**
     * Create new satker
     * 
     * @param satkerDto satker data
     * @return created satker
     */
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
    @Operation(summary = "Memperbarui Satuan Kerja", description = "Memperbarui data satuan kerja yang sudah ada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Satuan kerja berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SatkerDto.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SatkerDto> updateSatker(
            @PathVariable("id") Long id,
            @Valid @RequestBody SatkerDto satkerDto) {

        // Set the ID from the path variable
        satkerDto.setId(id);
        satkerService.perbaruiDataSatker(satkerDto);
        return ResponseEntity.ok(satkerDto);
    }

    /**
     * Delete satker by id
     * 
     * @param id satker id
     * @return success message
     */
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
}