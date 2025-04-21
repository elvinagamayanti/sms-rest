package com.sms.controller;

import com.sms.dto.ProvinceDto;
import com.sms.entity.Satker;
import com.sms.repository.SatkerRepository;
import com.sms.service.ProvinceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST API for Province operations
 * 
 * @author rest-api
 */
@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {
    private final ProvinceService provinceService;

    @Autowired
    private SatkerRepository satkerRepository;

    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    /**
     * Get all provinces
     * 
     * @return list of provinces
     */
    @GetMapping
    public ResponseEntity<List<ProvinceDto>> getAllProvinces() {
        List<ProvinceDto> provinceDtos = this.provinceService.ambilDaftarProvinsi();
        return ResponseEntity.ok(provinceDtos);
    }

    /**
     * Get province by id
     * 
     * @param id province id
     * @return province details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDto> getProvinceById(@PathVariable("id") Long id) {
        ProvinceDto provinceDto = provinceService.cariProvinceById(id);
        return ResponseEntity.ok(provinceDto);
    }

    /**
     * Get province by code
     * 
     * @param code province code
     * @return province details
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ProvinceDto> getProvinceByCode(@PathVariable("code") String code) {
        ProvinceDto provinceDto = provinceService.cariProvinceByCode(code);
        return ResponseEntity.ok(provinceDto);
    }

    /**
     * Create new province
     * 
     * @param provinceDto province data
     * @return created province
     */
    @PostMapping
    public ResponseEntity<ProvinceDto> createProvince(@Valid @RequestBody ProvinceDto provinceDto) {
        provinceService.simpanDataProvinsi(provinceDto);
        return new ResponseEntity<>(provinceDto, HttpStatus.CREATED);
    }

    /**
     * Update existing province
     * 
     * @param id          province id
     * @param provinceDto province data
     * @return updated province
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProvinceDto> updateProvince(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProvinceDto provinceDto) {

        // Set the ID from the path variable
        provinceDto.setId(id);
        provinceService.perbaruiDataProvinsi(provinceDto);
        return ResponseEntity.ok(provinceDto);
    }

    /**
     * Delete province by id
     * 
     * @param id province id
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProvince(@PathVariable("id") Long id) {
        provinceService.hapusDataProvinsi(id);
        return ResponseEntity.ok(Map.of("message", "Province with ID " + id + " deleted successfully"));
    }

    /**
     * Get satkers by province code
     * 
     * @param provinceCode province code
     * @return list of satkers
     */
    @GetMapping("/{provinceCode}/satkers")
    public ResponseEntity<List<Satker>> getSatkersByProvinceCode(@PathVariable String provinceCode) {
        List<Satker> satkers = satkerRepository.findByCodeStartingWith(provinceCode);
        return ResponseEntity.ok(satkers);
    }
}