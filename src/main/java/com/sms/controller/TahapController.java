package com.sms.controller;

import java.util.List;

import org.springframework.core.io.Resource;
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

import com.sms.dto.TahapStatusDto;
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
}
