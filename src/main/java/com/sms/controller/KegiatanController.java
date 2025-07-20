package com.sms.controller;

import java.util.HashMap;
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

import com.sms.annotation.LogActivity;
import com.sms.dto.KegiatanDto;
import com.sms.dto.SimpleKegiatanDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.Kegiatan;
import com.sms.entity.User;
import com.sms.mapper.KegiatanMapper;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.KegiatanService;
import com.sms.service.UserService;

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
    private final UserService userService;

    public KegiatanController(KegiatanService kegiatanService, UserService userService) {
        this.kegiatanService = kegiatanService;
        this.userService = userService;
    }

    /**
     * Get all kegiatans
     * 
     * @return list of kegiatans
     */
    @LogActivity(description = "Retrieved all kegiatan list", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Kegiatan", description = "Menampilkan daftar seluruh kegiatan yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    // public ResponseEntity<List<KegiatanDto>> getAllKegiatans() {
    // List<KegiatanDto> kegiatanDtos = this.kegiatanService.ambilDaftarKegiatan();
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getAllKegiatans() {
        List<KegiatanDto> kegiatan = this.kegiatanService.ambilDaftarKegiatan();
        List<SimpleKegiatanDto> kegiatanDtos = kegiatan.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(kegiatanDtos);
    }

    /**
     * Get kegiatan by id
     * 
     * @param id kegiatan id
     * @return kegiatan details
     */
    @LogActivity(description = "Retrieved kegiatan by ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan ID", description = "Menampilkan detail kegiatan berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan kegiatan berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    // public ResponseEntity<KegiatanDto> getKegiatanById(@PathVariable("id") Long
    // id) {
    // KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(id);
    // return ResponseEntity.ok(kegiatanDto);
    // }
    public ResponseEntity<SimpleKegiatanDto> getKegiatanById(@PathVariable("id") Long id) {
        KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(id);
        SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapKegiatanDtoToSimpleKegiatanDto(kegiatanDto);
        return ResponseEntity.ok(simpleKegiatanDto);
    }

    /**
     * Get kegiatan entity by id (for detailed information)
     * 
     * @param id kegiatan id
     * @return kegiatan entity with all relationships
     */
    @LogActivity(description = "Retrieved kegiatan entity by ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Detail Kegiatan", description = "Menampilkan detail kegiatan berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan detail kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Kegiatan.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}/detail")
    // public ResponseEntity<Kegiatan> getKegiatanDetailById(@PathVariable("id")
    // Long id) {
    // Kegiatan kegiatan = kegiatanService.findKegiatanById(id);
    // return ResponseEntity.ok(kegiatan);
    // }
    public ResponseEntity<SimpleKegiatanDto> getKegiatanDetailById(@PathVariable("id") Long id) {
        Kegiatan kegiatan = kegiatanService.findKegiatanById(id);
        SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapToSimpleKegiatanDto(kegiatan);
        return ResponseEntity.ok(simpleKegiatanDto);
    }

    /**
     * Create new kegiatan
     * 
     * @param kegiatanDto kegiatan data
     * @return created kegiatan
     */
    @LogActivity(description = "Created new kegiatan", activityType = ActivityType.CREATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
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
    @LogActivity(description = "Updated kegiatan by ID", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
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
    @LogActivity(description = "Deleted kegiatan by ID", activityType = ActivityType.DELETE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
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

    /**
     * Update partial kegiatan data
     * 
     * @param id      kegiatan id
     * @param updates map of field updates
     * @return updated kegiatan
     */
    @LogActivity(description = "Partially updated kegiatan by ID", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Kegiatan", description = "Memperbarui sebagian field kegiatan tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    // public ResponseEntity<KegiatanDto> patchKegiatan(
    // @PathVariable("id") Long id,
    // @RequestBody Map<String, Object> updates) {

    // KegiatanDto kegiatanDto = kegiatanService.patchKegiatan(id, updates);
    // return ResponseEntity.ok(kegiatanDto);
    // }
    public ResponseEntity<SimpleKegiatanDto> patchKegiatan(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        KegiatanDto kegiatanDto = kegiatanService.patchKegiatan(id, updates);
        SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapKegiatanDtoToSimpleKegiatanDto(kegiatanDto);
        return ResponseEntity.ok(simpleKegiatanDto);
    }

    /**
     * Get kegiatan by program ID
     * 
     * @param programId program ID
     * @return list of kegiatan for the given program
     */
    @LogActivity(description = "Retrieved kegiatan by program ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan direktorat penanggung jawab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan kegiatan berdasarkan direktorat PJ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KegiatanDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan")
    })
    @GetMapping("/direktorat/{direktoratId}")
    // public ResponseEntity<List<KegiatanDto>> getKegiatanByDirektoratPJ(
    // @PathVariable("direktoratId") Long direktoratId) {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanByDirektoratPJ(direktoratId);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanByDirektoratPJ(
            @PathVariable("direktoratId") Long direktoratId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJ(direktoratId);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved kegiatan by direktorat PJ code", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan Kode Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan kode direktorat penanggung jawab")
    @GetMapping("/direktorat/code/{direktoratCode}")
    // public ResponseEntity<List<KegiatanDto>> getKegiatanByDirektoratPJCode(
    // @PathVariable("direktoratCode") String direktoratCode) {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanByDirektoratPJCode(direktoratCode);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanByDirektoratPJCode(
            @PathVariable("direktoratCode") String direktoratCode) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJCode(direktoratCode);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved kegiatan by deputi PJ ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan Deputi PJ", description = "Menampilkan daftar kegiatan berdasarkan deputi penanggung jawab")
    @GetMapping("/deputi/{deputiId}")
    // public ResponseEntity<List<KegiatanDto>>
    // getKegiatanByDeputiPJ(@PathVariable("deputiId") Long deputiId) {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanByDeputiPJ(deputiId);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanByDeputiPJ(@PathVariable("deputiId") Long deputiId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJ(deputiId);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved kegiatan by deputi PJ code", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan Kode Deputi PJ", description = "Menampilkan daftar kegiatan berdasarkan kode deputi penanggung jawab")
    @GetMapping("/deputi/code/{deputiCode}")
    // public ResponseEntity<List<KegiatanDto>>
    // getKegiatanByDeputiPJCode(@PathVariable("deputiCode") String deputiCode) {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanByDeputiPJCode(deputiCode);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanByDeputiPJCode(
            @PathVariable("deputiCode") String deputiCode) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJCode(deputiCode);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved kegiatan by year and direktorat PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Kegiatan berdasarkan Tahun dan Direktorat PJ", description = "Menampilkan daftar kegiatan berdasarkan tahun dan direktorat penanggung jawab")
    @GetMapping("/direktorat/{direktoratId}/year/{year}")
    // public ResponseEntity<List<KegiatanDto>> getKegiatanByYearAndDirektoratPJ(
    // @PathVariable("direktoratId") Long direktoratId,
    // @PathVariable("year") int year) {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanByYearAndDirektoratPJ(year, direktoratId);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanByYearAndDirektoratPJ(
            @PathVariable("direktoratId") Long direktoratId,
            @PathVariable("year") int year) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByYearAndDirektoratPJ(year, direktoratId);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved kegiatan by year and deputi PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Kegiatan per Direktorat", description = "Menampilkan statistik jumlah kegiatan per direktorat")
    @GetMapping("/statistics/direktorat")
    public ResponseEntity<Map<String, Object>> getStatisticsByDirektorat() {
        Map<String, Object> statistics = kegiatanService.getKegiatanStatisticsByDirektorat();
        return ResponseEntity.ok(statistics);
    }

    @LogActivity(description = "Retrieved kegiatan statistics by deputi", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Kegiatan per Deputi", description = "Menampilkan statistik jumlah kegiatan per deputi")
    @GetMapping("/statistics/deputi")
    public ResponseEntity<Map<String, Object>> getStatisticsByDeputi() {
        Map<String, Object> statistics = kegiatanService.getKegiatanStatisticsByDeputi();
        return ResponseEntity.ok(statistics);
    }

    @LogActivity(description = "Retrieved budget statistics by direktorat", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Anggaran per Direktorat", description = "Menampilkan statistik total anggaran per direktorat")
    @GetMapping("/budget/direktorat")
    public ResponseEntity<Map<String, Object>> getBudgetByDirektorat() {
        Map<String, Object> budgetStats = kegiatanService.getBudgetStatisticsByDirektorat();
        return ResponseEntity.ok(budgetStats);
    }

    @LogActivity(description = "Retrieved budget statistics by deputi", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Anggaran per Deputi", description = "Menampilkan statistik total anggaran per deputi")
    @GetMapping("/budget/deputi")
    public ResponseEntity<Map<String, Object>> getBudgetByDeputi() {
        Map<String, Object> budgetStats = kegiatanService.getBudgetStatisticsByDeputi();
        return ResponseEntity.ok(budgetStats);
    }

    @LogActivity(description = "Retrieved kegiatan without direktorat PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan tanpa Direktorat PJ", description = "Menampilkan daftar kegiatan yang belum memiliki direktorat penanggung jawab")
    @GetMapping("/no-direktorat-pj")
    // public ResponseEntity<List<KegiatanDto>> getKegiatanWithoutDirektoratPJ() {
    // List<KegiatanDto> kegiatanDtos =
    // kegiatanService.getKegiatanWithoutDirektoratPJ();
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> getKegiatanWithoutDirektoratPJ() {
        List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanWithoutDirektoratPJ();
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Searched kegiatan by query", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Search Kegiatan", description = "Mencari kegiatan berdasarkan nama atau kode")
    @GetMapping("/search")
    // public ResponseEntity<List<KegiatanDto>> searchKegiatan(@RequestParam("q")
    // String query) {
    // List<KegiatanDto> kegiatanDtos = kegiatanService.searchKegiatan(query);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> searchKegiatan(@RequestParam("q") String query) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.searchKegiatan(query);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Filtered kegiatan by direktorat, year, and program", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Filter Kegiatan", description = "Filter kegiatan berdasarkan direktorat, tahun, dan program")
    @GetMapping("/filter")
    // public ResponseEntity<List<KegiatanDto>> filterKegiatan(
    // @RequestParam(value = "direktoratId", required = false) Long direktoratId,
    // @RequestParam(value = "year", required = false) Integer year,
    // @RequestParam(value = "programId", required = false) Long programId) {
    // List<KegiatanDto> kegiatanDtos = kegiatanService.filterKegiatan(direktoratId,
    // year, programId);
    // return ResponseEntity.ok(kegiatanDtos);
    // }
    public ResponseEntity<List<SimpleKegiatanDto>> filterKegiatan(
            @RequestParam(value = "direktoratId", required = false) Long direktoratId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "programId", required = false) Long programId) {
        List<KegiatanDto> kegiatanDtos = kegiatanService.filterKegiatan(direktoratId, year, programId);
        List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                .toList();
        return ResponseEntity.ok(simpleKegiatanDtos);
    }

    @LogActivity(description = "Retrieved monthly statistics for a specific direktorat", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Bulanan per Direktorat", description = "Menampilkan statistik kegiatan per bulan untuk direktorat tertentu")
    @GetMapping("/statistics/monthly/{direktoratId}/{year}")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(
            @PathVariable("direktoratId") Long direktoratId,
            @PathVariable("year") int year) {
        Map<String, Object> monthlyStats = kegiatanService.getMonthlyStatistics(year, direktoratId);
        return ResponseEntity.ok(monthlyStats);
    }

    @LogActivity(description = "Assigned direktorat PJ to a kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Assign Direktorat PJ", description = "Mengubah direktorat penanggung jawab kegiatan secara manual")
    @PostMapping("/{kegiatanId}/assign-direktorat/{direktoratId}")
    public ResponseEntity<Map<String, String>> assignDirektoratPJ(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("direktoratId") Long direktoratId) {
        kegiatanService.assignDirektoratPJ(kegiatanId, direktoratId);
        return ResponseEntity.ok(Map.of("message", "Direktorat PJ berhasil di-assign ke kegiatan"));
    }

    @LogActivity(description = "Synced direktorat PJ from user", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Sync Direktorat PJ dari User", description = "Sinkronisasi direktorat PJ dengan direktorat user untuk semua kegiatan")
    @PostMapping("/sync-direktorat-pj")
    public ResponseEntity<Map<String, Object>> syncDirektoratPJFromUser() {
        Map<String, Object> result = kegiatanService.syncDirektoratPJFromUser();
        return ResponseEntity.ok(result);
    }

    /**
     * Assign kegiatan ke multiple satker
     */
    @LogActivity(description = "Assigned kegiatan to multiple satkers", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Assign Kegiatan ke Satker", description = "Menduplikasi kegiatan dari pusat ke satker-satker daerah yang dipilih")
    @PostMapping("/{kegiatanId}/assign-to-satkers")
    public ResponseEntity<Map<String, Object>> assignKegiatanToSatkers(
            @PathVariable("kegiatanId") Long kegiatanId,
            @RequestBody Map<String, List<Long>> request) {

        List<Long> satkerIds = request.get("satkerIds");

        if (satkerIds == null || satkerIds.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Daftar satker tidak boleh kosong");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = kegiatanService.assignKegiatanToSatkers(kegiatanId, satkerIds);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * Assign kegiatan berdasarkan kode provinsi
     */
    @LogActivity(description = "Assigned kegiatan to provinces", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Assign Kegiatan ke Provinsi", description = "Menduplikasi kegiatan dari pusat ke semua satker di provinsi yang dipilih")
    @PostMapping("/{kegiatanId}/assign-to-provinces")
    public ResponseEntity<Map<String, Object>> assignKegiatanToProvinces(
            @PathVariable("kegiatanId") Long kegiatanId,
            @RequestBody Map<String, List<String>> request) {

        List<String> provinceCodes = request.get("provinceCodes");

        if (provinceCodes == null || provinceCodes.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Daftar kode provinsi tidak boleh kosong");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = kegiatanService.assignKegiatanToProvinces(kegiatanId, provinceCodes);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * Get kegiatan yang di-assign ke satker tertentu
     */
    @LogActivity(description = "Retrieved assigned kegiatan by satker", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan yang Di-assign ke Satker", description = "Menampilkan daftar kegiatan yang telah di-assign ke satker tertentu")
    @GetMapping("/assigned/satker/{satkerId}")
    public ResponseEntity<List<KegiatanDto>> getAssignedKegiatanBySatker(
            @PathVariable("satkerId") Long satkerId) {
        List<KegiatanDto> kegiatans = kegiatanService.getAssignedKegiatanBySatker(satkerId);
        return ResponseEntity.ok(kegiatans);
    }

    /**
     * Assign user ke kegiatan (untuk admin/supervisor)
     */
    @LogActivity(description = "Assigned user to kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Assign User ke Kegiatan", description = "Menugaskan user tertentu ke kegiatan di satker")
    @PostMapping("/{kegiatanId}/assign-user/{userId}")
    public ResponseEntity<Map<String, Object>> assignUserToKegiatan(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("userId") Long userId) {

        Map<String, Object> result = kegiatanService.assignUserToKegiatan(kegiatanId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * User claim kegiatan untuk dirinya sendiri
     */
    @LogActivity(description = "User claimed kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Claim Kegiatan", description = "User mengambil/claim kegiatan untuk dirinya sendiri")
    @PostMapping("/{kegiatanId}/claim")
    public ResponseEntity<Map<String, Object>> claimKegiatan(
            @PathVariable("kegiatanId") Long kegiatanId) {

        Map<String, Object> result = kegiatanService.claimKegiatan(kegiatanId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * Unassign user dari kegiatan
     */
    @LogActivity(description = "Unassigned user from kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Unassign User dari Kegiatan", description = "Melepas assignment user dari kegiatan")
    @PostMapping("/{kegiatanId}/unassign")
    public ResponseEntity<Map<String, Object>> unassignUserFromKegiatan(
            @PathVariable("kegiatanId") Long kegiatanId) {

        Map<String, Object> result = kegiatanService.unassignUserFromKegiatan(kegiatanId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * Get kegiatan yang belum ditugaskan di satker
     */
    @LogActivity(description = "Retrieved unassigned kegiatan by satker", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan Belum Ditugaskan", description = "Menampilkan daftar kegiatan yang belum ditugaskan di satker")
    @GetMapping("/unassigned/satker/{satkerId}")
    public ResponseEntity<List<KegiatanDto>> getUnassignedKegiatanBySatker(
            @PathVariable("satkerId") Long satkerId) {
        List<KegiatanDto> kegiatans = kegiatanService.getUnassignedKegiatanBySatker(satkerId);
        return ResponseEntity.ok(kegiatans);
    }

    /**
     * Get kegiatan yang ditugaskan ke user tertentu
     */
    @LogActivity(description = "Retrieved kegiatan by assigned user", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan User", description = "Menampilkan daftar kegiatan yang ditugaskan ke user tertentu")
    @GetMapping("/assigned/user/{userId}")
    public ResponseEntity<List<KegiatanDto>> getKegiatanByAssignedUser(
            @PathVariable("userId") Long userId) {
        List<KegiatanDto> kegiatans = kegiatanService.getKegiatanByAssignedUser(userId);
        return ResponseEntity.ok(kegiatans);
    }

    /**
     * Get kegiatan untuk current user yang sedang login
     */
    @Operation(summary = "Kegiatan Saya", description = "Menampilkan daftar kegiatan yang ditugaskan ke user yang sedang login")
    @GetMapping("/my-kegiatans")
    public ResponseEntity<List<KegiatanDto>> getMyKegiatans() {
        User currentUser = userService.getCurrentUser();
        List<KegiatanDto> kegiatans = kegiatanService.getKegiatanByAssignedUser(currentUser.getId());
        return ResponseEntity.ok(kegiatans);
    }

    /**
     * Transfer kegiatan dari satu user ke user lain
     */
    @LogActivity(description = "Transferred kegiatan between users", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Transfer Kegiatan", description = "Transfer kegiatan dari satu user ke user lain dalam satker yang sama")
    @PostMapping("/{kegiatanId}/transfer/{fromUserId}/{toUserId}")
    public ResponseEntity<Map<String, Object>> transferKegiatanToUser(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("fromUserId") Long fromUserId,
            @PathVariable("toUserId") Long toUserId) {

        Map<String, Object> result = kegiatanService.transferKegiatanToUser(
                kegiatanId, fromUserId, toUserId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
}