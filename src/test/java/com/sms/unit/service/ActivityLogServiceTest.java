package com.sms.unit.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.sms.dto.ActivityLogDto;
import com.sms.entity.ActivityLog;
import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.Province;
import com.sms.entity.Role;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.ActivityLogMapper;
import com.sms.repository.ActivityLogRepository;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.ActivityLogService;
import com.sms.service.UserService;
import com.sms.service.impl.ActivityLogServiceImpl;

public class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

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

    @Mock
    private UserService userService;

    private ActivityLogService activityLogService;

    AutoCloseable autoCloseable;
    ActivityLog activityLog;
    ActivityLogDto activityLogDto;
    User user;
    Role role;
    Province province;
    Satker satker;
    Deputi deputi;
    Direktorat direktorat;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        activityLogService = new ActivityLogServiceImpl(activityLogRepository, userService);

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

        activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setUser(user);
        activityLog.setUserEmail(user.getEmail());
        activityLog.setUserName(user.getName());
        activityLog.setActivityType(ActivityType.CREATE);
        activityLog.setEntityType(EntityType.USER);
        activityLog.setEntityId(1L);
        activityLog.setEntityName("Test Entity");
        activityLog.setDescription("Test activity log");
        activityLog.setDetails("Test details");
        activityLog.setSeverity(LogSeverity.LOW);
        activityLog.setIpAddress("192.168.1.1");
        activityLog.setUserAgent("Mozilla/5.0");
        activityLog.setCreatedAt(LocalDateTime.now());
        activityLog.setIsRead(false);
        activityLog.setNotificationSent(false);

        activityLogDto = ActivityLogMapper.mapToActivityLogDto(activityLog);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testLogActivity_WithBasicParameters() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);
        mock(UserService.class);

        when(userService.findUserByEmail(Mockito.any())).thenReturn(user);
        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        ActivityLogDto result = activityLogService.logActivity("Test description",
                ActivityType.CREATE, EntityType.USER, LogSeverity.LOW);

        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogActivity_WithUser() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        ActivityLogDto result = activityLogService.logActivity(user, "Test description",
                ActivityType.CREATE, EntityType.USER, LogSeverity.LOW);

        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogActivity_WithAllParameters() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        ActivityLogDto result = activityLogService.logActivity(user, "Test description",
                ActivityType.CREATE, EntityType.USER, 1L, "Test Entity",
                "Test details", LogSeverity.LOW, "192.168.1.1", "Mozilla/5.0");

        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testGetAllActivityLogs() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAll())
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getAllActivityLogs();
        assertThat(result.get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetAllActivityLogs_WithPagination() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> page = new PageImpl<>(Collections.singletonList(activityLog));
        when(activityLogRepository.findAll(pageable)).thenReturn(page);

        Page<ActivityLogDto> result = activityLogService.getAllActivityLogs(pageable);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetActivityLogById() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));

        ActivityLogDto result = activityLogService.getActivityLogById(1L);
        assertThat(result.getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetActivityLogsByUserId() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getActivityLogsByUserId(1L);
        assertThat(result.get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetActivityLogsByUserId_WithPagination() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> page = new PageImpl<>(Collections.singletonList(activityLog));
        when(activityLogRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).thenReturn(page);

        Page<ActivityLogDto> result = activityLogService.getActivityLogsByUserId(1L, pageable);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetUnreadLogs() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAllUnreadLogs())
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getUnreadLogs();
        assertThat(result.get(0).getIsRead()).isEqualTo(activityLog.getIsRead());
    }

    @Test
    void testGetUnreadLogs_WithPagination() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> page = new PageImpl<>(Collections.singletonList(activityLog));
        when(activityLogRepository.findUnreadLogs(pageable)).thenReturn(page);

        Page<ActivityLogDto> result = activityLogService.getUnreadLogs(pageable);
        assertThat(result.getContent().get(0).getIsRead()).isEqualTo(activityLog.getIsRead());
    }

    @Test
    void testGetActivitiesByType() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAllByActivityTypeOrderByCreatedAtDesc(ActivityType.CREATE))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getActivitiesByType(ActivityType.CREATE);
        assertThat(result.get(0).getActivityType()).isEqualTo(activityLog.getActivityType());
    }

    @Test
    void testGetActivitiesByEntityType() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAllByEntityTypeOrderByCreatedAtDesc(EntityType.USER))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getActivitiesByEntityType(EntityType.USER);
        assertThat(result.get(0).getEntityType()).isEqualTo(activityLog.getEntityType());
    }

    @Test
    void testGetActivitiesBySeverity() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findAllBySeverityOrderByCreatedAtDesc(LogSeverity.LOW))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getActivitiesBySeverity(LogSeverity.LOW);
        assertThat(result.get(0).getSeverity()).isEqualTo(activityLog.getSeverity());
    }

    @Test
    void testGetActivitiesByEntity() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType.USER, 1L))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getActivitiesByEntity(EntityType.USER, 1L);
        assertThat(result.get(0).getEntityId()).isEqualTo(activityLog.getEntityId());
    }

    @Test
    void testGetRecentActivities() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        when(activityLogRepository.findAllRecentActivities(Mockito.any(LocalDateTime.class)))
                .thenReturn(new ArrayList<ActivityLog>(Collections.singleton(activityLog)));

        List<ActivityLogDto> result = activityLogService.getRecentActivities(24);
        assertThat(result.get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testGetRecentActivities_WithPagination() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> page = new PageImpl<>(Collections.singletonList(activityLog));
        when(activityLogRepository.findRecentActivities(Mockito.any(LocalDateTime.class), Mockito.eq(pageable)))
                .thenReturn(page);

        Page<ActivityLogDto> result = activityLogService.getRecentActivities(24, pageable);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    void testMarkAsRead() {
        mock(ActivityLogRepository.class);

        doNothing().when(activityLogRepository).markAsRead(1L);

        activityLogService.markAsRead(1L);
        verify(activityLogRepository).markAsRead(1L);
    }

    @Test
    void testMarkAllAsReadByUserId() {
        mock(ActivityLogRepository.class);

        when(activityLogRepository.markAllAsReadByUserId(1L)).thenReturn(5);

        activityLogService.markAllAsReadByUserId(1L);
        verify(activityLogRepository).markAllAsReadByUserId(1L);
    }

    @Test
    void testDeleteActivityLog() {
        mock(ActivityLogRepository.class);

        doNothing().when(activityLogRepository).deleteById(1L);

        activityLogService.deleteActivityLog(1L);
        verify(activityLogRepository).deleteById(1L);
    }

    @Test
    void testLogUserLogin() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logUserLogin(user, "192.168.1.1", "Mozilla/5.0");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogUserLogout() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logUserLogout(user, "192.168.1.1");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogEntityCreated() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logEntityCreated(user, EntityType.USER, 1L, "Test Entity");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogEntityUpdated() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logEntityUpdated(user, EntityType.USER, 1L, "Test Entity");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogEntityDeleted() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logEntityDeleted(user, EntityType.USER, 1L, "Test Entity");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogFileUploaded() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logFileUploaded(user, "test.pdf", 1024L);
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogFileDownloaded() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logFileDownloaded(user, "test.pdf");
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogSystemEvent() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logSystemEvent("System maintenance", LogSeverity.HIGH);
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }

    @Test
    void testLogSecurityEvent() {
        mock(ActivityLog.class);
        mock(ActivityLogRepository.class);

        when(activityLogRepository.save(Mockito.any(ActivityLog.class))).thenReturn(activityLog);

        activityLogService.logSecurityEvent("Failed login attempt", "192.168.1.1", LogSeverity.HIGH);
        verify(activityLogRepository).save(Mockito.any(ActivityLog.class));
    }
}