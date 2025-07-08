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
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find by activity type
    List<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(ActivityType activityType);

    Page<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(ActivityType activityType, Pageable pageable);

    // Find by entity type
    List<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(EntityType entityType);

    Page<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(EntityType entityType, Pageable pageable);

    // Find by entity type and entity id
    List<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType entityType, Long entityId);

    // Find by severity
    List<ActivityLog> findBySeverityOrderByCreatedAtDesc(LogSeverity severity);

    Page<ActivityLog> findBySeverityOrderByCreatedAtDesc(LogSeverity severity, Pageable pageable);

    // Find unread logs
    @Query("SELECT al FROM ActivityLog al WHERE al.isRead = false ORDER BY al.createdAt DESC")
    List<ActivityLog> findUnreadLogs();

    @Query("SELECT al FROM ActivityLog al WHERE al.isRead = false ORDER BY al.createdAt DESC")
    Page<ActivityLog> findUnreadLogs(Pageable pageable);

    // Find unread logs for specific user
    @Query("SELECT al FROM ActivityLog al WHERE al.user.id = :userId AND al.isRead = false ORDER BY al.createdAt DESC")
    List<ActivityLog> findUnreadLogsByUserId(@Param("userId") Long userId);

    // Find logs that need notification
    @Query("SELECT al FROM ActivityLog al WHERE al.notificationSent = false AND al.severity IN ('HIGH', 'CRITICAL') ORDER BY al.createdAt DESC")
    List<ActivityLog> findLogsNeedingNotification();

    // Find recent activities (last 24 hours)
    @Query("SELECT al FROM ActivityLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
    List<ActivityLog> findRecentActivities(@Param("since") LocalDateTime since);

    @Query("SELECT al FROM ActivityLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
    Page<ActivityLog> findRecentActivities(@Param("since") LocalDateTime since, Pageable pageable);

    // Find activities by date range
    @Query("SELECT al FROM ActivityLog al WHERE al.createdAt BETWEEN :start AND :end ORDER BY al.createdAt DESC")
    List<ActivityLog> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT al FROM ActivityLog al WHERE al.createdAt BETWEEN :start AND :end ORDER BY al.createdAt DESC")
    Page<ActivityLog> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
            Pageable pageable);

    // Search activities
    @Query("SELECT al FROM ActivityLog al WHERE " +
            "al.description LIKE CONCAT('%', :query, '%') OR " +
            "al.details LIKE CONCAT('%', :query, '%') OR " +
            "al.userName LIKE CONCAT('%', :query, '%') OR " +
            "al.userEmail LIKE CONCAT('%', :query, '%') " +
            "ORDER BY al.createdAt DESC")
    List<ActivityLog> searchActivities(@Param("query") String query);

    @Query("SELECT al FROM ActivityLog al WHERE " +
            "al.description LIKE CONCAT('%', :query, '%') OR " +
            "al.details LIKE CONCAT('%', :query, '%') OR " +
            "al.userName LIKE CONCAT('%', :query, '%') OR " +
            "al.userEmail LIKE CONCAT('%', :query, '%') " +
            "ORDER BY al.createdAt DESC")
    Page<ActivityLog> searchActivities(@Param("query") String query, Pageable pageable);

    // Statistics queries
    @Query("SELECT al.activityType, COUNT(al) FROM ActivityLog al GROUP BY al.activityType")
    List<Object[]> getActivityTypeStatistics();

    @Query("SELECT al.entityType, COUNT(al) FROM ActivityLog al GROUP BY al.entityType")
    List<Object[]> getEntityTypeStatistics();

    @Query("SELECT al.severity, COUNT(al) FROM ActivityLog al GROUP BY al.severity")
    List<Object[]> getSeverityStatistics();

    @Query("SELECT DATE(al.createdAt), COUNT(al) FROM ActivityLog al WHERE al.createdAt >= :since GROUP BY DATE(al.createdAt) ORDER BY DATE(al.createdAt)")
    List<Object[]> getDailyActivityCount(@Param("since") LocalDateTime since);

    // Count queries
    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.isRead = false")
    Long countUnreadLogs();

    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.user.id = :userId AND al.isRead = false")
    Long countUnreadLogsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.createdAt >= :since")
    Long countRecentActivities(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.severity = :severity")
    Long countBySeverity(@Param("severity") LogSeverity severity);

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