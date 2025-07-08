package com.sms.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.dto.ActivityLogDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.payload.ApiErrorResponse;
import com.sms.service.ActivityLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST API for Activity Log operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Operation(summary = "Menampilkan Semua Log Aktivitas", description = "Menampilkan daftar semua log aktivitas dengan pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan log aktivitas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityLogDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<Page<ActivityLogDto>> getAllActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getAllActivityLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Menampilkan Detail Log Aktivitas", description = "Menampilkan detail log aktivitas berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil menampilkan detail log aktivitas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityLogDto.class))),
            @ApiResponse(responseCode = "404", description = "Log aktivitas tidak ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLogDto> getActivityLogById(@PathVariable Long id) {
        ActivityLogDto log = activityLogService.getActivityLogById(id);
        return ResponseEntity.ok(log);
    }

    @Operation(summary = "Menampilkan Log Aktivitas User", description = "Menampilkan log aktivitas berdasarkan ID user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityLogDto>> getActivityLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getActivityLogsByUserId(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Menampilkan Log Aktivitas yang Belum Dibaca", description = "Menampilkan daftar log aktivitas yang belum dibaca")
    @GetMapping("/unread")
    public ResponseEntity<Page<ActivityLogDto>> getUnreadLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getUnreadLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Menampilkan Log Aktivitas yang Belum Dibaca untuk User", description = "Menampilkan log aktivitas yang belum dibaca untuk user tertentu")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<ActivityLogDto>> getUnreadLogsByUserId(@PathVariable Long userId) {
        List<ActivityLogDto> logs = activityLogService.getUnreadLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Menampilkan Aktivitas Terkini", description = "Menampilkan aktivitas dalam beberapa jam terakhir")
    @GetMapping("/recent")
    public ResponseEntity<Page<ActivityLogDto>> getRecentActivities(
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getRecentActivities(hours, pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Mencari Log Aktivitas", description = "Mencari log aktivitas berdasarkan kata kunci")
    @GetMapping("/search")
    public ResponseEntity<Page<ActivityLogDto>> searchActivities(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.searchActivities(query, pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Filter Log Aktivitas", description = "Filter log aktivitas berdasarkan berbagai kriteria")
    @GetMapping("/filter")
    public ResponseEntity<Page<ActivityLogDto>> getActivitiesWithFilters(
            @RequestParam(required = false) ActivityType activityType,
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(required = false) LogSeverity severity,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getActivitiesWithFilters(
                activityType, entityType, severity, userId, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Log Aktivitas berdasarkan Rentang Tanggal", description = "Menampilkan log aktivitas dalam rentang tanggal tertentu")
    @GetMapping("/date-range")
    public ResponseEntity<Page<ActivityLogDto>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogDto> logs = activityLogService.getActivitiesByDateRange(start, end, pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Log Aktivitas berdasarkan Tipe Aktivitas", description = "Menampilkan log aktivitas berdasarkan tipe aktivitas")
    @GetMapping("/activity-type/{activityType}")
    public ResponseEntity<List<ActivityLogDto>> getActivitiesByType(@PathVariable ActivityType activityType) {
        List<ActivityLogDto> logs = activityLogService.getActivitiesByType(activityType);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Log Aktivitas berdasarkan Tipe Entity", description = "Menampilkan log aktivitas berdasarkan tipe entity")
    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<List<ActivityLogDto>> getActivitiesByEntityType(@PathVariable EntityType entityType) {
        List<ActivityLogDto> logs = activityLogService.getActivitiesByEntityType(entityType);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Log Aktivitas berdasarkan Tingkat Keparahan", description = "Menampilkan log aktivitas berdasarkan tingkat keparahan")
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<ActivityLogDto>> getActivitiesBySeverity(@PathVariable LogSeverity severity) {
        List<ActivityLogDto> logs = activityLogService.getActivitiesBySeverity(severity);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Log Aktivitas untuk Entity Tertentu", description = "Menampilkan log aktivitas untuk entity tertentu")
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ActivityLogDto>> getActivitiesByEntity(
            @PathVariable EntityType entityType,
            @PathVariable Long entityId) {
        List<ActivityLogDto> logs = activityLogService.getActivitiesByEntity(entityType, entityId);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Tandai sebagai Telah Dibaca", description = "Menandai log aktivitas sebagai telah dibaca")
    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        activityLogService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Activity log marked as read"));
    }

    @Operation(summary = "Tandai Semua sebagai Telah Dibaca", description = "Menandai semua log aktivitas user sebagai telah dibaca")
    @PatchMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAsReadByUserId(@PathVariable Long userId) {
        activityLogService.markAllAsReadByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "All activity logs marked as read for user"));
    }

    @Operation(summary = "Statistik Log Aktivitas", description = "Menampilkan statistik umum log aktivitas")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getActivityStatistics() {
        Map<String, Object> statistics = activityLogService.getActivityStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Tipe Aktivitas", description = "Menampilkan statistik berdasarkan tipe aktivitas")
    @GetMapping("/statistics/activity-types")
    public ResponseEntity<Map<String, Long>> getActivityTypeStatistics() {
        Map<String, Long> statistics = activityLogService.getActivityTypeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Tipe Entity", description = "Menampilkan statistik berdasarkan tipe entity")
    @GetMapping("/statistics/entity-types")
    public ResponseEntity<Map<String, Long>> getEntityTypeStatistics() {
        Map<String, Long> statistics = activityLogService.getEntityTypeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Tingkat Keparahan", description = "Menampilkan statistik berdasarkan tingkat keparahan")
    @GetMapping("/statistics/severities")
    public ResponseEntity<Map<String, Long>> getSeverityStatistics() {
        Map<String, Long> statistics = activityLogService.getSeverityStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Statistik Harian", description = "Menampilkan statistik aktivitas harian")
    @GetMapping("/statistics/daily")
    public ResponseEntity<Map<String, Long>> getDailyActivityCount(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Long> statistics = activityLogService.getDailyActivityCount(days);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Jumlah Log yang Belum Dibaca", description = "Menampilkan jumlah log aktivitas yang belum dibaca")
    @GetMapping("/count/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Long count = activityLogService.getUnreadCount();
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @Operation(summary = "Jumlah Log yang Belum Dibaca untuk User", description = "Menampilkan jumlah log aktivitas yang belum dibaca untuk user tertentu")
    @GetMapping("/user/{userId}/count/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCountByUserId(@PathVariable Long userId) {
        Long count = activityLogService.getUnreadCountByUserId(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @Operation(summary = "Jumlah Aktivitas Terkini", description = "Menampilkan jumlah aktivitas dalam beberapa jam terakhir")
    @GetMapping("/count/recent")
    public ResponseEntity<Map<String, Long>> getRecentActivityCount(
            @RequestParam(defaultValue = "24") int hours) {
        Long count = activityLogService.getRecentActivityCount(hours);
        return ResponseEntity.ok(Map.of("recentCount", count));
    }

    @Operation(summary = "Proses Notifikasi", description = "Memproses notifikasi untuk log aktivitas yang memerlukan notifikasi")
    @PostMapping("/process-notifications")
    public ResponseEntity<Map<String, String>> processNotifications() {
        activityLogService.processNotifications();
        return ResponseEntity.ok(Map.of("message", "Notifications processed successfully"));
    }

    @Operation(summary = "Log yang Memerlukan Notifikasi", description = "Menampilkan log aktivitas yang memerlukan notifikasi")
    @GetMapping("/notifications/pending")
    public ResponseEntity<List<ActivityLogDto>> getLogsNeedingNotification() {
        List<ActivityLogDto> logs = activityLogService.getLogsNeedingNotification();
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Bersihkan Log Lama", description = "Menghapus log aktivitas yang lebih lama dari jumlah hari yang ditentukan")
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldLogs(
            @RequestParam(defaultValue = "90") int daysToKeep) {
        activityLogService.cleanupOldLogs(daysToKeep);
        return ResponseEntity.ok(Map.of("message", "Old activity logs cleaned up successfully"));
    }

    @Operation(summary = "Hapus Log Aktivitas", description = "Menghapus log aktivitas berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteActivityLog(@PathVariable Long id) {
        activityLogService.deleteActivityLog(id);
        return ResponseEntity.ok(Map.of("message", "Activity log deleted successfully"));
    }

    @Operation(summary = "Hapus Multiple Log Aktivitas", description = "Menghapus multiple log aktivitas berdasarkan daftar ID")
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, String>> deleteActivityLogs(@RequestBody List<Long> ids) {
        activityLogService.deleteActivityLogs(ids);
        return ResponseEntity.ok(Map.of("message", String.format("%d activity logs deleted successfully", ids.size())));
    }

    @Operation(summary = "Log Manual", description = "Membuat log aktivitas secara manual")
    @PostMapping("/manual")
    public ResponseEntity<ActivityLogDto> createManualLog(@RequestBody Map<String, Object> logRequest) {
        String description = (String) logRequest.get("description");
        ActivityType activityType = ActivityType.valueOf((String) logRequest.get("activityType"));
        EntityType entityType = EntityType.valueOf((String) logRequest.get("entityType"));
        LogSeverity severity = LogSeverity.valueOf((String) logRequest.get("severity"));

        Long entityId = logRequest.get("entityId") != null ? Long.valueOf(logRequest.get("entityId").toString()) : null;
        String entityName = (String) logRequest.get("entityName");

        ActivityLogDto log = activityLogService.logActivity(description, activityType,
                entityType, entityId, entityName, severity);
        return ResponseEntity.ok(log);
    }
}