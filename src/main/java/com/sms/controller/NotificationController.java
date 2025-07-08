package com.sms.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST API for Notification operations
 * 
 * @author pinaa
 */
@CrossOrigin
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Kirim Notifikasi ke User", description = "Mengirim notifikasi ke user tertentu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifikasi berhasil dikirim"),
            @ApiResponse(responseCode = "404", description = "User tidak ditemukan")
    })
    @PostMapping("/send-to-user/{userId}")
    public ResponseEntity<Map<String, String>> sendNotificationToUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> notificationRequest) {

        String title = (String) notificationRequest.get("title");
        String message = (String) notificationRequest.get("message");
        LogSeverity severity = LogSeverity.valueOf((String) notificationRequest.getOrDefault("severity", "MEDIUM"));

        notificationService.sendNotificationToUser(userId, title, message, severity);

        return ResponseEntity.ok(Map.of("message", "Notification sent successfully to user"));
    }

    @Operation(summary = "Kirim Notifikasi Broadcast", description = "Mengirim notifikasi ke semua user")
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> sendBroadcastNotification(
            @RequestBody Map<String, Object> notificationRequest) {

        String title = (String) notificationRequest.get("title");
        String message = (String) notificationRequest.get("message");
        LogSeverity severity = LogSeverity.valueOf((String) notificationRequest.getOrDefault("severity", "MEDIUM"));

        notificationService.sendBroadcastNotification(title, message, severity);

        return ResponseEntity.ok(Map.of("message", "Broadcast notification sent successfully"));
    }

    @Operation(summary = "Kirim Notifikasi berdasarkan Role", description = "Mengirim notifikasi ke semua user dengan role tertentu")
    @PostMapping("/send-to-role/{roleName}")
    public ResponseEntity<Map<String, String>> sendNotificationToRole(
            @PathVariable String roleName,
            @RequestBody Map<String, Object> notificationRequest) {

        String title = (String) notificationRequest.get("title");
        String message = (String) notificationRequest.get("message");
        LogSeverity severity = LogSeverity.valueOf((String) notificationRequest.getOrDefault("severity", "MEDIUM"));

        notificationService.sendNotificationToRole(roleName, title, message, severity);

        return ResponseEntity.ok(Map.of("message", "Role-based notification sent successfully"));
    }

    @Operation(summary = "Kirim Emergency Alert", description = "Mengirim alert darurat ke semua admin")
    @PostMapping("/emergency")
    public ResponseEntity<Map<String, String>> sendEmergencyAlert(
            @RequestBody Map<String, String> alertRequest) {

        String message = alertRequest.get("message");
        String details = alertRequest.getOrDefault("details", "");

        notificationService.sendEmergencyAlert(message, details);

        return ResponseEntity.ok(Map.of("message", "Emergency alert sent successfully"));
    }

    @Operation(summary = "Proses Notifikasi Manual", description = "Memproses notifikasi yang tertunda secara manual")
    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processNotifications() {
        // This will be handled by ActivityLogService
        return ResponseEntity.ok(Map.of("message", "Manual notification processing initiated"));
    }

    @Operation(summary = "Statistik Notifikasi", description = "Menampilkan statistik notifikasi")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics() {
        Map<String, Object> statistics = Map.of(
                "notificationsSentToday", notificationService.getNotificationsSentToday(),
                "unprocessedNotifications", notificationService.getUnprocessedNotifications());

        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Test Notifikasi", description = "Mengirim notifikasi test untuk debugging")
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "LOW") LogSeverity severity) {

        String message = "This is a test notification from the system";
        String title = "Test Notification";

        if (userId != null) {
            notificationService.sendNotificationToUser(userId, title, message, severity);
            return ResponseEntity.ok(Map.of("message", "Test notification sent to user " + userId));
        } else {
            notificationService.sendNotificationToRole("ROLE_ADMIN", title, message, severity);
            return ResponseEntity.ok(Map.of("message", "Test notification sent to all admins"));
        }
    }
}