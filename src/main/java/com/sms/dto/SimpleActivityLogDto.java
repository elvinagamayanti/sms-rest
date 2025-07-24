package com.sms.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO untuk Activity Log - untuk JSON response tanpa circular reference
 * Pattern yang sama seperti SimpleSatkerDto, SimpleUserDto, dll
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleActivityLogDto {

    private Long id;

    // User information sebagai simple fields (tidak reference ke entity)
    private Long userId;
    private String userEmail;
    private String userName;

    // Enum dengan JSON formatting untuk consistent output
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ActivityType activityType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EntityType entityType;

    // Entity information
    private Long entityId;
    private String entityName;

    // Core activity information
    private String description;
    private String details;

    // Technical information
    private String ipAddress;
    private String userAgent;

    // Severity dengan JSON formatting
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LogSeverity severity;

    // Timestamp dengan formatting yang konsisten
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Status flags
    private Boolean isRead;
    private Boolean notificationSent;

    // ====================================
    // HELPER METHODS
    // ====================================

    /**
     * Get combined activity description
     */
    public String getActivityDescription() {
        return String.format("%s %s: %s",
                activityType != null ? activityType.toString().toLowerCase() : "unknown",
                entityType != null ? entityType.toString().toLowerCase() : "unknown",
                description != null ? description : "");
    }

    /**
     * Get formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return createdAt != null ? createdAt.toString() : "";
    }

    /**
     * Get CSS class for severity badge
     */
    public String getSeverityBadgeClass() {
        if (severity == null)
            return "badge-secondary";

        return switch (severity) {
            case LOW -> "badge-success";
            case MEDIUM -> "badge-warning";
            case HIGH -> "badge-danger";
            case CRITICAL -> "badge-dark";
        };
    }

    /**
     * Get icon class for activity type
     */
    public String getActivityIconClass() {
        if (activityType == null)
            return "fas fa-info-circle";

        return switch (activityType) {
            case CREATE -> "fas fa-plus-circle";
            case UPDATE -> "fas fa-edit";
            case DELETE -> "fas fa-trash";
            case VIEW -> "fas fa-eye";
            case LOGIN -> "fas fa-sign-in-alt";
            case LOGOUT -> "fas fa-sign-out-alt";
            case UPLOAD -> "fas fa-upload";
            case DOWNLOAD -> "fas fa-download";
            case APPROVE -> "fas fa-check-circle";
            case REJECT -> "fas fa-times-circle";
            case SUBMIT -> "fas fa-paper-plane";
            case ASSIGN -> "fas fa-user-plus";
            case COMPLETE -> "fas fa-check";
            case CANCEL -> "fas fa-times";
            case RESTORE -> "fas fa-undo";
            case SYNC -> "fas fa-sync";
        };
    }

    /**
     * Get user display name with fallback
     */
    public String getUserDisplayName() {
        if (userName != null && !userName.trim().isEmpty()) {
            return userName;
        }
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            return userEmail;
        }
        return "System";
    }

    /**
     * Check if activity is recent (within last hour)
     */
    public boolean isRecent() {
        if (createdAt == null)
            return false;
        return createdAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    /**
     * Check if activity is today
     */
    public boolean isToday() {
        if (createdAt == null)
            return false;
        return createdAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * Check if activity is critical or high severity
     */
    public boolean isCritical() {
        return severity == LogSeverity.CRITICAL || severity == LogSeverity.HIGH;
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "1 day ago")
     */
    public String getRelativeTime() {
        if (createdAt == null)
            return "Unknown time";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activityTime = createdAt;

        long minutes = java.time.Duration.between(activityTime, now).toMinutes();
        long hours = java.time.Duration.between(activityTime, now).toHours();
        long days = java.time.Duration.between(activityTime, now).toDays();

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
    }

    /**
     * Get severity text in Indonesian
     */
    public String getSeverityText() {
        if (severity == null)
            return "Normal";

        return switch (severity) {
            case LOW -> "Rendah";
            case MEDIUM -> "Sedang";
            case HIGH -> "Tinggi";
            case CRITICAL -> "Kritis";
        };
    }

    /**
     * Get activity type text in Indonesian
     */
    public String getActivityTypeText() {
        if (activityType == null)
            return "Tidak Diketahui";

        return switch (activityType) {
            case CREATE -> "Membuat";
            case UPDATE -> "Mengubah";
            case DELETE -> "Menghapus";
            case VIEW -> "Melihat";
            case LOGIN -> "Masuk";
            case LOGOUT -> "Keluar";
            case UPLOAD -> "Mengunggah";
            case DOWNLOAD -> "Mengunduh";
            case APPROVE -> "Menyetujui";
            case REJECT -> "Menolak";
            case SUBMIT -> "Mengirim";
            case ASSIGN -> "Menugaskan";
            case COMPLETE -> "Menyelesaikan";
            case CANCEL -> "Membatalkan";
            case RESTORE -> "Memulihkan";
            case SYNC -> "Sinkronisasi";
        };
    }
}