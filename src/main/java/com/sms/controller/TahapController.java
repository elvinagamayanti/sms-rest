package com.sms.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sms.annotation.LogActivity;
import com.sms.dto.SubtahapDateRequest;
import com.sms.dto.TahapStatusDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.service.FileUploadService;
import com.sms.service.TahapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/tahap")
public class TahapController {
    private final TahapService tahapService;
    private final FileUploadService fileUploadService;

    public TahapController(TahapService tahapService, FileUploadService fileUploadService) {
        this.tahapService = tahapService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Menampilkan status tahap kegiatan berdasarkan ID kegiatan.
     *
     * @param kegiatanId ID dari kegiatan yang ingin ditampilkan status tahapanya.
     * @return ResponseEntity yang berisi status tahapan kegiatan.
     */
    @LogActivity(description = "Retrieved tahap status for kegiatan", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Tahap Status", description = "Menampilkan status kegiatan pada semua tahapan berdasarkan ID kegiatan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan status tahap kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TahapStatusDto.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{kegiatanId}")
    public ResponseEntity<TahapStatusDto> getTahapStatus(@PathVariable Long kegiatanId) {
        TahapStatusDto status = tahapService.getTahapStatus(kegiatanId);
        return ResponseEntity.ok(status);
    }

    /**
     * Menampilkan persentase penyelesaian tahap kegiatan berdasarkan ID kegiatan
     * dan tahap.
     *
     * @param kegiatanId ID dari kegiatan yang ingin ditampilkan persentasenya.
     * @param tahap      Tahap dari kegiatan yang ingin ditampilkan persentasenya.
     * @return ResponseEntity yang berisi persentase penyelesaian tahap kegiatan.
     */
    @LogActivity(description = "Retrieved tahap completion percentage for kegiatan", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Persentase Tahap", description = "Menampilkan persentase penyelesaian tahap berdasarkan ID kegiatan dan tahap")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan persentase tahap kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404", description = "Activity or stage not found")
    })
    @GetMapping("/{kegiatanId}/percentage/{tahap}")
    public ResponseEntity<Integer> getTahapPercentage(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap) {
        int percentage = tahapService.getTahapCompletionPercentage(kegiatanId, tahap);
        return ResponseEntity.ok(percentage);
    }

    /**
     * Menampilkan status subtahap kegiatan berdasarkan ID kegiatan, tahap, dan
     * subtahap.
     *
     * @param kegiatanId ID dari kegiatan yang ingin ditampilkan status subtahapnya.
     * @param tahap      Tahap dari kegiatan yang ingin ditampilkan status
     *                   subtahapnya.
     * @param subtahap   Subtahap dari kegiatan yang ingin ditampilkan statusnya.
     * @return ResponseEntity yang berisi status penyelesaian subtahap kegiatan.
     */
    @LogActivity(description = "Retrieved subtahap completion status for kegiatan", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Status Subtahap", description = "Menampilkan status subtahap berdasarkan ID kegiatan, tahap, dan subtahap")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan status subtahap kegiatan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @GetMapping("/{kegiatanId}/{tahap}/{subtahap}")
    public ResponseEntity<Boolean> isSubtaskCompleted(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap) {
        boolean completed = tahapService.isSubtaskCompleted(kegiatanId, tahap, subtahap);
        return ResponseEntity.ok(completed);
    }

    /**
     * Mengupdate status subtahap kegiatan berdasarkan ID kegiatan, tahap, dan
     * subtahap.
     *
     * @param kegiatanId ID dari kegiatan yang ingin diupdate status subtahapnya.
     * @param tahap      Tahap dari kegiatan yang ingin diupdate status subtahapnya.
     * @param subtahap   Subtahap dari kegiatan yang ingin diupdate statusnya.
     * @param completed  Status penyelesaian subtahap (true jika selesai, false jika
     *                   belum).
     * @return ResponseEntity yang berisi status HTTP 200 OK jika berhasil.
     */
    @LogActivity(description = "Updated subtahap completion status for kegiatan", activityType = ActivityType.UPDATE, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Mengupdate Status Subtahap", description = "Mengupdate status subtahap berdasarkan ID kegiatan, tahap, dan subtahap")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mengupdate status subtahap kegiatan"),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @PostMapping("/{kegiatanId}/{tahap}/{subtahap}")
    public ResponseEntity<Void> updateSubtaskStatus(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap,
            @RequestBody boolean completed) {
        tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, completed);
        return ResponseEntity.ok().build();
    }

    /**
     * Menandai tahap tertentu sebagai selesai dengan menandai semua subtahapnya
     * sebagai selesai.
     *
     * @param kegiatanId ID dari kegiatan yang ingin ditandai tahapnya sebagai
     *                   selesai.
     * @param tahap      Tahap dari kegiatan yang ingin ditandai sebagai selesai.
     * @return ResponseEntity yang berisi status HTTP 200 OK jika berhasil.
     */
    @LogActivity(description = "Completed all subtahap for kegiatan tahap", activityType = ActivityType.COMPLETE, entityType = EntityType.TAHAP, severity = LogSeverity.HIGH)
    @Operation(summary = "Menandai Tahap Sebagai Selesai", description = "Menandai semua subtahap dari tahap tertentu sebagai selesai")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menandai tahap sebagai selesai"),
            @ApiResponse(responseCode = "404", description = "Activity or stage not found")
    })
    @PostMapping("/{kegiatanId}/{tahap}/complete")
    public ResponseEntity<Void> completeTahap(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap) {
        switch (tahap) {
            case 1 -> {
                for (int subtahap = 1; subtahap <= 6; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 2 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 3 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 4 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 5 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 6 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 7 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            case 8 -> {
                for (int subtahap = 1; subtahap <= 4; subtahap++) {
                    tahapService.updateSubtaskStatus(kegiatanId, tahap, subtahap, true);
                }
            }
            default -> {
            }
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Mengupload file untuk tahap tertentu.
     *
     * @param kegiatanId ID dari kegiatan yang ingin diupload filenya.
     * @param tahapId    ID dari tahap yang ingin diupload filenya.
     * @param file       File yang akan diupload.
     * @return ResponseEntity yang berisi pesan sukses atau error.
     */
    @LogActivity(description = "Uploaded file for tahap", activityType = ActivityType.UPLOAD, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Mengupload File untuk Tahap", description = "Mengupload file untuk tahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mengupload file"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "500", description = "Failed to upload file")
    })
    @PostMapping("/{kegiatanId}/{tahapId}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable Long kegiatanId,
            @PathVariable int tahapId,
            @RequestParam("file") MultipartFile file) {

        try {
            tahapService.uploadFileForTahap(kegiatanId, tahapId, file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Mengambil daftar file yang diupload untuk tahap tertentu.
     *
     * @param kegiatanId ID dari kegiatan yang ingin diambil daftarnya.
     * @param tahapId    ID dari tahap yang ingin diambil daftarnya.
     * @return ResponseEntity yang berisi daftar nama file yang diupload.
     */
    @LogActivity(description = "Retrieved uploaded files for tahap", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Mengambil Daftar File yang Diupload", description = "Mengambil daftar file yang diupload untuk tahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar file"),
            @ApiResponse(responseCode = "404", description = "Activity or stage not found")
    })
    @GetMapping("/{kegiatanId}/{tahapId}/files")
    public ResponseEntity<List<String>> getUploadedFiles(
            @PathVariable Long kegiatanId,
            @PathVariable int tahapId) {

        List<String> files = tahapService.getUploadedFilesForTahap(kegiatanId, tahapId);
        return ResponseEntity.ok(files);
    }

    /**
     * Mengunduh file yang diupload untuk tahap tertentu.
     *
     * @param kegiatanId ID dari kegiatan yang ingin diunduh filenya.
     * @param tahapId    ID dari tahap yang ingin diunduh filenya.
     * @param filename   Nama file yang akan diunduh.
     * @return ResponseEntity yang berisi file sebagai resource.
     */
    @LogActivity(description = "Downloaded file for tahap", activityType = ActivityType.DOWNLOAD, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Mengunduh File yang Diupload", description = "Mengunduh file yang diupload untuk tahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mengunduh file"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{kegiatanId}/{tahapId}/files/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long kegiatanId,
            @PathVariable int tahapId,
            @PathVariable String filename) {

        try {
            Resource resource = fileUploadService.loadFileAsResource(kegiatanId, tahapId, filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update tanggal perencanaan subtahap
     */
    @LogActivity(description = "Updated subtahap planning date", activityType = ActivityType.UPDATE, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Tanggal Perencanaan Subtahap", description = "Update tanggal perencanaan untuk subtahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil update tanggal perencanaan"),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @PostMapping("/{kegiatanId}/{tahap}/{subtahap}/tanggal-perencanaan")
    public ResponseEntity<Void> updateTanggalPerencanaan(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap,
            @RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        tahapService.updateSubtahapTanggalPerencanaan(kegiatanId, tahap, subtahap, tanggal);
        return ResponseEntity.ok().build();
    }

    /**
     * Update tanggal realisasi subtahap
     */
    @LogActivity(description = "Updated subtahap realization date", activityType = ActivityType.UPDATE, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Tanggal Realisasi Subtahap", description = "Update tanggal realisasi untuk subtahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil update tanggal realisasi"),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @PostMapping("/{kegiatanId}/{tahap}/{subtahap}/tanggal-realisasi")
    public ResponseEntity<Void> updateTanggalRealisasi(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap,
            @RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        tahapService.updateSubtahapTanggalRealisasi(kegiatanId, tahap, subtahap, tanggal);
        return ResponseEntity.ok().build();
    }

    /**
     * Get tanggal perencanaan subtahap
     */
    @LogActivity(description = "Retrieved subtahap planning date", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Get Tanggal Perencanaan Subtahap", description = "Mendapatkan tanggal perencanaan untuk subtahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mendapatkan tanggal perencanaan"),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @GetMapping("/{kegiatanId}/{tahap}/{subtahap}/tanggal-perencanaan")
    public ResponseEntity<LocalDate> getTanggalPerencanaan(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap) {
        LocalDate tanggal = tahapService.getSubtahapTanggalPerencanaan(kegiatanId, tahap, subtahap);
        return ResponseEntity.ok(tanggal);
    }

    /**
     * Get tanggal realisasi subtahap
     */
    @LogActivity(description = "Retrieved subtahap realization date", activityType = ActivityType.VIEW, entityType = EntityType.TAHAP, severity = LogSeverity.LOW)
    @Operation(summary = "Get Tanggal Realisasi Subtahap", description = "Mendapatkan tanggal realisasi untuk subtahap tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil mendapatkan tanggal realisasi"),
            @ApiResponse(responseCode = "404", description = "Activity, stage, or substage not found")
    })
    @GetMapping("/{kegiatanId}/{tahap}/{subtahap}/tanggal-realisasi")
    public ResponseEntity<LocalDate> getTanggalRealisasi(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap) {
        LocalDate tanggal = tahapService.getSubtahapTanggalRealisasi(kegiatanId, tahap, subtahap);
        return ResponseEntity.ok(tanggal);
    }

    /**
     * Update multiple dates for a subtahap at once
     */
    @LogActivity(description = "Updated subtahap dates", activityType = ActivityType.UPDATE, entityType = EntityType.TAHAP, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Tanggal Subtahap", description = "Update tanggal perencanaan dan realisasi untuk subtahap sekaligus")
    @PostMapping("/{kegiatanId}/{tahap}/{subtahap}/tanggal")
    public ResponseEntity<Void> updateSubtahapDates(
            @PathVariable Long kegiatanId,
            @PathVariable int tahap,
            @PathVariable int subtahap,
            @RequestBody SubtahapDateRequest request) {

        if (request.getTanggalPerencanaan() != null) {
            tahapService.updateSubtahapTanggalPerencanaan(kegiatanId, tahap, subtahap, request.getTanggalPerencanaan());
        }

        if (request.getTanggalRealisasi() != null) {
            tahapService.updateSubtahapTanggalRealisasi(kegiatanId, tahap, subtahap, request.getTanggalRealisasi());
        }

        return ResponseEntity.ok().build();
    }
}
