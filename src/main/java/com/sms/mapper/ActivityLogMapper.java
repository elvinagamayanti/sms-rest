package com.sms.mapper;

import com.sms.dto.ActivityLogDto;
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
}