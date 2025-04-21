package com.sms.controller;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Kegiatan;
import com.sms.service.KegiatanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for Kegiatan (Activity) operations
 * 
 * @author rest-api
 */
@RestController
@RequestMapping("/api/kegiatans")
public class KegiatanController {
    private final KegiatanService kegiatanService;

    public KegiatanController(KegiatanService kegiatanService) {
        this.kegiatanService = kegiatanService;
    }

    /**
     * Get all kegiatans
     * 
     * @return list of kegiatans
     */
    @GetMapping
    public ResponseEntity<List<KegiatanDto>> getAllKegiatans() {
        List<KegiatanDto> kegiatanDtos = this.kegiatanService.ambilDaftarKegiatan();
        return ResponseEntity.ok(kegiatanDtos);
    }

    /**
     * Get kegiatan by id
     * 
     * @param id kegiatan id
     * @return kegiatan details
     */
    @GetMapping("/{id}")
    public ResponseEntity<KegiatanDto> getKegiatanById(@PathVariable("id") Long id) {
        KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(id);
        return ResponseEntity.ok(kegiatanDto);
    }

    /**
     * Get kegiatan entity by id (for detailed information)
     * 
     * @param id kegiatan id
     * @return kegiatan entity with all relationships
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<Kegiatan> getKegiatanDetailById(@PathVariable("id") Long id) {
        Kegiatan kegiatan = kegiatanService.findKegiatanById(id);
        return ResponseEntity.ok(kegiatan);
    }

    /**
     * Create new kegiatan
     * 
     * @param kegiatanDto kegiatan data
     * @return created kegiatan
     */
    @PostMapping
    public ResponseEntity<KegiatanDto> createKegiatan(@Valid @RequestBody KegiatanDto kegiatanDto) {
        kegiatanService.simpanDataKegiatan(kegiatanDto);
        return new ResponseEntity<>(kegiatanDto, HttpStatus.CREATED);
    }

    /**
     * Update existing kegiatan
     * 
     * @param id          kegiatan id
     * @param kegiatanDto kegiatan data
     * @return updated kegiatan
     */
    @PutMapping("/{id}")
    public ResponseEntity<KegiatanDto> updateKegiatan(
            @PathVariable("id") Long id,
            @Valid @RequestBody KegiatanDto kegiatanDto) {

        // Set the ID from the path variable
        kegiatanDto.setId(id);
        kegiatanService.perbaruiDataKegiatan(kegiatanDto);
        return ResponseEntity.ok(kegiatanDto);
    }

    /**
     * Delete kegiatan by id
     * 
     * @param id kegiatan id
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteKegiatan(@PathVariable("id") Long id) {
        kegiatanService.hapusDataKegiatan(id);
        return ResponseEntity.ok(Map.of("message", "Kegiatan with ID " + id + " deleted successfully"));
    }
}