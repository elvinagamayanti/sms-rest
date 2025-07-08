package com.sms.dto;

import java.time.LocalDateTime;

import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk Activity Log
 * 
 * @author pinaa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {

    private Long id;
    private User user;
    private String userEmail;
    private String userName;
    private ActivityType activityType;
    private EntityType entityType;
    private Long entityId;
    private String entityName;
    private String description;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LogSeverity severity;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private Boolean notificationSent;

    // Helper methods
    public String getActivityDescription() {
        return String.format("%s %s: %s",
                activityType.toString().toLowerCase(),
                entityType.toString().toLowerCase(),
                description);
    }

    public String getFormattedTimestamp() {
        return createdAt != null ? createdAt.toString() : "";
    }

    public String getSeverityBadge() {
        if (severity == null)
            return "secondary";

        return switch (severity) {
            case LOW -> "success";
            case MEDIUM -> "warning";
            case HIGH -> "danger";
            case CRITICAL -> "dark";
        };
    }

    public String getActivityIcon() {
        if (activityType == null)
            return "info-circle";

        return switch (activityType) {
            case CREATE -> "plus-circle";
            case UPDATE -> "edit";
            case DELETE -> "trash";
            case VIEW -> "eye";
            case LOGIN -> "log-in";
            case LOGOUT -> "log-out";
            case UPLOAD -> "upload";
            case DOWNLOAD -> "download";
            case APPROVE -> "check-circle";
            case REJECT -> "x-circle";
            case SUBMIT -> "send";
            case ASSIGN -> "user-plus";
            case COMPLETE -> "check";
            case CANCEL -> "x";
            case RESTORE -> "refresh-cw";
            case SYNC -> "refresh-ccw";
        };
    }
}