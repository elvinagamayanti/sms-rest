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
import com.sms.dto.DeputiDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.DeputiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Deputi operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/deputis")
public class DeputiController {
    private final DeputiService deputiService;

    public DeputiController(DeputiService deputiService) {
        this.deputiService = deputiService;
    }

    /**
     * Get all deputis
     * 
     * @return list of deputis
     */
    @LogActivity(description = "Retrieved all deputis list", activityType = ActivityType.VIEW, entityType = EntityType.DEPUTI, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Deputi", description = "Menampilkan daftar seluruh deputi yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<DeputiDto>> getAllDeputis() {
        List<DeputiDto> deputiDtos = this.deputiService.ambilDaftarDeputi();
        return ResponseEntity.ok(deputiDtos);
    }

    /**
     * Get deputi by ID
     * 
     * @param id the ID of the deputi
     * @return the deputi with the specified ID
     */
    @LogActivity(description = "Retrieved deputi by ID", activityType = ActivityType.VIEW, entityType = EntityType.DEPUTI, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Deputi berdasarkan ID", description = "Menampilkan detail deputi berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan deputi berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeputiDto> getDeputiById(@PathVariable("id") Long id) {
        DeputiDto deputiDto = deputiService.cariDeputiById(id);
        return ResponseEntity.ok(deputiDto);
    }

    /**
     * Get deputi by code
     * 
     * @param code the code of the deputi
     * @return the deputi with the specified code
     */
    @LogActivity(description = "Retrieved deputi by code", activityType = ActivityType.VIEW, entityType = EntityType.DEPUTI, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Deputi berdasarkan Kode", description = "Menampilkan detail deputi berdasarkan kode yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan deputi berdasarkan kode", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<DeputiDto> getDeputiByCode(@PathVariable("code") String code) {
        DeputiDto deputiDto = deputiService.cariDeputiByCode(code);
        return ResponseEntity.ok(deputiDto);
    }

    /**
     * Create a new deputi
     * 
     * @param deputiDto the deputi data to create
     * @return the created deputi
     */
    @LogActivity(description = "Created new deputi", activityType = ActivityType.CREATE, entityType = EntityType.DEPUTI, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Deputi Baru", description = "Membuat deputi baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deputi berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "400", description = "Permintaan tidak valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DeputiDto> createDeputi(@Valid @RequestBody DeputiDto deputiDto) {
        deputiService.simpanDataDeputi(deputiDto);
        return new ResponseEntity<>(deputiDto, HttpStatus.CREATED);
    }

    /**
     * Update an existing deputi
     * 
     * @param id        the ID of the deputi to update
     * @param deputiDto the updated deputi data
     * @return the updated deputi
     */
    @LogActivity(description = "Updated deputi by ID", activityType = ActivityType.UPDATE, entityType = EntityType.DEPUTI, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Deputi", description = "Memperbarui deputi yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deputi berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeputiDto> updateDeputi(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeputiDto deputiDto) {
        deputiDto.setId(id);
        deputiService.perbaruiDataDeputi(deputiDto);
        return ResponseEntity.ok(deputiDto);
    }

    /**
     * Delete a deputi by ID
     * 
     * @param id the ID of the deputi to delete
     * @return a message indicating success
     */
    @LogActivity(description = "Deleted deputi by ID", activityType = ActivityType.DELETE, entityType = EntityType.DEPUTI, severity = LogSeverity.HIGH)
    @Operation(summary = "Menghapus Deputi", description = "Menghapus deputi berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deputi berhasil dihapus"),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDeputi(@PathVariable("id") Long id) {
        deputiService.hapusDataDeputi(id);
        return ResponseEntity.ok(Map.of("message", "Deputi with ID " + id + " deleted successfully"));
    }

    /**
     * Get users by deputi ID
     * 
     * @param id the ID of the deputi
     * @return list of users associated with the deputi
     */
    @LogActivity(description = "Retrieved users by deputi ID", activityType = ActivityType.VIEW, entityType = EntityType.DEPUTI, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Pengguna berdasarkan ID Deputi", description = "Menampilkan daftar pengguna yang tergabung dalam deputi tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan pengguna berdasarkan ID deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getUsersByDeputiId(@PathVariable("id") Long id) {
        List<User> users = deputiService.getUsersByDeputiId(id);
        return ResponseEntity.ok(users);
    }

    /**
     * Update partial deputi data
     * 
     * @param id      the ID of the deputi to update
     * @param updates the fields to update
     * @return the updated deputi
     */
    @LogActivity(description = "Partially updated deputi by ID", activityType = ActivityType.UPDATE, entityType = EntityType.DEPUTI, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Deputi", description = "Memperbarui sebagian field deputi tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data deputi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeputiDto.class))),
            @ApiResponse(responseCode = "404", description = "Deputi tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DeputiDto> patchDeputi(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        DeputiDto deputiDto = deputiService.patchDeputi(id, updates);
        return ResponseEntity.ok(deputiDto);
    }
}