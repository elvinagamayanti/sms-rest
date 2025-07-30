package com.sms.unit.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.ActivityLogDto;
import com.sms.dto.UserDto;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.ActivityLogService;
import com.sms.service.NotificationService;
import com.sms.service.UserService;

public class NotificationServiceTest {

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private SatkerRepository satkerRepository;

    @Mock
    private DeputiRepository deputiRepository;

    @Mock
    private DirektoratRepository direktoratRepository;

    private NotificationService notificationService;

    AutoCloseable autoCloseable;
    User user;
    UserDto userDto;
    Role role;
    Province province;
    Satker satker;
    Deputi deputi;
    Direktorat direktorat;
    ActivityLogDto activityLogDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService();

        // Use reflection to set the mocked dependencies
        try {
            java.lang.reflect.Field activityLogServiceField = NotificationService.class
                    .getDeclaredField("activityLogService");
            activityLogServiceField.setAccessible(true);
            activityLogServiceField.set(notificationService, activityLogService);

            java.lang.reflect.Field userServiceField = NotificationService.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(notificationService, userService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roleRepository.save(role);

        province = new Province();
        province.setName("Test Province");
        province.setCode("01");
        provinceRepository.save(province);

        satker = new Satker();
        satker.setName("Test Satker");
        satker.setCode("0100");
        satker.setAddress("123 Test Street");
        satker.setNumber("12345");
        satker.setEmail("satker@email.com");
        satker.setIsProvince(true);
        satker.setProvince(province);
        satkerRepository.save(satker);

        deputi = new Deputi();
        deputi.setName("Test Deputi");
        deputi.setCode("D01");
        deputiRepository.save(deputi);

        direktorat = new Direktorat();
        direktorat.setName("Test Direktorat");
        direktorat.setCode("D0101");
        direktorat.setDeputi(deputi);
        direktoratRepository.save(direktorat);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setNip("1234567890");
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setIsActive(true);
        user.setSatker(satker);
        user.setDirektorat(direktorat);
        user.setRoles(List.of(role));
        userRepository.save(user);

        userDto = UserMapper.mapToUserDto(user);

        activityLogDto = ActivityLogDto.builder()
                .id(1L)
                .user(user)
                .userEmail(user.getEmail())
                .userName(user.getName())
                .activityType(ActivityType.CREATE)
                .entityType(EntityType.USER)
                .entityId(1L)
                .entityName("Test Entity")
                .description("Test activity log")
                .details("Test details")
                .severity(LogSeverity.MEDIUM)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationSent(false)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testProcessScheduledNotifications_Success() {
        mock(ActivityLogService.class);

        List<ActivityLogDto> logsNeedingNotification = new ArrayList<>(Collections.singleton(activityLogDto));
        when(activityLogService.getLogsNeedingNotification()).thenReturn(logsNeedingNotification);

        notificationService.processScheduledNotifications();

        verify(activityLogService).getLogsNeedingNotification();
    }

    @Test
    void testProcessScheduledNotifications_EmptyList() {
        mock(ActivityLogService.class);

        when(activityLogService.getLogsNeedingNotification()).thenReturn(new ArrayList<>());

        notificationService.processScheduledNotifications();

        verify(activityLogService).getLogsNeedingNotification();
    }

    @Test
    void testProcessScheduledNotifications_ExceptionHandling() {
        mock(ActivityLogService.class);

        when(activityLogService.getLogsNeedingNotification()).thenThrow(new RuntimeException("Database error"));

        // Should not throw exception, just log error
        notificationService.processScheduledNotifications();

        verify(activityLogService).getLogsNeedingNotification();
    }

    @Test
    void testProcessNotification_CriticalSeverity() {
        mock(ActivityLogService.class);

        activityLogDto.setSeverity(LogSeverity.CRITICAL);

        notificationService.processNotification(activityLogDto);

        verify(activityLogService).markNotificationSent(activityLogDto.getId());
    }

    @Test
    void testProcessNotification_HighSeverity() {
        mock(ActivityLogService.class);

        activityLogDto.setSeverity(LogSeverity.HIGH);

        notificationService.processNotification(activityLogDto);

        verify(activityLogService).markNotificationSent(activityLogDto.getId());
    }

    @Test
    void testProcessNotification_MediumSeverity() {
        mock(ActivityLogService.class);

        activityLogDto.setSeverity(LogSeverity.MEDIUM);

        notificationService.processNotification(activityLogDto);

        verify(activityLogService).markNotificationSent(activityLogDto.getId());
    }

    @Test
    void testProcessNotification_LowSeverity() {
        mock(ActivityLogService.class);

        activityLogDto.setSeverity(LogSeverity.LOW);

        notificationService.processNotification(activityLogDto);

        verify(activityLogService).markNotificationSent(activityLogDto.getId());
    }

    @Test
    void testSendNotificationToUser_Success() {
        mock(UserService.class);

        when(userService.findUserById(1L)).thenReturn(user);

        notificationService.sendNotificationToUser(1L, "Test Title", "Test Message", LogSeverity.MEDIUM);

        verify(userService).findUserById(1L);
    }

    @Test
    void testSendNotificationToUser_CriticalSeverity() {
        mock(UserService.class);

        when(userService.findUserById(1L)).thenReturn(user);

        notificationService.sendNotificationToUser(1L, "Critical Alert", "Critical Message", LogSeverity.CRITICAL);

        verify(userService).findUserById(1L);
    }

    @Test
    void testSendNotificationToUser_ExceptionHandling() {
        mock(UserService.class);

        when(userService.findUserById(1L)).thenThrow(new RuntimeException("User not found"));

        // Should not throw exception, just log error
        notificationService.sendNotificationToUser(1L, "Test Title", "Test Message", LogSeverity.MEDIUM);

        verify(userService).findUserById(1L);
    }

    @Test
    void testSendBroadcastNotification_Success() {
        mock(UserService.class);

        List<UserDto> userDtos = new ArrayList<>(Collections.singleton(userDto));
        when(userService.findAllUsers()).thenReturn(userDtos);
        when(userService.findUserById(1L)).thenReturn(user);

        notificationService.sendBroadcastNotification("Broadcast Title", "Broadcast Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
        verify(userService).findUserById(1L);
    }

    @Test
    void testSendBroadcastNotification_EmptyUserList() {
        mock(UserService.class);

        when(userService.findAllUsers()).thenReturn(new ArrayList<>());

        notificationService.sendBroadcastNotification("Broadcast Title", "Broadcast Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
    }

    @Test
    void testSendBroadcastNotification_ExceptionHandling() {
        mock(UserService.class);

        when(userService.findAllUsers()).thenThrow(new RuntimeException("Database error"));

        // Should not throw exception, just log error
        notificationService.sendBroadcastNotification("Broadcast Title", "Broadcast Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
    }

    @Test
    void testSendNotificationToRole_Success() {
        mock(UserService.class);

        List<UserDto> userDtos = new ArrayList<>(Collections.singleton(userDto));
        when(userService.findAllUsers()).thenReturn(userDtos);
        when(userService.findUserById(1L)).thenReturn(user);
        when(userService.hasRole(user, "ROLE_ADMIN")).thenReturn(true);

        notificationService.sendNotificationToRole("ROLE_ADMIN", "Role Title", "Role Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
        verify(userService).findUserById(1L);
        verify(userService).hasRole(user, "ROLE_ADMIN");
    }

    @Test
    void testSendNotificationToRole_NoUsersWithRole() {
        mock(UserService.class);

        List<UserDto> userDtos = new ArrayList<>(Collections.singleton(userDto));
        when(userService.findAllUsers()).thenReturn(userDtos);
        when(userService.findUserById(1L)).thenReturn(user);
        when(userService.hasRole(user, "ROLE_ADMIN")).thenReturn(false);

        notificationService.sendNotificationToRole("ROLE_ADMIN", "Role Title", "Role Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
        verify(userService).findUserById(1L);
        verify(userService).hasRole(user, "ROLE_ADMIN");
    }

    @Test
    void testSendNotificationToRole_ExceptionHandling() {
        mock(UserService.class);

        when(userService.findAllUsers()).thenThrow(new RuntimeException("Database error"));

        // Should not throw exception, just log error
        notificationService.sendNotificationToRole("ROLE_ADMIN", "Role Title", "Role Message", LogSeverity.MEDIUM);

        verify(userService).findAllUsers();
    }

    @Test
    void testSendEmergencyAlert_Success() {
        mock(ActivityLogService.class);
        mock(UserService.class);

        List<UserDto> userDtos = new ArrayList<>(Collections.singleton(userDto));
        when(userService.findAllUsers()).thenReturn(userDtos);
        when(userService.findUserById(1L)).thenReturn(user);
        when(userService.hasRole(user, "ROLE_ADMIN")).thenReturn(true);

        notificationService.sendEmergencyAlert("Emergency Message", "Emergency Details");

        verify(activityLogService).logSystemEvent(Mockito.contains("EMERGENCY"), Mockito.eq(LogSeverity.CRITICAL));
        verify(userService).findAllUsers();
    }

    @Test
    void testSendEmergencyAlert_NoAdminUsers() {
        mock(ActivityLogService.class);
        mock(UserService.class);

        List<UserDto> userDtos = new ArrayList<>(Collections.singleton(userDto));
        when(userService.findAllUsers()).thenReturn(userDtos);
        when(userService.findUserById(1L)).thenReturn(user);
        when(userService.hasRole(user, "ROLE_ADMIN")).thenReturn(false);
        when(userService.hasRole(user, "ROLE_SUPERADMIN")).thenReturn(false);

        notificationService.sendEmergencyAlert("Emergency Message", "Emergency Details");

        verify(activityLogService).logSystemEvent(Mockito.contains("EMERGENCY"), Mockito.eq(LogSeverity.CRITICAL));
        verify(userService).findAllUsers();
    }

    @Test
    void testShouldSendNotification_CriticalSeverity() {
        boolean result = notificationService.shouldSendNotification(user, LogSeverity.CRITICAL);
        assertThat(result).isTrue();
    }

    @Test
    void testShouldSendNotification_HighSeverity() {
        boolean result = notificationService.shouldSendNotification(user, LogSeverity.HIGH);
        assertThat(result).isTrue();
    }

    @Test
    void testShouldSendNotification_MediumSeverity() {
        boolean result = notificationService.shouldSendNotification(user, LogSeverity.MEDIUM);
        assertThat(result).isTrue();
    }

    @Test
    void testShouldSendNotification_LowSeverity() {
        boolean result = notificationService.shouldSendNotification(user, LogSeverity.LOW);
        assertThat(result).isFalse();
    }

    @Test
    void testGetNotificationsSentToday_Success() {
        mock(ActivityLogService.class);

        when(activityLogService.getRecentActivityCount(24)).thenReturn(10L);

        long result = notificationService.getNotificationsSentToday();

        assertThat(result).isEqualTo(10L);
        verify(activityLogService).getRecentActivityCount(24);
    }

    @Test
    void testGetNotificationsSentToday_ExceptionHandling() {
        mock(ActivityLogService.class);

        when(activityLogService.getRecentActivityCount(24)).thenThrow(new RuntimeException("Database error"));

        // Should return 0 on exception
        long result = notificationService.getNotificationsSentToday();

        assertThat(result).isEqualTo(0L);
        verify(activityLogService).getRecentActivityCount(24);
    }

    @Test
    void testGetUnprocessedNotifications_Success() {
        mock(ActivityLogService.class);

        List<ActivityLogDto> unprocessedLogs = new ArrayList<>(Collections.singleton(activityLogDto));
        when(activityLogService.getLogsNeedingNotification()).thenReturn(unprocessedLogs);

        long result = notificationService.getUnprocessedNotifications();

        assertThat(result).isEqualTo(1L);
        verify(activityLogService).getLogsNeedingNotification();
    }

    @Test
    void testGetUnprocessedNotifications_EmptyList() {
        mock(ActivityLogService.class);

        when(activityLogService.getLogsNeedingNotification()).thenReturn(new ArrayList<>());

        long result = notificationService.getUnprocessedNotifications();

        assertThat(result).isEqualTo(0L);
        verify(activityLogService).getLogsNeedingNotification();
    }

    @Test
    void testGetUnprocessedNotifications_ExceptionHandling() {
        mock(ActivityLogService.class);

        when(activityLogService.getLogsNeedingNotification()).thenThrow(new RuntimeException("Database error"));

        // Should return 0 on exception
        long result = notificationService.getUnprocessedNotifications();

        assertThat(result).isEqualTo(0L);
        verify(activityLogService).getLogsNeedingNotification();
    }
}