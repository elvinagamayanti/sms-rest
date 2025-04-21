package com.sms.controller;

import com.sms.dto.SatkerDto;
import com.sms.service.SatkerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for Satker (Work Unit) operations
 * 
 * @author rest-api
 */
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSatker(@PathVariable("id") Long id) {
        satkerService.hapusDataSatker(id);
        return ResponseEntity.ok(Map.of("message", "Satker with ID " + id + " deleted successfully"));
    }
}