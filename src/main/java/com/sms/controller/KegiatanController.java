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
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "Update Sebagian Data Kegiatan", description = "Memperbarui sebagian field kegiatan tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<KegiatanDto> patchKegiatan(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        KegiatanDto kegiatanDto = kegiatanService.patchKegiatan(id, updates);
        return ResponseEntity.ok(kegiatanDto);
    }

    @Operation(summary = "Menampilkan Kegiatan berdasarkan Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan direktorat penanggung jawab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan kegiatan berdasarkan direktorat PJ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan")
    })
    @GetMapping("/direktorat/{direktoratId}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByDirektoratPJ(
            @PathVariable("direktoratId") Long direktoratId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJ(direktoratId);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Menampilkan Kegiatan berdasarkan Kode Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan kode direktorat penanggung jawab")
    @GetMapping("/direktorat/code/{direktoratCode}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByDirektoratPJCode(
            @PathVariable("direktoratCode") String direktoratCode) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJCode(direktoratCode);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Menampilkan Kegiatan berdasarkan Deputi PJ", description = "Menampilkan daftar kegiatan berdasarkan deputi penanggung jawab")
    @GetMapping("/deputi/{deputiId}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByDeputiPJ(@PathVariable("deputiId") Long deputiId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJ(deputiId);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Menampilkan Kegiatan berdasarkan Kode Deputi PJ", description = "Menampilkan daftar kegiatan berdasarkan kode deputi penanggung jawab")
    @GetMapping("/deputi/code/{deputiCode}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByDeputiPJCode(@PathVariable("deputiCode") String deputiCode) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJCode(deputiCode);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Menampilkan Kegiatan berdasarkan Tahun dan Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan tahun dan direktorat penanggung jawab")
    @GetMapping("/direktorat/{direktoratId}/year/{year}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByYearAndDirektoratPJ(
            @PathVariable("direktoratId") Long direktoratId,
            @PathVariable("year") int year) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByYearAndDirektoratPJ(year, direktoratId);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Statistik Kegiatan per Direktorat", description = "Menampilkan statistik jumlah kegiatan per direktorat")
    @GetMapping("/statistics/direktorat")
    public ResponseEntity<Map<String, Object>> getStatisticsByDirektorat() {
        Map<String, Object> statistics = kegiatanService.getKegiatanStatisticsByDirektorat();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Kegiatan per Deputi", description = "Menampilkan statistik jumlah kegiatan per deputi")
    @GetMapping("/statistics/deputi")
    public ResponseEntity<Map<String, Object>> getStatisticsByDeputi() {
        Map<String, Object> statistics = kegiatanService.getKegiatanStatisticsByDeputi();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Anggaran per Direktorat", description = "Menampilkan statistik total anggaran per direktorat")
    @GetMapping("/budget/direktorat")
    public ResponseEntity<Map<String, Object>> getBudgetByDirektorat() {
        Map<String, Object> budgetStats = kegiatanService.getBudgetStatisticsByDirektorat();
        return ResponseEntity.ok(budgetStats);
    }

    @Operation(summary = "Statistik Anggaran per Deputi", description = "Menampilkan statistik total anggaran per deputi")
    @GetMapping("/budget/deputi")
    public ResponseEntity<Map<String, Object>> getBudgetByDeputi() {
        Map<String, Object> budgetStats = kegiatanService.getBudgetStatisticsByDeputi();
        return ResponseEntity.ok(budgetStats);
    }

    @Operation(summary = "Kegiatan tanpa Direktorat PJ", description = "Menampilkan daftar kegiatan yang belum memiliki direktorat penanggung jawab")
    @GetMapping("/no-direktorat-pj")
    public ResponseEntity<List<KegiatanDto>> getKegiatanWithoutDirektoratPJ() {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanWithoutDirektoratPJ();
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Search Kegiatan", description = "Mencari kegiatan berdasarkan nama atau kode")
    @GetMapping("/search")
    public ResponseEntity<List<KegiatanDto>> searchKegiatan(@RequestParam("q") String query) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.searchKegiatan(query);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Filter Kegiatan", description = "Filter kegiatan berdasarkan direktorat, tahun, dan program")
    @GetMapping("/filter")
    public ResponseEntity<List<KegiatanDto>> filterKegiatan(
            @RequestParam(value = "direktoratId", required = false) Long direktoratId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "programId", required = false) Long programId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.filterKegiatan(direktoratId, year, programId);
        return ResponseEntity.ok(kegiatanDtos);
    }

    @Operation(summary = "Statistik Bulanan per Direktorat", description = "Menampilkan statistik kegiatan per bulan untuk direktorat tertentu")
    @GetMapping("/statistics/monthly/{direktoratId}/{year}")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(
            @PathVariable("direktoratId") Long direktoratId,
            @PathVariable("year") int year) {
        Map<String, Object> monthlyStats = kegiatanService.getMonthlyStatistics(year, direktoratId);
        return ResponseEntity.ok(monthlyStats);
    }

    @Operation(summary = "Assign Direktorat PJ", description = "Mengubah direktorat penanggung jawab kegiatan secara manual")
    @PostMapping("/{kegiatanId}/assign-direktorat/{direktoratId}")
    public ResponseEntity<Map<String, String>> assignDirektoratPJ(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("direktoratId") Long direktoratId) {
        kegiatanService.assignDirektoratPJ(kegiatanId, direktoratId);
        return ResponseEntity.ok(Map.of("message", "Direktorat PJ berhasil di-assign ke kegiatan"));
    }

    @Operation(summary = "Sync Direktorat PJ dari User", description = "Sinkronisasi direktorat PJ dengan direktorat user untuk semua kegiatan")
    @PostMapping("/sync-direktorat-pj")
    public ResponseEntity<Map<String, Object>> syncDirektoratPJFromUser() {
        Map<String, Object> result = kegiatanService.syncDirektoratPJFromUser();
        return ResponseEntity.ok(result);
    }
}