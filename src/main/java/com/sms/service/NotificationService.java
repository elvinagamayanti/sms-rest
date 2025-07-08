package com.sms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sms.dto.ActivityLogDto;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;

/**
 * Service untuk menangani notifikasi berdasarkan activity log
 * 
 * @author pinaa
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private UserService userService;

    // Process notifications every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void processScheduledNotifications() {
        logger.info("Processing scheduled notifications...");

        try {
            List<ActivityLogDto> logsNeedingNotification = activityLogService.getLogsNeedingNotification();

            if (!logsNeedingNotification.isEmpty()) {
                logger.info("Found {} logs needing notification", logsNeedingNotification.size());

                for (ActivityLogDto log : logsNeedingNotification) {
                    processNotificationAsync(log);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing scheduled notifications: {}", e.getMessage(), e);
        }
    }

    @Async
    public CompletableFuture<Void> processNotificationAsync(ActivityLogDto log) {
        try {
            processNotification(log);
        } catch (Exception e) {
            logger.error("Error processing notification for log {}: {}", log.getId(), e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public void processNotification(ActivityLogDto log) {
        logger.debug("Processing notification for log: {}", log.getId());

        try {
            switch (log.getSeverity()) {
                case CRITICAL:
                    sendCriticalNotification(log);
                    break;
                case HIGH:
                    sendHighPriorityNotification(log);
                    break;
                case MEDIUM:
                    sendMediumPriorityNotification(log);
                    break;
                case LOW:
                    sendLowPriorityNotification(log);
                    break;
            }

            // Mark notification as sent
            activityLogService.markNotificationSent(log.getId());

        } catch (Exception e) {
            logger.error("Error sending notification for log {}: {}", log.getId(), e.getMessage(), e);
        }
    }

    private void sendCriticalNotification(ActivityLogDto log) {
        logger.info("Sending CRITICAL notification: {}", log.getDescription());

        // Send immediate notifications to all admins
        List<User> admins = getAdminUsers();
        for (User admin : admins) {
            sendEmailNotification(admin, log, "CRITICAL ALERT");
            sendPushNotification(admin, log);
            // Could also send SMS for critical alerts
        }

        // Log to system monitoring
        logToSystemMonitoring(log);
    }

    private void sendHighPriorityNotification(ActivityLogDto log) {
        logger.info("Sending HIGH priority notification: {}", log.getDescription());

        // Send notifications to relevant users and admins
        List<User> relevantUsers = getRelevantUsers(log);
        for (User user : relevantUsers) {
            sendEmailNotification(user, log, "High Priority Alert");
            sendPushNotification(user, log);
        }
    }

    private void sendMediumPriorityNotification(ActivityLogDto log) {
        logger.debug("Sending MEDIUM priority notification: {}", log.getDescription());

        // Send notification to relevant users only
        List<User> relevantUsers = getRelevantUsers(log);
        for (User user : relevantUsers) {
            sendInAppNotification(user, log);
        }
    }

    private void sendLowPriorityNotification(ActivityLogDto log) {
        logger.debug("Sending LOW priority notification: {}", log.getDescription());

        // Only send in-app notifications for low priority
        if (log.getUser() != null) {
            sendInAppNotification(log.getUser(), log);
        }
    }

    @Async
    public CompletableFuture<Void> sendEmailNotification(User user, ActivityLogDto log, String priority) {
        try {
            logger.info("Sending email notification to {}: {}", user.getEmail(), log.getDescription());

            String subject = String.format("[%s] Activity Alert - %s", priority, log.getEntityType());
            String body = buildEmailBody(log, priority);

            // Here you would integrate with your email service
            // Example: emailService.sendEmail(user.getEmail(), subject, body);

            logger.debug("Email notification sent successfully to {}", user.getEmail());

        } catch (Exception e) {
            logger.error("Error sending email notification to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendPushNotification(User user, ActivityLogDto log) {
        try {
            logger.info("Sending push notification to {}: {}", user.getEmail(), log.getDescription());

            String title = String.format("%s Alert", log.getSeverity());
            String message = log.getDescription();

            // Here you would integrate with your push notification service
            // Example: pushNotificationService.sendNotification(user.getId(), title,
            // message);

            logger.debug("Push notification sent successfully to {}", user.getEmail());

        } catch (Exception e) {
            logger.error("Error sending push notification to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendInAppNotification(User user, ActivityLogDto log) {
        try {
            logger.debug("Sending in-app notification to {}: {}", user.getEmail(), log.getDescription());

            // Here you would create an in-app notification record
            // This could be stored in database and shown in the UI

            createInAppNotificationRecord(user, log);

            // Send via WebSocket if user is online
            sendWebSocketNotification(user, log);

            logger.debug("In-app notification sent successfully to {}", user.getEmail());

        } catch (Exception e) {
            logger.error("Error sending in-app notification to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void createInAppNotificationRecord(User user, ActivityLogDto log) {
        // Implementation to create in-app notification record
        // This would typically involve saving to database
        logger.debug("Creating in-app notification record for user {} and log {}", user.getId(), log.getId());
    }

    private void sendWebSocketNotification(User user, ActivityLogDto log) {
        try {
            // Here you would send WebSocket message to user if they're online
            // Example: webSocketService.sendToUser(user.getId(), notificationMessage);

            logger.debug("WebSocket notification sent to user {}", user.getId());

        } catch (Exception e) {
            logger.debug("User {} not connected via WebSocket", user.getId());
        }
    }

    private void logToSystemMonitoring(ActivityLogDto log) {
        logger.info("Logging to system monitoring: CRITICAL activity - {}", log.getDescription());

        // Here you would integrate with system monitoring tools
        // Example: monitoringService.logCriticalEvent(log);
    }

    private String buildEmailBody(ActivityLogDto log, String priority) {
        StringBuilder body = new StringBuilder();
        body.append(String.format("Priority: %s\n", priority));
        body.append(String.format("Activity: %s\n", log.getActivityType()));
        body.append(String.format("Entity: %s\n", log.getEntityType()));
        body.append(String.format("Description: %s\n", log.getDescription()));
        body.append(String.format("Time: %s\n", log.getCreatedAt()));

        if (log.getUserName() != null) {
            body.append(String.format("User: %s (%s)\n", log.getUserName(), log.getUserEmail()));
        }

        if (log.getIpAddress() != null) {
            body.append(String.format("IP Address: %s\n", log.getIpAddress()));
        }

        if (log.getDetails() != null && !log.getDetails().isEmpty()) {
            body.append(String.format("Details: %s\n", log.getDetails()));
        }

        body.append("\n---\n");
        body.append("This is an automated notification from the Survey Management System.");

        return body.toString();
    }

    private List<User> getAdminUsers() {
        // Get all users with admin roles
        try {
            return userService.findAllUsers().stream()
                    .map(userDto -> userService.findUserById(userDto.getId()))
                    .filter(user -> userService.hasRole(user, "ROLE_ADMIN")
                            || userService.hasRole(user, "ROLE_SUPERADMIN"))
                    .toList();
        } catch (Exception e) {
            logger.error("Error getting admin users: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private List<User> getRelevantUsers(ActivityLogDto log) {
        // Get users relevant to the activity log
        try {
            List<User> relevantUsers = List.of();

            // If log has a specific user, include them
            if (log.getUser() != null) {
                relevantUsers = List.of(log.getUser());
            }

            // For high severity, also include admins
            if (log.getSeverity() == LogSeverity.HIGH || log.getSeverity() == LogSeverity.CRITICAL) {
                List<User> adminUsers = getAdminUsers();
                relevantUsers = new java.util.ArrayList<>(relevantUsers);
                relevantUsers.addAll(adminUsers);
            }

            // Remove duplicates
            return relevantUsers.stream().distinct().toList();

        } catch (Exception e) {
            logger.error("Error getting relevant users: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // Public methods for manual notification sending
    public void sendNotificationToUser(Long userId, String title, String message, LogSeverity severity) {
        try {
            User user = userService.findUserById(userId);

            // Create a temporary activity log for notification
            ActivityLogDto tempLog = ActivityLogDto.builder()
                    .description(message)
                    .severity(severity)
                    .createdAt(LocalDateTime.now())
                    .build();

            switch (severity) {
                case CRITICAL:
                case HIGH:
                    sendEmailNotification(user, tempLog, severity.toString());
                    sendPushNotification(user, tempLog);
                    break;
                case MEDIUM:
                    sendInAppNotification(user, tempLog);
                    break;
                case LOW:
                    sendInAppNotification(user, tempLog);
                    break;
            }

        } catch (Exception e) {
            logger.error("Error sending manual notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    public void sendBroadcastNotification(String title, String message, LogSeverity severity) {
        try {
            List<User> allUsers = userService.findAllUsers().stream()
                    .map(userDto -> userService.findUserById(userDto.getId()))
                    .toList();

            for (User user : allUsers) {
                sendNotificationToUser(user.getId(), title, message, severity);
            }

            logger.info("Broadcast notification sent to {} users", allUsers.size());

        } catch (Exception e) {
            logger.error("Error sending broadcast notification: {}", e.getMessage(), e);
        }
    }

    public void sendNotificationToRole(String roleName, String title, String message, LogSeverity severity) {
        try {
            List<User> roleUsers = userService.findAllUsers().stream()
                    .map(userDto -> userService.findUserById(userDto.getId()))
                    .filter(user -> userService.hasRole(user, roleName))
                    .toList();

            for (User user : roleUsers) {
                sendNotificationToUser(user.getId(), title, message, severity);
            }

            logger.info("Role-based notification sent to {} users with role {}", roleUsers.size(), roleName);

        } catch (Exception e) {
            logger.error("Error sending role-based notification: {}", e.getMessage(), e);
        }
    }

    // Notification preferences (could be extended)
    public boolean shouldSendNotification(User user, LogSeverity severity) {
        // Here you could implement user preferences for notifications
        // For now, send all notifications

        // Example logic:
        // - Always send CRITICAL notifications
        // - Send HIGH/MEDIUM based on user preferences
        // - Send LOW only if user opted in

        return switch (severity) {
            case CRITICAL -> true;
            case HIGH -> true; // Could check user preferences
            case MEDIUM -> true; // Could check user preferences
            case LOW -> false; // Could check user preferences
        };
    }

    // Notification statistics
    public long getNotificationsSentToday() {
        // This would typically query a notifications table
        // For now, return count from activity logs processed today
        try {
            return activityLogService.getRecentActivityCount(24);
        } catch (Exception e) {
            logger.error("Error getting notifications sent today: {}", e.getMessage(), e);
            return 0;
        }
    }

    public long getUnprocessedNotifications() {
        try {
            return activityLogService.getLogsNeedingNotification().size();
        } catch (Exception e) {
            logger.error("Error getting unprocessed notifications: {}", e.getMessage(), e);
            return 0;
        }
    }

    // Emergency notification for critical system events
    public void sendEmergencyAlert(String message, String details) {
        logger.error("EMERGENCY ALERT: {}", message);

        // Create critical activity log
        activityLogService.logSystemEvent("EMERGENCY: " + message, LogSeverity.CRITICAL);

        // Send immediate notifications to all admins
        List<User> admins = getAdminUsers();
        for (User admin : admins) {
            try {
                ActivityLogDto emergencyLog = ActivityLogDto.builder()
                        .description(message)
                        .details(details)
                        .severity(LogSeverity.CRITICAL)
                        .createdAt(LocalDateTime.now())
                        .build();

                sendEmailNotification(admin, emergencyLog, "EMERGENCY ALERT");
                sendPushNotification(admin, emergencyLog);

            } catch (Exception e) {
                logger.error("Error sending emergency alert to admin {}: {}", admin.getEmail(), e.getMessage());
            }
        }
    }
}