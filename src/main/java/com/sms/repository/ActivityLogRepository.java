package com.sms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sms.entity.ActivityLog;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;

/**
 * Repository untuk Activity Log
 * 
 * @author pinaa
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

        // Find by user
        List<ActivityLog> findAllByUserIdOrderByCreatedAtDesc(Long userId);

        Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

        // Find by activity type
        List<ActivityLog> findAllByActivityTypeOrderByCreatedAtDesc(ActivityType activityType);

        Page<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(ActivityType activityType, Pageable pageable);

        // Find by entity type
        List<ActivityLog> findAllByEntityTypeOrderByCreatedAtDesc(EntityType entityType);

        Page<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(EntityType entityType, Pageable pageable);

        // Find by entity type and entity id
        List<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType entityType, Long entityId);

        // Find by severity
        List<ActivityLog> findAllBySeverityOrderByCreatedAtDesc(LogSeverity severity);

        Page<ActivityLog> findBySeverityOrderByCreatedAtDesc(LogSeverity severity, Pageable pageable);

        // Find unread logs
        @Query("SELECT al FROM ActivityLog al WHERE al.isRead = false ORDER BY al.createdAt DESC")
        List<ActivityLog> findAllUnreadLogs();

        @Query("SELECT al FROM ActivityLog al WHERE al.isRead = false ORDER BY al.createdAt DESC")
        Page<ActivityLog> findUnreadLogs(Pageable pageable);

        @Query("SELECT al FROM ActivityLog al WHERE al.user.id = :userId AND al.isRead = false ORDER BY al.createdAt DESC")
        List<ActivityLog> findUnreadLogsByUserId(@Param("userId") Long userId);

        // Recent activities
        @Query("SELECT al FROM ActivityLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
        List<ActivityLog> findAllRecentActivities(@Param("since") LocalDateTime since);

        @Query("SELECT al FROM ActivityLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
        Page<ActivityLog> findRecentActivities(@Param("since") LocalDateTime since, Pageable pageable);

        // Date range queries
        @Query("SELECT al FROM ActivityLog al WHERE al.createdAt BETWEEN :start AND :end ORDER BY al.createdAt DESC")
        List<ActivityLog> findAllByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("SELECT al FROM ActivityLog al WHERE al.createdAt BETWEEN :start AND :end ORDER BY al.createdAt DESC")
        Page<ActivityLog> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                        Pageable pageable);

        // Search activities
        @Query("SELECT al FROM ActivityLog al WHERE " +
                        "LOWER(al.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(al.entityName) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "ORDER BY al.createdAt DESC")
        List<ActivityLog> findAllBySearchQuery(@Param("query") String query);

        @Query("SELECT al FROM ActivityLog al WHERE " +
                        "LOWER(al.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(al.entityName) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "ORDER BY al.createdAt DESC")
        Page<ActivityLog> searchActivities(@Param("query") String query, Pageable pageable);

        // Count queries
        @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.isRead = false")
        Long countUnreadLogs();

        @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.user.id = :userId AND al.isRead = false")
        Long countUnreadLogsByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.createdAt >= :since")
        Long countRecentActivities(@Param("since") LocalDateTime since);

        @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.severity = :severity")
        Long countBySeverity(@Param("severity") LogSeverity severity);

        // Statistics queries
        @Query("SELECT al.activityType, COUNT(al) FROM ActivityLog al GROUP BY al.activityType")
        List<Object[]> getActivityTypeStatistics();

        @Query("SELECT al.entityType, COUNT(al) FROM ActivityLog al GROUP BY al.entityType")
        List<Object[]> getEntityTypeStatistics();

        @Query("SELECT al.severity, COUNT(al) FROM ActivityLog al GROUP BY al.severity")
        List<Object[]> getSeverityStatistics();

        // @Query("SELECT DATE(al.createdAt), COUNT(al) FROM ActivityLog al WHERE
        // al.createdAt >= :since GROUP BY DATE(al.createdAt) ORDER BY
        // DATE(al.createdAt)")
        // List<Object[]> getDailyActivityCount(@Param("since") LocalDateTime since);

        @Query("SELECT FUNCTION('DATE', al.createdAt), COUNT(al) FROM ActivityLog al WHERE al.createdAt >= :since GROUP BY FUNCTION('DATE', al.createdAt) ORDER BY FUNCTION('DATE', al.createdAt)")
        List<Object[]> getDailyActivityCount(@Param("since") LocalDateTime since);

        // Notification queries
        @Query("SELECT al FROM ActivityLog al WHERE al.notificationSent = false AND al.severity IN ('CRITICAL', 'HIGH') ORDER BY al.createdAt DESC")
        List<ActivityLog> findLogsNeedingNotification();

        // Update queries
        @Modifying
        @Transactional
        @Query("UPDATE ActivityLog al SET al.isRead = true WHERE al.id = :id")
        int markAsRead(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE ActivityLog al SET al.isRead = true WHERE al.user.id = :userId")
        int markAllAsReadByUserId(@Param("userId") Long userId);

        @Modifying
        @Transactional
        @Query("UPDATE ActivityLog al SET al.notificationSent = true WHERE al.id = :id")
        int markNotificationSent(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE ActivityLog al SET al.notificationSent = true WHERE al.id IN :ids")
        int markNotificationSentBatch(@Param("ids") List<Long> ids);

        // Cleanup old logs (for maintenance)
        @Modifying
        @Transactional
        @Query("DELETE FROM ActivityLog al WHERE al.createdAt < :before")
        int deleteOldLogs(@Param("before") LocalDateTime before);

        // Complex filters
        @Query("SELECT al FROM ActivityLog al WHERE " +
                        "(:activityType IS NULL OR al.activityType = :activityType) AND " +
                        "(:entityType IS NULL OR al.entityType = :entityType) AND " +
                        "(:severity IS NULL OR al.severity = :severity) AND " +
                        "(:userId IS NULL OR al.user.id = :userId) AND " +
                        "(:startDate IS NULL OR al.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR al.createdAt <= :endDate) " +
                        "ORDER BY al.createdAt DESC")
        Page<ActivityLog> findWithFilters(
                        @Param("activityType") ActivityType activityType,
                        @Param("entityType") EntityType entityType,
                        @Param("severity") LogSeverity severity,
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);
}