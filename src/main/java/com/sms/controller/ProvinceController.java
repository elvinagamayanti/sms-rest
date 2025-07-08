package com.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.sms.dto.ProvinceDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.Satker;
import com.sms.payload.ApiErrorResponse;
import com.sms.repository.SatkerRepository;
import com.sms.service.ProvinceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Province operations
 * 
 * @author pinaa
 */
@CrossOrigin
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
    @LogActivity(description = "Retrieved all provinces list", activityType = ActivityType.VIEW, entityType = EntityType.PROVINCE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Provinsi", description = "Menampilkan daftar seluruh provinsi yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar provinsi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @LogActivity(description = "Retrieved province by ID", activityType = ActivityType.VIEW, entityType = EntityType.PROVINCE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Provinsi berdasarkan ID", description = "Menampilkan detail provinsi berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan provinsi berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "404", description = "Provinsi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @LogActivity(description = "Retrieved province by code", activityType = ActivityType.VIEW, entityType = EntityType.PROVINCE, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Provinsi berdasarkan Kode", description = "Menampilkan detail provinsi berdasarkan kode yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan provinsi berdasarkan kode", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "404", description = "Provinsi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @LogActivity(description = "Created a new province", activityType = ActivityType.CREATE, entityType = EntityType.PROVINCE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Provinsi Baru", description = "Membuat provinsi baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Provinsi berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @LogActivity(description = "Updated province by ID", activityType = ActivityType.UPDATE, entityType = EntityType.PROVINCE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Provinsi", description = "Memperbarui provinsi yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provinsi berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "404", description = "Provinsi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @LogActivity(description = "Deleted province by ID", activityType = ActivityType.DELETE, entityType = EntityType.PROVINCE, severity = LogSeverity.HIGH)
    @Operation(summary = "Menghapus Provinsi", description = "Menghapus provinsi berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provinsi berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Provinsi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @LogActivity(description = "Retrieved satkers by province code", activityType = ActivityType.VIEW, entityType = EntityType.SATKER, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Satuan Kerja berdasarkan Kode Provinsi", description = "Menampilkan daftar satuan kerja berdasarkan kode provinsi yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan satuan kerja berdasarkan kode provinsi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Satker.class))),
            @ApiResponse(responseCode = "404", description = "Satuan kerja tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{provinceCode}/satkers")
    public ResponseEntity<List<Satker>> getSatkersByProvinceCode(@PathVariable String provinceCode) {
        List<Satker> satkers = satkerRepository.findByCodeStartingWith(provinceCode);
        return ResponseEntity.ok(satkers);
    }

    /**
     * Update partial province data
     * 
     * @param id      province id
     * @param updates fields to update
     * @return updated province
     */
    @LogActivity(description = "Partially updated province by ID", activityType = ActivityType.UPDATE, entityType = EntityType.PROVINCE, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Provinsi", description = "Memperbarui sebagian field provinsi tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data provinsi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProvinceDto.class))),
            @ApiResponse(responseCode = "404", description = "Provinsi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProvinceDto> patchProvince(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        ProvinceDto provinceDto = provinceService.patchProvince(id, updates);
        return ResponseEntity.ok(provinceDto);
    }
}