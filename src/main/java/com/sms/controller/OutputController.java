package com.sms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.sms.dto.OutputDto;
import com.sms.dto.SimpleOutputDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.mapper.OutputMapper;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.OutputService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST API for Output operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/outputs")
public class OutputController {
    private final OutputService outputService;

    public OutputController(OutputService outputService) {
        this.outputService = outputService;
    }

    /**
     * Get all outputs
     * 
     * @return list of outputs
     */
    @LogActivity(description = "Retrieved all output list", activityType = ActivityType.VIEW, entityType = EntityType.OUTPUT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Daftar Output", description = "Menampilkan daftar seluruh output yang terdaftar pada sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan daftar output", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutputDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    // public ResponseEntity<List<OutputDto>> getAllOutputs() {
    // List<OutputDto> outputDtos = this.outputService.ambilDaftarOutput();
    // return ResponseEntity.ok(outputDtos);
    // }
    public ResponseEntity<List<SimpleOutputDto>> getAllOutputs() {
        List<OutputDto> output = outputService.ambilDaftarOutput();
        List<SimpleOutputDto> outputDtos = output.stream()
                .map(OutputMapper::mapOutputDtoToSimpleOutputDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(outputDtos);
    }

    /**
     * Get output by id
     * 
     * @param id output id
     * @return output details
     */
    @LogActivity(description = "Retrieved output by ID", activityType = ActivityType.VIEW, entityType = EntityType.OUTPUT, severity = LogSeverity.LOW)
    @Operation(summary = "Menampilkan Output berdasarkan ID", description = "Menampilkan detail output berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan output berdasarkan ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Output tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    // public ResponseEntity<OutputDto> getOutputById(@PathVariable("id") Long id) {
    // OutputDto outputDto = outputService.cariOutputById(id);
    // return ResponseEntity.ok(outputDto);
    // }
    public ResponseEntity<SimpleOutputDto> getOutputById(@PathVariable("id") Long id) {
        OutputDto outputDto = outputService.cariOutputById(id);
        SimpleOutputDto simpleOutputDto = OutputMapper.mapOutputDtoToSimpleOutputDto(outputDto);
        return ResponseEntity.ok(simpleOutputDto);
    }

    /**
     * Create new output
     * 
     * @param outputDto output data
     * @return created output
     */
    @LogActivity(description = "Created new output", activityType = ActivityType.CREATE, entityType = EntityType.OUTPUT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Membuat Output Baru", description = "Membuat output baru dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Berhasil membuat output baru", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OutputDto> createOutput(@Valid @RequestBody OutputDto outputDto) {
        outputService.simpanDataOutput(outputDto);
        return new ResponseEntity<>(outputDto, HttpStatus.CREATED);
    }

    /**
     * Update existing output
     * 
     * @param id        output id
     * @param outputDto output data
     * @return updated output
     */
    @LogActivity(description = "Updated output by ID", activityType = ActivityType.UPDATE, entityType = EntityType.OUTPUT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Memperbarui Output", description = "Memperbarui output yang sudah ada dengan data yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui output", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Output tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SimpleOutputDto> updateOutput(
            @PathVariable("id") Long id,
            @Valid @RequestBody OutputDto outputDto) {

        // Set the ID from the path variable
        outputDto.setId(id);
        outputService.perbaruiDataOutput(outputDto);
        SimpleOutputDto simpleOutputDto = OutputMapper.mapOutputDtoToSimpleOutputDto(outputDto);
        return ResponseEntity.ok(simpleOutputDto);
    }

    /**
     * Delete output by id
     * 
     * @param id output id
     * @return success message
     */
    @LogActivity(description = "Deleted output by ID", activityType = ActivityType.DELETE, entityType = EntityType.OUTPUT, severity = LogSeverity.HIGH)
    @Operation(summary = "Menghapus Output", description = "Menghapus output berdasarkan ID yang diberikan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menghapus output", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Output tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOutput(@PathVariable("id") Long id) {
        outputService.hapusDataOutput(id);
        return ResponseEntity.ok(Map.of("message", "Output with ID " + id + " deleted successfully"));
    }

    /**
     * Update partial output data
     * 
     * @param id      output id
     * @param updates fields to update
     * @return updated output
     */
    @LogActivity(description = "Partially updated output by ID", activityType = ActivityType.UPDATE, entityType = EntityType.OUTPUT, severity = LogSeverity.MEDIUM)
    @Operation(summary = "Update Sebagian Data Output", description = "Memperbarui sebagian field output tanpa harus mengisi semua field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil memperbarui sebagian data output", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Output tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<SimpleOutputDto> patchOutput(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {

        OutputDto outputDto = outputService.patchOutput(id, updates);
        SimpleOutputDto simpleOutputDto = OutputMapper.mapOutputDtoToSimpleOutputDto(outputDto);
        return ResponseEntity.ok(simpleOutputDto);
    }
}