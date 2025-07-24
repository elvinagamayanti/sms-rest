package com.sms.mapper;

import com.sms.dto.ActivityLogDto;
import com.sms.dto.SimpleActivityLogDto;
import com.sms.entity.ActivityLog;

/**
 * Mapper untuk Activity Log
 * 
 * @author pinaa
 */
public class ActivityLogMapper {

    public static ActivityLogDto mapToActivityLogDto(ActivityLog activityLog) {
        return ActivityLogDto.builder()
                .id(activityLog.getId())
                .user(activityLog.getUser())
                .userEmail(activityLog.getUserEmail())
                .userName(activityLog.getUserName())
                .activityType(activityLog.getActivityType())
                .entityType(activityLog.getEntityType())
                .entityId(activityLog.getEntityId())
                .entityName(activityLog.getEntityName())
                .description(activityLog.getDescription())
                .details(activityLog.getDetails())
                .ipAddress(activityLog.getIpAddress())
                .userAgent(activityLog.getUserAgent())
                .severity(activityLog.getSeverity())
                .createdAt(activityLog.getCreatedAt())
                .isRead(activityLog.getIsRead())
                .notificationSent(activityLog.getNotificationSent())
                .build();
    }

    public static ActivityLog mapToActivityLog(ActivityLogDto activityLogDto) {
        return ActivityLog.builder()
                .id(activityLogDto.getId())
                .user(activityLogDto.getUser())
                .userEmail(activityLogDto.getUserEmail())
                .userName(activityLogDto.getUserName())
                .activityType(activityLogDto.getActivityType())
                .entityType(activityLogDto.getEntityType())
                .entityId(activityLogDto.getEntityId())
                .entityName(activityLogDto.getEntityName())
                .description(activityLogDto.getDescription())
                .details(activityLogDto.getDetails())
                .ipAddress(activityLogDto.getIpAddress())
                .userAgent(activityLogDto.getUserAgent())
                .severity(activityLogDto.getSeverity())
                .createdAt(activityLogDto.getCreatedAt())
                .isRead(activityLogDto.getIsRead())
                .notificationSent(activityLogDto.getNotificationSent())
                .build();
    }

    /**
     * Map ke SimpleActivityLogDto untuk JSON response (tanpa circular reference)
     * Pattern yang sama seperti mapSatkerDtoToSimpleSatkerDto()
     */
    public static SimpleActivityLogDto mapToSimpleActivityLogDto(ActivityLog activityLog) {
        return SimpleActivityLogDto.builder()
                .id(activityLog.getId())
                // Map user info sebagai simple fields, tidak reference entity
                .userId(activityLog.getUser() != null ? activityLog.getUser().getId() : null)
                .userEmail(activityLog.getUserEmail())
                .userName(activityLog.getUserName())
                .activityType(activityLog.getActivityType())
                .entityType(activityLog.getEntityType())
                .entityId(activityLog.getEntityId())
                .entityName(activityLog.getEntityName())
                .description(activityLog.getDescription())
                .details(activityLog.getDetails())
                .ipAddress(activityLog.getIpAddress())
                .userAgent(activityLog.getUserAgent())
                .severity(activityLog.getSeverity())
                .createdAt(activityLog.getCreatedAt())
                .isRead(activityLog.getIsRead())
                .notificationSent(activityLog.getNotificationSent())
                .build();
    }

    /**
     * Map dari ActivityLogDto ke SimpleActivityLogDto
     * Untuk convert dari internal DTO ke response DTO
     */
    public static SimpleActivityLogDto mapActivityLogDtoToSimpleActivityLogDto(ActivityLogDto activityLogDto) {
        return SimpleActivityLogDto.builder()
                .id(activityLogDto.getId())
                // Extract user info dari User entity jika ada
                .userId(activityLogDto.getUser() != null ? activityLogDto.getUser().getId() : null)
                .userEmail(activityLogDto.getUserEmail())
                .userName(activityLogDto.getUserName())
                .activityType(activityLogDto.getActivityType())
                .entityType(activityLogDto.getEntityType())
                .entityId(activityLogDto.getEntityId())
                .entityName(activityLogDto.getEntityName())
                .description(activityLogDto.getDescription())
                .details(activityLogDto.getDetails())
                .ipAddress(activityLogDto.getIpAddress())
                .userAgent(activityLogDto.getUserAgent())
                .severity(activityLogDto.getSeverity())
                .createdAt(activityLogDto.getCreatedAt())
                .isRead(activityLogDto.getIsRead())
                .notificationSent(activityLogDto.getNotificationSent())
                .build();
    }

    // ====================================
    // UTILITY MAPPING METHODS
    // ====================================

    /**
     * Map minimal information untuk performance (hanya field penting)
     */
    public static SimpleActivityLogDto mapToMinimalActivityLogDto(ActivityLog activityLog) {
        return SimpleActivityLogDto.builder()
                .id(activityLog.getId())
                .userId(activityLog.getUser() != null ? activityLog.getUser().getId() : null)
                .userName(activityLog.getUserName())
                .activityType(activityLog.getActivityType())
                .entityType(activityLog.getEntityType())
                .description(activityLog.getDescription())
                .severity(activityLog.getSeverity())
                .createdAt(activityLog.getCreatedAt())
                .isRead(activityLog.getIsRead())
                .build();
    }

    /**
     * Map untuk notification (field yang dibutuhkan untuk notifikasi)
     */
    public static SimpleActivityLogDto mapToNotificationDto(ActivityLog activityLog) {
        return SimpleActivityLogDto.builder()
                .id(activityLog.getId())
                .userId(activityLog.getUser() != null ? activityLog.getUser().getId() : null)
                .userName(activityLog.getUserName())
                .userEmail(activityLog.getUserEmail())
                .activityType(activityLog.getActivityType())
                .entityType(activityLog.getEntityType())
                .entityName(activityLog.getEntityName())
                .description(activityLog.getDescription())
                .severity(activityLog.getSeverity())
                .createdAt(activityLog.getCreatedAt())
                .isRead(activityLog.getIsRead())
                .notificationSent(activityLog.getNotificationSent())
                .build();
    }

    // /**
    // * Bulk mapping untuk List<ActivityLog> ke List<SimpleActivityLogDto>
    // */
    // public static java.util.List<SimpleActivityLogDto>
    // mapToSimpleActivityLogDtoList(
    // java.util.List<ActivityLog> activityLogs) {
    // return activityLogs.stream()
    // .map(ActivityLogMapper::mapToSimpleActivityLogDto)
    // .collect(java.util.stream.Collectors.toList());
    // }

    // /**
    // * Bulk mapping untuk Page<ActivityLog> ke Page<SimpleActivityLogDto>
    // */
    // public static org.springframework.data.domain.Page<SimpleActivityLogDto>
    // mapToSimpleActivityLogDtoPage(
    // org.springframework.data.domain.Page<ActivityLog> activityLogPage) {
    // return activityLogPage.map(ActivityLogMapper::mapToSimpleActivityLogDto);
    // }
}