package com.sms.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sms.annotation.LogActivity;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.User;
import com.sms.service.ActivityLogService;
import com.sms.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Aspect untuk logging aktivitas secara otomatis menggunakan AOP
 * 
 * @author pinaa
 */
@Aspect
@Component
public class ActivityLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogAspect.class);

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private UserService userService;

    @AfterReturning(pointcut = "@annotation(logActivity)", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            User currentUser = getCurrentUser();
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            String description = !logActivity.description().isEmpty() ? logActivity.description()
                    : String.format("%s executed in %s", methodName, className);

            ActivityType activityType = logActivity.activityType() != ActivityType.VIEW ? logActivity.activityType()
                    : determineActivityType(methodName);

            EntityType entityType = logActivity.entityType() != EntityType.SYSTEM ? logActivity.entityType()
                    : determineEntityType(className);

            LogSeverity severity = logActivity.severity();

            // Extract entity information if available
            Long entityId = extractEntityId(joinPoint, result);
            String entityName = extractEntityName(joinPoint, result);

            // Get request details
            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();

            activityLogService.logActivity(currentUser, description, activityType, entityType,
                    entityId, entityName, null, severity, ipAddress, userAgent);

            logger.debug("Activity logged: {} - {} by {}", activityType, description,
                    currentUser != null ? currentUser.getEmail() : "system");

        } catch (Exception e) {
            logger.error("Error logging activity: {}", e.getMessage(), e);
        }
    }

    @AfterThrowing(pointcut = "@annotation(logActivity)", throwing = "exception")
    public void logAfterException(JoinPoint joinPoint, LogActivity logActivity, Exception exception) {
        try {
            User currentUser = getCurrentUser();
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            String description = String.format("Error in %s.%s: %s", className, methodName, exception.getMessage());

            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();

            activityLogService.logActivity(currentUser, description, ActivityType.VIEW,
                    EntityType.SYSTEM, null, "Error", exception.getStackTrace().toString(),
                    LogSeverity.HIGH, ipAddress, userAgent);

            logger.warn("Error activity logged: {} by {}", description,
                    currentUser != null ? currentUser.getEmail() : "system");

        } catch (Exception e) {
            logger.error("Error logging exception activity: {}", e.getMessage(), e);
        }
    }

    // Pointcuts for specific controller methods
    @AfterReturning("execution(* com.sms.controller.*Controller.create*(..)) && args(..)")
    public void logCreateOperations(JoinPoint joinPoint) {
        logControllerOperation(joinPoint, ActivityType.CREATE, LogSeverity.LOW);
    }

    @AfterReturning("execution(* com.sms.controller.*Controller.update*(..)) && args(..)")
    public void logUpdateOperations(JoinPoint joinPoint) {
        logControllerOperation(joinPoint, ActivityType.UPDATE, LogSeverity.LOW);
    }

    @AfterReturning("execution(* com.sms.controller.*Controller.delete*(..)) && args(..)")
    public void logDeleteOperations(JoinPoint joinPoint) {
        logControllerOperation(joinPoint, ActivityType.DELETE, LogSeverity.MEDIUM);
    }

    @AfterReturning("execution(* com.sms.controller.*Controller.get*(..)) && args(..)")
    public void logViewOperations(JoinPoint joinPoint) {
        // Only log sensitive view operations
        String methodName = joinPoint.getSignature().getName();
        if (methodName.contains("Detail") || methodName.contains("ById")) {
            logControllerOperation(joinPoint, ActivityType.VIEW, LogSeverity.LOW);
        }
    }

    // Login/Logout specific logging
    @AfterReturning("execution(* com.sms.controller.AuthController.login(..)) && args(..)")
    public void logLoginAttempt(JoinPoint joinPoint) {
        try {
            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();

            // Try to get user from result or authentication
            User user = getCurrentUser();
            if (user != null) {
                activityLogService.logUserLogin(user, ipAddress, userAgent);
            }
        } catch (Exception e) {
            logger.error("Error logging login activity: {}", e.getMessage());
        }
    }

    @AfterReturning("execution(* com.sms.controller.AuthController.logout(..)) && args(..)")
    public void logLogoutAttempt(JoinPoint joinPoint) {
        try {
            String ipAddress = getClientIpAddress();
            User user = getCurrentUser();
            if (user != null) {
                activityLogService.logUserLogout(user, ipAddress);
            }
        } catch (Exception e) {
            logger.error("Error logging logout activity: {}", e.getMessage());
        }
    }

    // File upload/download logging
    @AfterReturning("execution(* com.sms.controller.*Controller.*upload*(..)) && args(..)")
    public void logFileUpload(JoinPoint joinPoint) {
        logControllerOperation(joinPoint, ActivityType.UPLOAD, LogSeverity.LOW);
    }

    @AfterReturning("execution(* com.sms.controller.*Controller.*download*(..)) && args(..)")
    public void logFileDownload(JoinPoint joinPoint) {
        logControllerOperation(joinPoint, ActivityType.DOWNLOAD, LogSeverity.LOW);
    }

    // Private helper methods
    private void logControllerOperation(JoinPoint joinPoint, ActivityType activityType, LogSeverity severity) {
        try {
            User currentUser = getCurrentUser();
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            EntityType entityType = determineEntityType(className);
            String description = String.format("%s operation in %s", activityType.toString().toLowerCase(),
                    entityType.toString().toLowerCase());

            Long entityId = extractEntityId(joinPoint, null);
            String entityName = extractEntityName(joinPoint, null);

            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();

            activityLogService.logActivity(currentUser, description, activityType, entityType,
                    entityId, entityName, null, severity, ipAddress, userAgent);

        } catch (Exception e) {
            logger.error("Error logging controller operation: {}", e.getMessage());
        }
    }

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

    private ActivityType determineActivityType(String methodName) {
        String lowerMethodName = methodName.toLowerCase();

        if (lowerMethodName.contains("create") || lowerMethodName.contains("save") || lowerMethodName.contains("add")) {
            return ActivityType.CREATE;
        } else if (lowerMethodName.contains("update") || lowerMethodName.contains("edit")
                || lowerMethodName.contains("patch")) {
            return ActivityType.UPDATE;
        } else if (lowerMethodName.contains("delete") || lowerMethodName.contains("remove")) {
            return ActivityType.DELETE;
        } else if (lowerMethodName.contains("login")) {
            return ActivityType.LOGIN;
        } else if (lowerMethodName.contains("logout")) {
            return ActivityType.LOGOUT;
        } else if (lowerMethodName.contains("upload")) {
            return ActivityType.UPLOAD;
        } else if (lowerMethodName.contains("download")) {
            return ActivityType.DOWNLOAD;
        } else if (lowerMethodName.contains("assign")) {
            return ActivityType.ASSIGN;
        } else if (lowerMethodName.contains("complete")) {
            return ActivityType.COMPLETE;
        } else if (lowerMethodName.contains("cancel")) {
            return ActivityType.CANCEL;
        } else if (lowerMethodName.contains("approve")) {
            return ActivityType.APPROVE;
        } else if (lowerMethodName.contains("reject")) {
            return ActivityType.REJECT;
        } else if (lowerMethodName.contains("submit")) {
            return ActivityType.SUBMIT;
        } else if (lowerMethodName.contains("sync")) {
            return ActivityType.SYNC;
        } else if (lowerMethodName.contains("restore")) {
            return ActivityType.RESTORE;
        }

        return ActivityType.VIEW;
    }

    private EntityType determineEntityType(String className) {
        String lowerClassName = className.toLowerCase();

        if (lowerClassName.contains("user")) {
            return EntityType.USER;
        } else if (lowerClassName.contains("role")) {
            return EntityType.ROLE;
        } else if (lowerClassName.contains("satker")) {
            return EntityType.SATKER;
        } else if (lowerClassName.contains("province")) {
            return EntityType.PROVINCE;
        } else if (lowerClassName.contains("program")) {
            return EntityType.PROGRAM;
        } else if (lowerClassName.contains("output")) {
            return EntityType.OUTPUT;
        } else if (lowerClassName.contains("kegiatan")) {
            return EntityType.KEGIATAN;
        } else if (lowerClassName.contains("deputi")) {
            return EntityType.DEPUTI;
        } else if (lowerClassName.contains("direktorat")) {
            return EntityType.DIREKTORAT;
        } else if (lowerClassName.contains("tahap")) {
            return EntityType.TAHAP;
        } else if (lowerClassName.contains("file")) {
            return EntityType.FILE;
        }

        return EntityType.SYSTEM;
    }

    private Long extractEntityId(JoinPoint joinPoint, Object result) {
        try {
            // Try to extract ID from method arguments
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                // Check if first argument is Long (ID)
                if (args[0] instanceof Long) {
                    return (Long) args[0];
                }
                // Check if first argument has ID field
                if (args[0] != null) {
                    try {
                        Method getIdMethod = args[0].getClass().getMethod("getId");
                        Object id = getIdMethod.invoke(args[0]);
                        if (id instanceof Long) {
                            return (Long) id;
                        }
                    } catch (Exception e) {
                        // Ignore if no getId method
                    }
                }
            }

            // Try to extract ID from method signature (path variable)
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getName();
            if (methodName.contains("ById")) {
                // Look for Long parameter in method signature
                Class<?>[] paramTypes = signature.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    if (paramTypes[i] == Long.class && args[i] instanceof Long) {
                        return (Long) args[i];
                    }
                }
            }

        } catch (Exception e) {
            logger.debug("Could not extract entity ID: {}", e.getMessage());
        }
        return null;
    }

    private String extractEntityName(JoinPoint joinPoint, Object result) {
        try {
            // Try to extract name from method arguments
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg != null) {
                    try {
                        Method getNameMethod = arg.getClass().getMethod("getName");
                        Object name = getNameMethod.invoke(arg);
                        if (name instanceof String) {
                            return (String) name;
                        }
                    } catch (Exception e) {
                        // Try other common name fields
                        try {
                            Method getTitleMethod = arg.getClass().getMethod("getTitle");
                            Object title = getTitleMethod.invoke(arg);
                            if (title instanceof String) {
                                return (String) title;
                            }
                        } catch (Exception ex) {
                            // Ignore if no name/title method
                        }
                    }
                }
            }

            // Try to extract name from result
            if (result != null) {
                try {
                    Method getNameMethod = result.getClass().getMethod("getName");
                    Object name = getNameMethod.invoke(result);
                    if (name instanceof String) {
                        return (String) name;
                    }
                } catch (Exception e) {
                    // Ignore if no getName method
                }
            }

        } catch (Exception e) {
            logger.debug("Could not extract entity name: {}", e.getMessage());
        }
        return null;
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.debug("Could not get client IP address: {}", e.getMessage());
        }
        return null;
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            logger.debug("Could not get user agent: {}", e.getMessage());
        }
        return null;
    }
}