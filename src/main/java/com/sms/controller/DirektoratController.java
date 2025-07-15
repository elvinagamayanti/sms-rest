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
import org.springframework.web.bind.annotation.RestController;

import com.sms.annotation.LogActivity;
import com.sms.dto.DirektoratDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.DirektoratService;
import com.sms.mapper.DirektoratMapper;
import com.sms.dto.SimpleDirektoratDto;
import java.util.stream.Collectors;
import com.sms.dto.SimpleUserDto;
import com.sms.mapper.UserMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Direktorat operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/direktorats")
public class DirektoratController {
    private final DirektoratService direktoratService;

    public DirektoratController(DirektoratService direktoratService) {
        this.direktoratService = direktoratService;
    }

    /**
     * Get all direktorat
     * 
     * @return list of direktorat
     */
    @LogActivity(description = "Retrieved all direktorats list", activityType = ActivityType.VIEW, entityType = EntityType.DIREKTORAT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Direktorat", description = "Menampilkan daftar seluruh direktorat yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    // public ResponseEntity<List<DirektoratDto>> getAllDirektorats() {
    // List<DirektoratDto> direktoratDtos =
    // this.direktoratService.ambilDaftarDirektorat();
    // return ResponseEntity.ok(direktoratDtos);
    // }
    public ResponseEntity<List<SimpleDirektoratDto>> getAllDirektorats() {
        List<DirektoratDto> direktoratDtos = this.direktoratService.ambilDaftarDirektorat();
        List<SimpleDirektoratDto> simpleDirektoratDtos = direktoratDtos.stream()
                .map(DirektoratMapper::mapDirektoratDtoToSimpleDirektoratDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleDirektoratDtos);
    }

    /**
     * Get direktorat by ID
     * 
     * @param id the ID of the direktorat
     * @return the direktorat with the specified ID
     */
    @LogActivity(description = "Retrieved direktorat by ID", activityType = ActivityType.VIEW, entityType = EntityType.DIREKTORAT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Direktorat berdasarkan ID", description = "Menampilkan detail direktorat berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan direktorat berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    // public ResponseEntity<DirektoratDto> getDirektoratById(@PathVariable("id")
    // Long id) {
    // DirektoratDto direktoratDto = direktoratService.cariDirektoratById(id);
    // return ResponseEntity.ok(direktoratDto);
    // }
    public ResponseEntity<SimpleDirektoratDto> getDirektoratById(@PathVariable("id") Long id) {
        DirektoratDto direktoratDto = direktoratService.cariDirektoratById(id);
        SimpleDirektoratDto simpleDirektoratDto = DirektoratMapper.mapDirektoratDtoToSimpleDirektoratDto(direktoratDto);
        return ResponseEntity.ok(simpleDirektoratDto);
    }

    @LogActivity(description = "Retrieved direktorat by code", activityType = ActivityType.VIEW, entityType = EntityType.DIREKTORAT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Direktorat berdasarkan Kode", description = "Menampilkan detail direktorat berdasarkan kode yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan direktorat berdasarkan kode", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/code/{code}")
    // public ResponseEntity<DirektoratDto>
    // getDirektoratByCode(@PathVariable("code") String code) {
    // DirektoratDto direktoratDto = direktoratService.cariDirektoratByCode(code);
    // return ResponseEntity.ok(direktoratDto);
    // }
    public ResponseEntity<SimpleDirektoratDto> getDirektoratByCode(@PathVariable("code") String code) {
        DirektoratDto direktoratDto = direktoratService.cariDirektoratByCode(code);
        SimpleDirektoratDto simpleDirektoratDto = DirektoratMapper.mapDirektoratDtoToSimpleDirektoratDto(direktoratDto);
        return ResponseEntity.ok(simpleDirektoratDto);
    }

    @LogActivity(description = "Created new direktorat", activityType = ActivityType.CREATE, entityType = EntityType.DIREKTORAT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Direktorat Baru", description = "Membuat direktorat baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Direktorat berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DirektoratDto> createDirektorat(@Valid @RequestBody DirektoratDto direktoratDto) {
        direktoratService.simpanDataDirektorat(direktoratDto);
        return new ResponseEntity<>(direktoratDto, HttpStatus.CREATED);
    }

    @LogActivity(description = "Updated direktorat by ID", activityType = ActivityType.UPDATE, entityType = EntityType.DIREKTORAT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Direktorat", description = "Memperbarui direktorat yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direktorat berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DirektoratDto> updateDirektorat(
            @PathVariable("id") Long id,
            @Valid @RequestBody DirektoratDto direktoratDto) {
        direktoratDto.setId(id);
        direktoratService.perbaruiDataDirektorat(direktoratDto);
        return ResponseEntity.ok(direktoratDto);
    }

    @LogActivity(description = "Deleted direktorat by ID", activityType = ActivityType.DELETE, entityType = EntityType.DIREKTORAT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Menghapus Direktorat", description = "Menghapus direktorat berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direktorat berhasil dihapus"),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDirektorat(@PathVariable("id") Long id) {
        direktoratService.hapusDataDirektorat(id);
        return ResponseEntity.ok(Map.of("message", "Direktorat with ID " + id + " deleted successfully"));
    }

    @LogActivity(description = "Retrieved direktorats by deputi ID", activityType = ActivityType.VIEW, entityType = EntityType.DIREKTORAT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Direktorat berdasarkan ID Deputi", description = "Menampilkan daftar direktorat yang tergabung dalam deputi tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan direktorat berdasarkan ID deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/deputi/{deputiId}")
    // public ResponseEntity<List<DirektoratDto>>
    // getDirektoratsByDeputiId(@PathVariable("deputiId") Long deputiId) {
    // List<DirektoratDto> direktorats =
    // direktoratService.getDirektoratsByDeputiId(deputiId);
    // return ResponseEntity.ok(direktorats);
    // }
    public ResponseEntity<List<SimpleDirektoratDto>> getDirektoratsByDeputiId(@PathVariable("deputiId") Long deputiId) {
        List<DirektoratDto> direktorats = direktoratService.getDirektoratsByDeputiId(deputiId);
        List<SimpleDirektoratDto> simpleDirektoratDtos = direktorats.stream()
                .map(DirektoratMapper::mapDirektoratDtoToSimpleDirektoratDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleDirektoratDtos);
    }

    @LogActivity(description = "Retrieved users by direktorat ID", activityType = ActivityType.VIEW, entityType = EntityType.DIREKTORAT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Pengguna berdasarkan ID Direktorat", description = "Menampilkan daftar pengguna yang tergabung dalam direktorat tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan pengguna berdasarkan ID direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}/users")
    // public ResponseEntity<List<User>> getUsersByDirektoratId(@PathVariable("id")
    // Long id) {
    // List<User> users = direktoratService.getUsersByDirektoratId(id);
    // return ResponseEntity.ok(users);
    // }
    public ResponseEntity<List<SimpleUserDto>> getUsersByDirektoratId(@PathVariable("id") Long id) {
        List<User> users = direktoratService.getUsersByDirektoratId(id);
        List<SimpleUserDto> simpleUserDtos = users.stream()
                .map(UserMapper::mapToSimpleUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleUserDtos);
    }

    @LogActivity(description = "Partially updated direktorat by ID", activityType = ActivityType.UPDATE, entityType = EntityType.DIREKTORAT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Direktorat", description = "Memperbarui sebagian field direktorat tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data direktorat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DirektoratDto.class))),
            @ApiResponse(responseCode = "404", description = "Direktorat tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    // public ResponseEntity<DirektoratDto> patchDirektorat(
    // @PathVariable("id") Long id,
    // @RequestBody Map<String, Object> updates) {

    // DirektoratDto direktoratDto = direktoratService.patchDirektorat(id, updates);
    // return ResponseEntity.ok(direktoratDto);
    // }
    public ResponseEntity<SimpleDirektoratDto> patchDirektorat(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        DirektoratDto direktoratDto = direktoratService.patchDirektorat(id, updates);
        SimpleDirektoratDto simpleDirektoratDto = DirektoratMapper.mapDirektoratDtoToSimpleDirektoratDto(direktoratDto);
        return ResponseEntity.ok(simpleDirektoratDto);
    }

}