package com.sms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sms.dto.ActivityLogDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;

/**
 * Service interface untuk Activity Log
 * 
 * @author pinaa
 */
public interface ActivityLogService {

    // Create activity log
    ActivityLogDto logActivity(String description, ActivityType activityType,
            EntityType entityType, LogSeverity severity);

    ActivityLogDto logActivity(String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, LogSeverity severity);

    ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, LogSeverity severity);

    ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, LogSeverity severity);

    ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, String details,
            LogSeverity severity, String ipAddress, String userAgent);

    // Read operations
    List<ActivityLogDto> getAllActivityLogs();

    Page<ActivityLogDto> getAllActivityLogs(Pageable pageable);

    ActivityLogDto getActivityLogById(Long id);

    List<ActivityLogDto> getActivityLogsByUserId(Long userId);

    Page<ActivityLogDto> getActivityLogsByUserId(Long userId, Pageable pageable);

    List<ActivityLogDto> getUnreadLogs();

    Page<ActivityLogDto> getUnreadLogs(Pageable pageable);

    List<ActivityLogDto> getUnreadLogsByUserId(Long userId);

    List<ActivityLogDto> getRecentActivities(int hours);

    Page<ActivityLogDto> getRecentActivities(int hours, Pageable pageable);

    List<ActivityLogDto> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end);

    Page<ActivityLogDto> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<ActivityLogDto> searchActivities(String query);

    Page<ActivityLogDto> searchActivities(String query, Pageable pageable);

    // Filter operations
    Page<ActivityLogDto> getActivitiesWithFilters(
            ActivityType activityType, EntityType entityType, LogSeverity severity,
            Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<ActivityLogDto> getActivitiesByType(ActivityType activityType);

    List<ActivityLogDto> getActivitiesByEntityType(EntityType entityType);

    List<ActivityLogDto> getActivitiesBySeverity(LogSeverity severity);

    List<ActivityLogDto> getActivitiesByEntity(EntityType entityType, Long entityId);

    // Update operations
    void markAsRead(Long id);

    void markAllAsReadByUserId(Long userId);

    void markNotificationSent(Long id);

    void markNotificationSentBatch(List<Long> ids);

    // Statistics
    Map<String, Object> getActivityStatistics();

    Map<String, Long> getActivityTypeStatistics();

    Map<String, Long> getEntityTypeStatistics();

    Map<String, Long> getSeverityStatistics();

    Map<String, Long> getDailyActivityCount(int days);

    // Count operations
    Long getTotalActivityLogs();

    Long getUnreadCount();

    Long getUnreadCountByUserId(Long userId);

    Long getRecentActivityCount(int hours);

    Long getCountBySeverity(LogSeverity severity);

    // Notification related
    List<ActivityLogDto> getLogsNeedingNotification();

    void processNotifications();

    // Maintenance
    void cleanupOldLogs(int daysToKeep);

    // Bulk operations
    void deleteActivityLog(Long id);

    void deleteActivityLogs(List<Long> ids);

    // Helper methods for common logging scenarios
    void logUserLogin(User user, String ipAddress, String userAgent);

    void logUserLogout(User user, String ipAddress);

    void logEntityCreated(User user, EntityType entityType, Long entityId, String entityName);

    void logEntityUpdated(User user, EntityType entityType, Long entityId, String entityName);

    void logEntityDeleted(User user, EntityType entityType, Long entityId, String entityName);

    void logEntityViewed(User user, EntityType entityType, Long entityId, String entityName);

    void logFileUploaded(User user, String fileName, Long fileSize);

    void logFileDownloaded(User user, String fileName);

    void logSystemEvent(String description, LogSeverity severity);

    void logSecurityEvent(String description, String ipAddress, LogSeverity severity);

    // Async logging (for performance)
    void logActivityAsync(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId, String entityName,
            LogSeverity severity);
}