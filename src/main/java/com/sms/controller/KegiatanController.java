package com.sms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.sms.entity.User;
import com.sms.mapper.KegiatanMapper;
import com.sms.service.KegiatanService;
import com.sms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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

    private final Logger logger = LoggerFactory.getLogger(KegiatanController.class);

    public KegiatanController(KegiatanService kegiatanService, UserService userService) {
        this.kegiatanService = kegiatanService;
        this.userService = userService;
    }

    /**
     * Get all kegiatan with automatic scope filtering
     */
    @LogActivity(description = "Retrieved filtered kegiatan list", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Kegiatan", description = "Menampilkan daftar kegiatan berdasarkan scope akses user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar kegiatan"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> getAllKegiatans() {
        try {
            // Using filtered method based on user scope
            List<KegiatanDto> kegiatanDtos = kegiatanService.findAllKegiatanFiltered();
            List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Tidak memiliki akses untuk melihat kegiatan"));
        } catch (Exception e) {
            logger.error("Error getting all kegiatan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }

    /**
     * Get kegiatan by ID with access validation
     */
    @LogActivity(description = "Retrieved kegiatan by ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Detail Kegiatan", description = "Menampilkan detail kegiatan berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan detail kegiatan"),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan"),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk melihat kegiatan ini")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> getKegiatanById(@PathVariable("id") Long id) {
        try {
            // Validate access permission
            if (!kegiatanService.canAccessKegiatan(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk melihat kegiatan ini"));
            }

            KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(id);
            SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapKegiatanDtoToSimpleKegiatanDto(kegiatanDto);
            return ResponseEntity.ok(simpleKegiatanDto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Kegiatan tidak ditemukan"));
        } catch (Exception e) {
            logger.error("Error getting kegiatan by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }

    /**
     * Get detailed kegiatan entity by ID (for comprehensive information)
     */
    @LogActivity(description = "Retrieved kegiatan entity by ID", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Detail Lengkap Kegiatan", description = "Menampilkan detail lengkap kegiatan dengan semua relasi")
    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> getKegiatanDetailById(@PathVariable("id") Long id) {
        try {
            if (!kegiatanService.canAccessKegiatan(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk melihat detail kegiatan ini"));
            }

            KegiatanDto kegiatanDto = kegiatanService.cariKegiatanById(id);
            return ResponseEntity.ok(kegiatanDto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Kegiatan tidak ditemukan"));
        } catch (Exception e) {
            logger.error("Error getting kegiatan detail by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }

    /**
     * Create new kegiatan (master kegiatan - pusat only)
     */
    @LogActivity(description = "Created new kegiatan", activityType = ActivityType.CREATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Kegiatan Baru", description = "Membuat kegiatan master baru (hanya untuk pusat)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kegiatan berhasil dibuat"),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid"),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk membuat kegiatan")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
    public ResponseEntity<?> createKegiatan(@Valid @RequestBody KegiatanDto kegiatanDto) {
        try {
            KegiatanDto savedKegiatan = kegiatanService.simpanDataKegiatan(kegiatanDto);

            User currentUser = userService.getUserLogged();
            logger.info("Kegiatan {} created by user {}", savedKegiatan.getName(), currentUser.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedKegiatan);

        } catch (Exception e) {
            logger.error("Error creating kegiatan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal membuat kegiatan: " + e.getMessage()));
        }
    }

    /**
     * Update kegiatan with permission validation
     */
    @LogActivity(description = "Updated kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Kegiatan", description = "Memperbarui data kegiatan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kegiatan berhasil diperbarui"),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan"),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk mengubah kegiatan ini")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
    public ResponseEntity<?> updateKegiatan(@PathVariable("id") Long id, @Valid @RequestBody KegiatanDto kegiatanDto) {
        try {
            // Validate modify permission
            if (!kegiatanService.canModifyKegiatan(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk mengubah kegiatan ini"));
            }

            kegiatanDto.setId(id);
            kegiatanService.perbaruiDataKegiatan(kegiatanDto);

            SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapKegiatanDtoToSimpleKegiatanDto(kegiatanDto);
            return ResponseEntity.ok(simpleKegiatanDto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Kegiatan tidak ditemukan"));
        } catch (Exception e) {
            logger.error("Error updating kegiatan {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengupdate kegiatan: " + e.getMessage()));
        }
    }

    /**
     * Partial update kegiatan
     */
    @LogActivity(description = "Partially updated kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Kegiatan", description = "Memperbarui sebagian field kegiatan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data kegiatan"),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan"),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk mengubah kegiatan ini")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
    public ResponseEntity<?> patchKegiatan(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
        try {
            if (!kegiatanService.canModifyKegiatan(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk mengubah kegiatan ini"));
            }

            KegiatanDto kegiatanDto = kegiatanService.patchKegiatan(id, updates);
            SimpleKegiatanDto simpleKegiatanDto = KegiatanMapper.mapKegiatanDtoToSimpleKegiatanDto(kegiatanDto);
            return ResponseEntity.ok(simpleKegiatanDto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Kegiatan tidak ditemukan"));
        } catch (Exception e) {
            logger.error("Error patching kegiatan {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengupdate kegiatan: " + e.getMessage()));
        }
    }

    /**
     * Delete kegiatan (pusat only)
     */
    @LogActivity(description = "Deleted kegiatan", activityType = ActivityType.DELETE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Hapus Kegiatan", description = "Menghapus kegiatan (hanya untuk pusat)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kegiatan berhasil dihapus"),
            @ApiResponse(responseCode = "404", description = "Kegiatan tidak ditemukan"),
            @ApiResponse(responseCode = "403", description = "Tidak memiliki akses untuk menghapus kegiatan")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT')")
    public ResponseEntity<?> deleteKegiatan(@PathVariable("id") Long id) {
        try {
            if (!kegiatanService.canModifyKegiatan(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk menghapus kegiatan ini"));
            }

            kegiatanService.hapusDataKegiatan(id);
            return ResponseEntity.ok(Map.of("message", "Kegiatan berhasil dihapus"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Kegiatan tidak ditemukan"));
        } catch (Exception e) {
            logger.error("Error deleting kegiatan {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal menghapus kegiatan"));
        }
    }

    // ====================================
    // ASSIGNMENT OPERATIONS (PUSAT ONLY)
    // ====================================

    /**
     * Assign kegiatan to multiple satkers (pusat only)
     */
    @LogActivity(description = "Assigned kegiatan to satkers", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Assign Kegiatan ke Satkers", description = "Menduplikasi kegiatan dari pusat ke satker-satker daerah")
    @PostMapping("/{kegiatanId}/assign-to-satkers")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
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

        try {
            Map<String, Object> result = kegiatanService.assignKegiatanToSatkers(kegiatanId, satkerIds);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        } catch (Exception e) {
            logger.error("Error assigning kegiatan {} to satkers: {}", kegiatanId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Gagal assign kegiatan: " + e.getMessage()));
        }
    }

    /**
     * Assign kegiatan to provinces (pusat only)
     */
    @LogActivity(description = "Assigned kegiatan to provinces", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Assign Kegiatan ke Provinsi", description = "Menduplikasi kegiatan dari pusat ke semua satker di provinsi yang dipilih")
    @PostMapping("/{kegiatanId}/assign-to-provinces")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
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

        try {
            Map<String, Object> result = kegiatanService.assignKegiatanToProvinces(kegiatanId, provinceCodes);

            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        } catch (Exception e) {
            logger.error("Error assigning kegiatan {} to provinces: {}", kegiatanId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Gagal assign kegiatan: " + e.getMessage()));
        }
    }

    /**
     * Assign user to kegiatan (admin only)
     */
    @LogActivity(description = "Assigned user to kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Assign User ke Kegiatan", description = "Menugaskan user untuk mengerjakan kegiatan tertentu")
    @PostMapping("/{kegiatanId}/assign-user")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'ADMIN_PROVINSI', 'ADMIN_SATKER')")
    public ResponseEntity<Map<String, Object>> assignUserToKegiatan(
            @PathVariable("kegiatanId") Long kegiatanId,
            @RequestBody Map<String, Long> request) {

        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "User ID tidak boleh kosong"));
        }

        try {
            // Validate access to this kegiatan
            if (!kegiatanService.canModifyKegiatan(kegiatanId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message",
                                "Tidak memiliki akses untuk assign user ke kegiatan ini"));
            }

            Map<String, Object> result = kegiatanService.assignUserToKegiatan(kegiatanId, userId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error assigning user {} to kegiatan {}: {}", userId, kegiatanId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Gagal assign user: " + e.getMessage()));
        }
    }

    // ====================================
    // QUERY & FILTERING ENDPOINTS
    // ====================================

    /**
     * Get kegiatan assigned to specific satker with access validation
     */
    @LogActivity(description = "Retrieved assigned kegiatan by satker", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan yang Di-assign ke Satker", description = "Menampilkan daftar kegiatan yang telah di-assign ke satker tertentu")
    @GetMapping("/assigned/satker/{satkerId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> getAssignedKegiatanBySatker(@PathVariable("satkerId") Long satkerId) {
        try {
            // Validate access to this satker's data
            User currentUser = userService.getUserLogged();
            String userRole = userService.getCurrentUserHighestRole();

            boolean canAccess = false;
            switch (userRole) {
                case "ROLE_SUPERADMIN":
                case "ROLE_ADMIN_PUSAT":
                case "ROLE_OPERATOR_PUSAT":
                    canAccess = true; // Can access any satker
                    break;
                case "ROLE_ADMIN_PROVINSI":
                case "ROLE_OPERATOR_PROVINSI":
                    // Can only access satkers in same province
                    // TODO: Implement validation that satkerId is in same province as current user
                    canAccess = true; // Temporary - should validate province scope
                    break;
                case "ROLE_ADMIN_SATKER":
                case "ROLE_OPERATOR_SATKER":
                    // Can only access own satker
                    canAccess = currentUser.getSatker().getId().equals(satkerId);
                    break;
            }

            if (!canAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Tidak memiliki akses untuk melihat kegiatan satker ini"));
            }

            List<KegiatanDto> kegiatans = kegiatanService.getAssignedKegiatanBySatker(satkerId);
            List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatans.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting assigned kegiatan for satker {}: {}", satkerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan sistem"));
        }
    }

    /**
     * Search kegiatan (with automatic scope filtering)
     */
    @LogActivity(description = "Searched kegiatan by query", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Search Kegiatan", description = "Mencari kegiatan berdasarkan nama atau kode")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> searchKegiatan(@RequestParam("q") String query) {
        try {
            // Note: searchKegiatan should be enhanced to respect user scope
            List<KegiatanDto> kegiatanDtos = kegiatanService.searchKegiatan(query);

            // Filter results based on user access
            List<KegiatanDto> filteredResults = kegiatanDtos.stream()
                    .filter(dto -> kegiatanService.canAccessKegiatan(dto.getId()))
                    .collect(Collectors.toList());

            List<SimpleKegiatanDto> simpleKegiatanDtos = filteredResults.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error searching kegiatan with query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal melakukan pencarian"));
        }
    }

    /**
     * Filter kegiatan with multiple criteria
     */
    @LogActivity(description = "Filtered kegiatan by criteria", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Filter Kegiatan", description = "Filter kegiatan berdasarkan direktorat, tahun, dan program")
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> filterKegiatan(
            @RequestParam(value = "direktoratId", required = false) Long direktoratId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "programId", required = false) Long programId) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.filterKegiatan(direktoratId, year, programId);

            // Filter results based on user access
            List<KegiatanDto> filteredResults = kegiatanDtos.stream()
                    .filter(dto -> kegiatanService.canAccessKegiatan(dto.getId()))
                    .collect(Collectors.toList());

            List<SimpleKegiatanDto> simpleKegiatanDtos = filteredResults.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error filtering kegiatan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal melakukan filter"));
        }
    }

    /**
     * Get all kegiatan with automatic scope filtering
     */
    @LogActivity(description = "Retrieved filtered kegiatan list", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Kegiatan", description = "Menampilkan daftar kegiatan berdasarkan scope akses user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar kegiatan"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI', 'ADMIN_SATKER', 'OPERATOR_SATKER')")
    public ResponseEntity<?> getKegiatanStatistics() {
        try {
            Map<String, Object> statistics = kegiatanService.getKegiatanStatisticsForCurrentScope();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Error getting kegiatan statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil statistik"));
        }
    }

    /**
     * Get monthly statistics for specific year and direktorat
     */
    @LogActivity(description = "Retrieved monthly statistics", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Statistik Bulanan", description = "Menampilkan statistik kegiatan bulanan")
    @GetMapping("/statistics/monthly")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
    public ResponseEntity<?> getMonthlyStatistics(
            @RequestParam("year") int year,
            @RequestParam(value = "direktoratId", required = false) Long direktoratId) {
        try {
            Map<String, Object> statistics = kegiatanService.getMonthlyStatistics(year, direktoratId);
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Error getting monthly statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil statistik bulanan"));
        }
    }

    // ====================================
    // DIREKTORAT & DEPUTI QUERIES
    // ====================================

    /**
     * Get kegiatan by direktorat PJ
     */
    @LogActivity(description = "Retrieved kegiatan by direktorat PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan berdasarkan Direktorat PJ", description = "Menampilkan kegiatan berdasarkan direktorat penanggung jawab")
    @GetMapping("/direktorat/{direktoratId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
    public ResponseEntity<?> getKegiatanByDirektoratPJ(@PathVariable("direktoratId") Long direktoratId) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJ(direktoratId);

            // Filter results based on user access
            List<KegiatanDto> filteredResults = kegiatanDtos.stream()
                    .filter(dto -> kegiatanService.canAccessKegiatan(dto.getId()))
                    .collect(Collectors.toList());

            List<SimpleKegiatanDto> simpleKegiatanDtos = filteredResults.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan by direktorat PJ {}: {}", direktoratId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    /**
     * Get kegiatan by direktorat PJ code
     */
    @LogActivity(description = "Retrieved kegiatan by direktorat PJ code", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan berdasarkan Kode Direktorat PJ", description = "Menampilkan kegiatan berdasarkan kode direktorat penanggung jawab")
    @GetMapping("/direktorat/code/{direktoratCode}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
    public ResponseEntity<?> getKegiatanByDirektoratPJCode(@PathVariable("direktoratCode") String direktoratCode) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDirektoratPJCode(direktoratCode);

            // Filter results based on user access
            List<KegiatanDto> filteredResults = kegiatanDtos.stream()
                    .filter(dto -> kegiatanService.canAccessKegiatan(dto.getId()))
                    .collect(Collectors.toList());

            List<SimpleKegiatanDto> simpleKegiatanDtos = filteredResults.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan by direktorat PJ code {}: {}", direktoratCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    /**
     * Get kegiatan by deputi PJ
     */
    @LogActivity(description = "Retrieved kegiatan by deputi PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan berdasarkan Deputi PJ", description = "Menampilkan kegiatan berdasarkan deputi penanggung jawab")
    @GetMapping("/deputi/{deputiId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
    public ResponseEntity<?> getKegiatanByDeputiPJ(@PathVariable("deputiId") Long deputiId) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJ(deputiId);
            List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan by deputi PJ {}: {}", deputiId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    /**
     * Get kegiatan by deputi PJ code
     */
    @LogActivity(description = "Retrieved kegiatan by deputi PJ code", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan berdasarkan Kode Deputi PJ", description = "Menampilkan kegiatan berdasarkan kode deputi penanggung jawab")
    @GetMapping("/deputi/code/{deputiCode}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
    public ResponseEntity<?> getKegiatanByDeputiPJCode(@PathVariable("deputiCode") String deputiCode) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByDeputiPJCode(deputiCode);
            List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan by deputi PJ code {}: {}", deputiCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    /**
     * Get kegiatan by year and direktorat PJ
     */
    @LogActivity(description = "Retrieved kegiatan by year and direktorat PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan berdasarkan Tahun dan Direktorat PJ", description = "Menampilkan kegiatan berdasarkan tahun dan direktorat penanggung jawab")
    @GetMapping("/year/{year}/direktorat/{direktoratId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT', 'ADMIN_PROVINSI', 'OPERATOR_PROVINSI')")
    public ResponseEntity<?> getKegiatanByYearAndDirektoratPJ(
            @PathVariable("year") int year,
            @PathVariable("direktoratId") Long direktoratId) {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanByYearAndDirektoratPJ(year, direktoratId);

            // Filter results based on user access
            List<KegiatanDto> filteredResults = kegiatanDtos.stream()
                    .filter(dto -> kegiatanService.canAccessKegiatan(dto.getId()))
                    .collect(Collectors.toList());

            List<SimpleKegiatanDto> simpleKegiatanDtos = filteredResults.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan by year {} and direktorat PJ {}: {}", year, direktoratId,
                    e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    // ====================================
    // UTILITY ENDPOINTS
    // ====================================

    /**
     * Get kegiatan without direktorat PJ (admin only)
     */
    @LogActivity(description = "Retrieved kegiatan without direktorat PJ", activityType = ActivityType.VIEW, entityType = EntityType.KEGIATAN, severity = LogSeverity.LOW)
    @Operation(summary = "Kegiatan Tanpa Direktorat PJ", description = "Menampilkan kegiatan yang belum memiliki direktorat penanggung jawab")
    @GetMapping("/without-direktorat-pj")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT', 'OPERATOR_PUSAT')")
    public ResponseEntity<?> getKegiatanWithoutDirektoratPJ() {
        try {
            List<KegiatanDto> kegiatanDtos = kegiatanService.getKegiatanWithoutDirektoratPJ();
            List<SimpleKegiatanDto> simpleKegiatanDtos = kegiatanDtos.stream()
                    .map(KegiatanMapper::mapKegiatanDtoToSimpleKegiatanDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(simpleKegiatanDtos);

        } catch (Exception e) {
            logger.error("Error getting kegiatan without direktorat PJ: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal mengambil data kegiatan"));
        }
    }

    /**
     * Assign direktorat PJ to kegiatan (admin only)
     */
    @LogActivity(description = "Assigned direktorat PJ to kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Assign Direktorat PJ", description = "Menugaskan direktorat penanggung jawab ke kegiatan")
    @PostMapping("/{kegiatanId}/assign-direktorat-pj/{direktoratId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT')")
    public ResponseEntity<?> assignDirektoratPJ(
            @PathVariable("kegiatanId") Long kegiatanId,
            @PathVariable("direktoratId") Long direktoratId) {
        try {
            kegiatanService.assignDirektoratPJ(kegiatanId, direktoratId);
            return ResponseEntity.ok(Map.of("message", "Direktorat PJ berhasil di-assign"));

        } catch (Exception e) {
            logger.error("Error assigning direktorat PJ {} to kegiatan {}: {}", direktoratId, kegiatanId,
                    e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Gagal assign direktorat PJ: " + e.getMessage()));
        }
    }

    /**
     * Sync direktorat PJ from user (admin only)
     */
    @LogActivity(description = "Synced direktorat PJ from user", activityType = ActivityType.UPDATE, entityType = EntityType.KEGIATAN, severity = LogSeverity.HIGH)
    @Operation(summary = "Sync Direktorat PJ dari User", description = "Menyinkronkan direktorat PJ berdasarkan direktorat user")
    @PostMapping("/sync-direktorat-pj")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN_PUSAT')")
    public ResponseEntity<Map<String, Object>> syncDirektoratPJFromUser() {
        try {
            Map<String, Object> result = kegiatanService.syncDirektoratPJFromUser();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error syncing direktorat PJ from user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Gagal sync direktorat PJ: " + e.getMessage()));
        }
    }

}