package com.sms.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sms.dto.ActivityLogDto;
import com.sms.entity.ActivityLog;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.mapper.ActivityLogMapper;
import com.sms.repository.ActivityLogRepository;
import com.sms.service.ActivityLogService;
import com.sms.service.UserService;

/**
 * Implementation of Activity Log Service
 * 
 * @author pinaa
 */
@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;
    private final UserService userService;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, UserService userService) {
        this.activityLogRepository = activityLogRepository;
        this.userService = userService;
    }

    @Override
    public ActivityLogDto logActivity(String description, ActivityType activityType,
            EntityType entityType, LogSeverity severity) {
        return logActivity(getCurrentUser(), description, activityType, entityType, severity);
    }

    @Override
    public ActivityLogDto logActivity(String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, LogSeverity severity) {
        return logActivity(getCurrentUser(), description, activityType, entityType,
                entityId, entityName, severity);
    }

    @Override
    public ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, LogSeverity severity) {
        return logActivity(user, description, activityType, entityType, null, null, severity);
    }

    @Override
    public ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, LogSeverity severity) {
        return logActivity(user, description, activityType, entityType, entityId,
                entityName, null, severity, null, null);
    }

    @Override
    public ActivityLogDto logActivity(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId,
            String entityName, String details,
            LogSeverity severity, String ipAddress, String userAgent) {
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .user(user)
                    .userEmail(user != null ? user.getEmail() : "system")
                    .userName(user != null ? user.getName() : "System")
                    .activityType(activityType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .entityName(entityName)
                    .description(description)
                    .details(details)
                    .severity(severity)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .notificationSent(false)
                    .build();

            ActivityLog saved = activityLogRepository.save(activityLog);

            logger.info("Activity logged: {} {} by {}", activityType, entityType,
                    user != null ? user.getEmail() : "system");

            return ActivityLogMapper.mapToActivityLogDto(saved);

        } catch (Exception e) {
            logger.error("Error logging activity: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Async
    public void logActivityAsync(User user, String description, ActivityType activityType,
            EntityType entityType, Long entityId, String entityName,
            LogSeverity severity) {
        logActivity(user, description, activityType, entityType, entityId, entityName, severity);
    }

    @Override
    public List<ActivityLogDto> getAllActivityLogs() {
        return activityLogRepository.findAll().stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> getAllActivityLogs(Pageable pageable) {
        return activityLogRepository.findAll(pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public ActivityLogDto getActivityLogById(Long id) {
        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity log not found with id: " + id));
        return ActivityLogMapper.mapToActivityLogDto(activityLog);
    }

    @Override
    public List<ActivityLogDto> getActivityLogsByUserId(Long userId) {
        return activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> getActivityLogsByUserId(Long userId, Pageable pageable) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public List<ActivityLogDto> getUnreadLogs() {
        return activityLogRepository.findAllUnreadLogs().stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> getUnreadLogs(Pageable pageable) {
        return activityLogRepository.findUnreadLogs(pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public List<ActivityLogDto> getUnreadLogsByUserId(Long userId) {
        return activityLogRepository.findUnreadLogsByUserId(userId).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getRecentActivities(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityLogRepository.findAllRecentActivities(since).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> getRecentActivities(int hours, Pageable pageable) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityLogRepository.findRecentActivities(since, pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public List<ActivityLogDto> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end) {
        return activityLogRepository.findAllByDateRange(start, end).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return activityLogRepository.findByDateRange(start, end, pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public List<ActivityLogDto> searchActivities(String query) {
        return activityLogRepository.findAllBySearchQuery(query).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogDto> searchActivities(String query, Pageable pageable) {
        return activityLogRepository.searchActivities(query, pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public Page<ActivityLogDto> getActivitiesWithFilters(
            ActivityType activityType, EntityType entityType, LogSeverity severity,
            Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return activityLogRepository.findWithFilters(activityType, entityType, severity,
                userId, startDate, endDate, pageable)
                .map(ActivityLogMapper::mapToActivityLogDto);
    }

    @Override
    public List<ActivityLogDto> getActivitiesByType(ActivityType activityType) {
        return activityLogRepository.findAllByActivityTypeOrderByCreatedAtDesc(activityType).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivitiesByEntityType(EntityType entityType) {
        return activityLogRepository.findAllByEntityTypeOrderByCreatedAtDesc(entityType).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivitiesBySeverity(LogSeverity severity) {
        return activityLogRepository.findAllBySeverityOrderByCreatedAtDesc(severity).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivitiesByEntity(EntityType entityType, Long entityId) {
        return activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId).stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long id) {
        activityLogRepository.markAsRead(id);
        logger.debug("Activity log {} marked as read", id);
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        int updated = activityLogRepository.markAllAsReadByUserId(userId);
        logger.debug("Marked {} activity logs as read for user {}", updated, userId);
    }

    @Override
    public void markNotificationSent(Long id) {
        activityLogRepository.markNotificationSent(id);
        logger.debug("Activity log {} marked as notification sent", id);
    }

    @Override
    public void markNotificationSentBatch(List<Long> ids) {
        int updated = activityLogRepository.markNotificationSentBatch(ids);
        logger.debug("Marked {} activity logs as notification sent", updated);
    }

    @Override
    public Map<String, Object> getActivityStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("total", getTotalActivityLogs());
        statistics.put("unread", getUnreadCount());
        statistics.put("recent24h", getRecentActivityCount(24));
        statistics.put("critical", getCountBySeverity(LogSeverity.CRITICAL));
        statistics.put("high", getCountBySeverity(LogSeverity.HIGH));
        statistics.put("activityTypes", getActivityTypeStatistics());
        statistics.put("entityTypes", getEntityTypeStatistics());
        statistics.put("severities", getSeverityStatistics());

        return statistics;
    }

    @Override
    public Map<String, Long> getActivityTypeStatistics() {
        List<Object[]> results = activityLogRepository.getActivityTypeStatistics();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            ActivityType type = (ActivityType) result[0];
            Long count = (Long) result[1];
            statistics.put(type.toString(), count);
        }

        return statistics;
    }

    @Override
    public Map<String, Long> getEntityTypeStatistics() {
        List<Object[]> results = activityLogRepository.getEntityTypeStatistics();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            EntityType type = (EntityType) result[0];
            Long count = (Long) result[1];
            statistics.put(type.toString(), count);
        }

        return statistics;
    }

    @Override
    public Map<String, Long> getSeverityStatistics() {
        List<Object[]> results = activityLogRepository.getSeverityStatistics();
        Map<String, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            LogSeverity severity = (LogSeverity) result[0];
            Long count = (Long) result[1];
            statistics.put(severity.toString(), count);
        }

        return statistics;
    }

    @Override
    public Map<String, Long> getDailyActivityCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = activityLogRepository.getDailyActivityCount(since);
        Map<String, Long> dailyCount = new HashMap<>();

        for (Object[] result : results) {
            String date = result[0].toString();
            Long count = (Long) result[1];
            dailyCount.put(date, count);
        }

        return dailyCount;
    }

    @Override
    public Long getTotalActivityLogs() {
        return activityLogRepository.count();
    }

    @Override
    public Long getUnreadCount() {
        return activityLogRepository.countUnreadLogs();
    }

    @Override
    public Long getUnreadCountByUserId(Long userId) {
        return activityLogRepository.countUnreadLogsByUserId(userId);
    }

    @Override
    public Long getRecentActivityCount(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityLogRepository.countRecentActivities(since);
    }

    @Override
    public Long getCountBySeverity(LogSeverity severity) {
        return activityLogRepository.countBySeverity(severity);
    }

    @Override
    public List<ActivityLogDto> getLogsNeedingNotification() {
        return activityLogRepository.findLogsNeedingNotification().stream()
                .map(ActivityLogMapper::mapToActivityLogDto)
                .collect(Collectors.toList());
    }

    @Override
    public void processNotifications() {
        List<ActivityLog> logs = activityLogRepository.findLogsNeedingNotification();

        for (ActivityLog log : logs) {
            try {
                // Here you would implement actual notification logic
                // For example: send email, push notification, etc.
                sendNotification(log);

                // Mark as notification sent
                markNotificationSent(log.getId());

            } catch (Exception e) {
                logger.error("Error sending notification for activity log {}: {}",
                        log.getId(), e.getMessage());
            }
        }

        logger.info("Processed {} notifications", logs.size());
    }

    @Override
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        int deleted = activityLogRepository.deleteOldLogs(cutoff);
        logger.info("Cleaned up {} old activity logs older than {} days", deleted, daysToKeep);
    }

    @Override
    public void deleteActivityLog(Long id) {
        activityLogRepository.deleteById(id);
        logger.debug("Deleted activity log {}", id);
    }

    @Override
    public void deleteActivityLogs(List<Long> ids) {
        activityLogRepository.deleteAllById(ids);
        logger.debug("Deleted {} activity logs", ids.size());
    }

    // Helper methods for common logging scenarios
    @Override
    public void logUserLogin(User user, String ipAddress, String userAgent) {
        logActivity(user, "User logged in", ActivityType.LOGIN, EntityType.USER,
                user.getId(), user.getName(), null, LogSeverity.LOW, ipAddress, userAgent);
    }

    @Override
    public void logUserLogout(User user, String ipAddress) {
        logActivity(user, "User logged out", ActivityType.LOGOUT, EntityType.USER,
                user.getId(), user.getName(), null, LogSeverity.LOW, ipAddress, null);
    }

    @Override
    public void logEntityCreated(User user, EntityType entityType, Long entityId, String entityName) {
        logActivity(user, String.format("Created %s: %s", entityType.toString().toLowerCase(), entityName),
                ActivityType.CREATE, entityType, entityId, entityName, LogSeverity.LOW);
    }

    @Override
    public void logEntityUpdated(User user, EntityType entityType, Long entityId, String entityName) {
        logActivity(user, String.format("Updated %s: %s", entityType.toString().toLowerCase(), entityName),
                ActivityType.UPDATE, entityType, entityId, entityName, LogSeverity.LOW);
    }

    @Override
    public void logEntityDeleted(User user, EntityType entityType, Long entityId, String entityName) {
        logActivity(user, String.format("Deleted %s: %s", entityType.toString().toLowerCase(), entityName),
                ActivityType.DELETE, entityType, entityId, entityName, LogSeverity.MEDIUM);
    }

    @Override
    public void logEntityViewed(User user, EntityType entityType, Long entityId, String entityName) {
        logActivity(user, String.format("Viewed %s: %s", entityType.toString().toLowerCase(), entityName),
                ActivityType.VIEW, entityType, entityId, entityName, LogSeverity.LOW);
    }

    @Override
    public void logFileUploaded(User user, String fileName, Long fileSize) {
        String details = String.format("File size: %d bytes", fileSize);
        logActivity(user, String.format("Uploaded file: %s", fileName),
                ActivityType.UPLOAD, EntityType.FILE, null, fileName,
                details, LogSeverity.LOW, null, null);
    }

    @Override
    public void logFileDownloaded(User user, String fileName) {
        logActivity(user, String.format("Downloaded file: %s", fileName),
                ActivityType.DOWNLOAD, EntityType.FILE, null, fileName, LogSeverity.LOW);
    }

    @Override
    public void logSystemEvent(String description, LogSeverity severity) {
        logActivity(null, description, ActivityType.VIEW, EntityType.SYSTEM,
                null, "System", null, severity, null, null);
    }

    @Override
    public void logSecurityEvent(String description, String ipAddress, LogSeverity severity) {
        logActivity(null, description, ActivityType.VIEW, EntityType.SYSTEM,
                null, "Security", null, severity, ipAddress, null);
    }

    // Private helper methods
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                String email = authentication.getName();
                return userService.findUserByEmail(email);
            }
        } catch (Exception e) {
            logger.debug("Could not get current user: {}", e.getMessage());
        }
        return null;
    }

    private void sendNotification(ActivityLog log) {
        // Implement actual notification logic here
        // This could involve:
        // - Sending emails
        // - Push notifications
        // - WebSocket messages
        // - SMS alerts
        // etc.

        logger.info("Sending notification for activity: {} - {}",
                log.getActivityType(), log.getDescription());

        // Example implementation placeholder
        if (log.getSeverity() == LogSeverity.CRITICAL) {
            // Send immediate alert
        } else if (log.getSeverity() == LogSeverity.HIGH) {
            // Send priority notification
        }
    }
}