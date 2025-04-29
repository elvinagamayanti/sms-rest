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

import com.sms.dto.KegiatanDto;
import com.sms.entity.Kegiatan;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.KegiatanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Kegiatan (Activity) operations
 * 
 * @author pinaa
 */
@CrossOrigin
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
    @Operation(summary = "Menampilkan Daftar Kegiatan", description = "Menampilkan daftar seluruh kegiatan yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
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
    @Operation(summary = "Menampilkan Kegiatan berdasarkan ID", description = "Menampilkan detail kegiatan berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan kegiatan berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Menampilkan Detail Kegiatan", description = "Menampilkan detail kegiatan berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan detail kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Kegiatan.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Membuat Kegiatan Baru", description = "Membuat kegiatan baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kegiatan berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    // @PostMapping
    // public ResponseEntity<KegiatanDto> createKegiatan(@Valid @RequestBody
    // KegiatanDto kegiatanDto) {
    // kegiatanService.simpanDataKegiatan(kegiatanDto);
    // return new ResponseEntity<>(kegiatanDto, HttpStatus.CREATED);
    // }
    @PostMapping
    public ResponseEntity<KegiatanDto> createKegiatan(@Valid @RequestBody KegiatanDto kegiatanDto) {
        KegiatanDto savedKegiatan = kegiatanService.simpanDataKegiatan(kegiatanDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedKegiatan);
    }

    /**
     * Update existing kegiatan
     * 
     * @param id          kegiatan id
     * @param kegiatanDto kegiatan data
     * @return updated kegiatan
     */
    @Operation(summary = "Memperbarui Kegiatan", description = "Memperbarui kegiatan yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kegiatan berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
    @Operation(summary = "Menghapus Kegiatan", description = "Menghapus kegiatan berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kegiatan berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteKegiatan(@PathVariable("id") Long id) {
        kegiatanService.hapusDataKegiatan(id);
        return ResponseEntity.ok(Map.of("message", "Kegiatan with ID " + id + " deleted successfully"));
    }
}