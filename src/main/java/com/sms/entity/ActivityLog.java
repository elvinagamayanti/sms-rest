package com.sms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity untuk menyimpan log aktivitas sistem
 * 
 * @author pinaa
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @Column(name = "user_email", nullable = true)
    private String userEmail;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    @Column(name = "entity_name", nullable = true)
    private String entityName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", nullable = true)
    private String ipAddress;

    @Column(name = "user_agent", nullable = true)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private LogSeverity severity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "notification_sent", nullable = false)
    @Builder.Default
    private Boolean notificationSent = false;

    // Enums
    public enum ActivityType {
        CREATE, UPDATE, DELETE, VIEW, LOGIN, LOGOUT,
        UPLOAD, DOWNLOAD, APPROVE, REJECT, SUBMIT,
        ASSIGN, COMPLETE, CANCEL, RESTORE, SYNC
    }

    public enum EntityType {
        USER, ROLE, SATKER, PROVINCE, PROGRAM, OUTPUT, KEGIATAN,
        DEPUTI, DIREKTORAT, TAHAP, FILE, SYSTEM
    }

    public enum LogSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Helper methods
    public String getActivityDescription() {
        return String.format("%s %s: %s",
                activityType.toString().toLowerCase(),
                entityType.toString().toLowerCase(),
                description);
    }

    public String getTimestamp() {
        return createdAt.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s by %s at %s",
                severity, entityType, activityType, userName, createdAt);
    }
}