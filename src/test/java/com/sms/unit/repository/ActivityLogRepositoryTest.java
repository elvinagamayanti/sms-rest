package com.sms.unit.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import com.sms.repository.ActivityLogRepository;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.ProvinceRepository;
import com.sms.repository.RoleRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;

@DataJpaTest
public class ActivityLogRepositoryTest {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SatkerRepository satkerRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DirektoratRepository direktoratRepository;

    @Autowired
    private DeputiRepository deputiRepository;

    ActivityLog activityLog;
    ActivityLog activityLog2;
    User user;
    Role role;
    Satker satker;
    Province province;
    Direktorat direktorat;
    Deputi deputi;

    @BeforeEach
    void setUp() {
        // Setup dependencies
        role = new Role();
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
        user.setName("Test User");
        user.setNip("1234567890");
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setIsActive(true);
        user.setSatker(satker);
        user.setDirektorat(direktorat);
        user.setRoles(List.of(role));
        userRepository.save(user);

        // Setup ActivityLog entities
        activityLog = ActivityLog.builder()
                .user(user)
                .userEmail(user.getEmail())
                .userName(user.getName())
                .activityType(ActivityType.CREATE)
                .entityType(EntityType.USER)
                .entityId(user.getId())
                .entityName(user.getName())
                .description("User created successfully")
                .details("New user account created with basic permissions")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0 Test Browser")
                .severity(LogSeverity.MEDIUM)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationSent(false)
                .build();
        activityLogRepository.save(activityLog);

        activityLog2 = ActivityLog.builder()
                .user(user)
                .userEmail(user.getEmail())
                .userName(user.getName())
                .activityType(ActivityType.UPDATE)
                .entityType(EntityType.PROGRAM)
                .entityId(100L)
                .entityName("Test Program")
                .description("Program updated")
                .details("Program details were modified")
                .ipAddress("192.168.1.2")
                .userAgent("Mozilla/5.0 Test Browser")
                .severity(LogSeverity.LOW)
                .createdAt(LocalDateTime.now().minusHours(1))
                .isRead(true)
                .notificationSent(true)
                .build();
        activityLogRepository.save(activityLog2);
    }

    @AfterEach
    void tearDown() {
        activityLog = null;
        activityLog2 = null;
        user = null;
        role = null;
        satker = null;
        province = null;
        direktorat = null;
        deputi = null;

        activityLogRepository.deleteAll();
        userRepository.deleteAll();
        direktoratRepository.deleteAll();
        deputiRepository.deleteAll();
        satkerRepository.deleteAll();
        provinceRepository.deleteAll();
        roleRepository.deleteAll();
    }

    // Test Success

    @Test
    public void testFindById_Found() {
        Optional<ActivityLog> foundLog = activityLogRepository.findById(activityLog.getId());
        assertThat(foundLog).isPresent();
        assertThat(foundLog.get().getDescription()).isEqualTo(activityLog.getDescription());
        assertThat(foundLog.get().getActivityType()).isEqualTo(ActivityType.CREATE);
        assertThat(foundLog.get().getEntityType()).isEqualTo(EntityType.USER);
    }

    @Test
    public void testFindAllByUserIdOrderByCreatedAtDesc_Found() {
        List<ActivityLog> foundLogs = activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        assertThat(foundLogs).isNotEmpty();
        assertThat(foundLogs).hasSize(2);
        // Should be ordered by created date descending (newest first)
        assertThat(foundLogs.get(0).getCreatedAt()).isAfter(foundLogs.get(1).getCreatedAt());
    }

    @Test
    public void testFindByUserIdOrderByCreatedAtDesc_WithPagination() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<ActivityLog> foundLogs = activityLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        assertThat(foundLogs.getContent()).hasSize(1);
        assertThat(foundLogs.getTotalElements()).isEqualTo(2);
        assertThat(foundLogs.getContent().get(0).getDescription()).isEqualTo(activityLog.getDescription());
    }

    @Test
    public void testFindAllByActivityTypeOrderByCreatedAtDesc_Found() {
        List<ActivityLog> foundLogs = activityLogRepository
                .findAllByActivityTypeOrderByCreatedAtDesc(ActivityType.CREATE);
        assertThat(foundLogs).isNotEmpty();
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getActivityType()).isEqualTo(ActivityType.CREATE);
    }

    @Test
    public void testFindByActivityTypeOrderByCreatedAtDesc_WithPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ActivityLog> foundLogs = activityLogRepository.findByActivityTypeOrderByCreatedAtDesc(ActivityType.UPDATE,
                pageable);
        assertThat(foundLogs.getContent()).hasSize(1);
        assertThat(foundLogs.getContent().get(0).getActivityType()).isEqualTo(ActivityType.UPDATE);
    }

    @Test
    public void testFindAllByEntityTypeOrderByCreatedAtDesc_Found() {
        List<ActivityLog> foundLogs = activityLogRepository.findAllByEntityTypeOrderByCreatedAtDesc(EntityType.USER);
        assertThat(foundLogs).isNotEmpty();
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getEntityType()).isEqualTo(EntityType.USER);
    }

    @Test
    public void testFindByEntityTypeOrderByCreatedAtDesc_WithPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ActivityLog> foundLogs = activityLogRepository.findByEntityTypeOrderByCreatedAtDesc(EntityType.PROGRAM,
                pageable);
        assertThat(foundLogs.getContent()).hasSize(1);
        assertThat(foundLogs.getContent().get(0).getEntityType()).isEqualTo(EntityType.PROGRAM);
    }

    @Test
    public void testFindByEntityTypeAndEntityIdOrderByCreatedAtDesc_Found() {
        List<ActivityLog> foundLogs = activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                EntityType.USER, user.getId());
        assertThat(foundLogs).isNotEmpty();
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getEntityId()).isEqualTo(user.getId());
    }

    @Test
    public void testFindAllBySeverityOrderByCreatedAtDesc_Found() {
        List<ActivityLog> foundLogs = activityLogRepository.findAllBySeverityOrderByCreatedAtDesc(LogSeverity.MEDIUM);
        assertThat(foundLogs).isNotEmpty();
        assertThat(foundLogs).hasSize(1);
        assertThat(foundLogs.get(0).getSeverity()).isEqualTo(LogSeverity.MEDIUM);
    }

    @Test
    public void testFindBySeverityOrderByCreatedAtDesc_WithPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ActivityLog> foundLogs = activityLogRepository.findBySeverityOrderByCreatedAtDesc(LogSeverity.LOW,
                pageable);
        assertThat(foundLogs.getContent()).hasSize(1);
        assertThat(foundLogs.getContent().get(0).getSeverity()).isEqualTo(LogSeverity.LOW);
    }

    @Test
    public void testSearchActivities_Found() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> foundLogs = activityLogRepository.searchActivities("User created", pageable);
        assertThat(foundLogs.getContent()).isNotEmpty();
        assertThat(foundLogs.getContent().get(0).getDescription()).contains("User created");
    }

    @Test
    public void testCountUnreadLogs() {
        Long count = activityLogRepository.countUnreadLogs();
        assertThat(count).isEqualTo(1L); // Only activityLog is unread
    }

    @Test
    public void testCountUnreadLogsByUserId() {
        Long count = activityLogRepository.countUnreadLogsByUserId(user.getId());
        assertThat(count).isEqualTo(1L); // Only activityLog is unread for this user
    }

    @Test
    public void testCountRecentActivities() {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        Long count = activityLogRepository.countRecentActivities(since);
        assertThat(count).isEqualTo(2L); // Both logs are recent
    }

    @Test
    public void testCountBySeverity() {
        Long count = activityLogRepository.countBySeverity(LogSeverity.MEDIUM);
        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void testGetActivityTypeStatistics() {
        List<Object[]> stats = activityLogRepository.getActivityTypeStatistics();
        assertThat(stats).isNotEmpty();
        assertThat(stats).hasSize(2); // CREATE and UPDATE
    }

    @Test
    public void testGetEntityTypeStatistics() {
        List<Object[]> stats = activityLogRepository.getEntityTypeStatistics();
        assertThat(stats).isNotEmpty();
        assertThat(stats).hasSize(2); // USER and PROGRAM
    }

    @Test
    public void testGetSeverityStatistics() {
        List<Object[]> stats = activityLogRepository.getSeverityStatistics();
        assertThat(stats).isNotEmpty();
        assertThat(stats).hasSize(2); // MEDIUM and LOW
    }

    @Test
    public void testGetDailyActivityCount() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Object[]> counts = activityLogRepository.getDailyActivityCount(since);
        assertThat(counts).isNotEmpty();
    }

    @Test
    public void testFindWithFilters_AllParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> foundLogs = activityLogRepository.findWithFilters(
                ActivityType.CREATE,
                EntityType.USER,
                LogSeverity.MEDIUM,
                user.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                pageable);
        assertThat(foundLogs.getContent()).hasSize(1);
        assertThat(foundLogs.getContent().get(0).getActivityType()).isEqualTo(ActivityType.CREATE);
    }

    @Test
    public void testFindWithFilters_NullParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> foundLogs = activityLogRepository.findWithFilters(
                null, null, null, null, null, null, pageable);
        assertThat(foundLogs.getContent()).hasSize(2); // Should return all logs
    }

    @Test
    public void testMarkAsRead() {
        int updated = activityLogRepository.markAsRead(activityLog.getId());
        assertThat(updated).isEqualTo(1);

        // Verify the change
        // ActivityLog updatedLog =
        // activityLogRepository.findById(activityLog.getId()).orElse(null);
        // assertThat(updatedLog).isNotNull();
        // assertThat(updatedLog.getIsRead()).isTrue();
    }

    @Test
    public void testMarkAllAsReadByUserId() {
        int updated = activityLogRepository.markAllAsReadByUserId(user.getId());
        assertThat(updated).isEqualTo(2); // Both logs should be marked as read
    }

    @Test
    public void testMarkNotificationSent() {
        int updated = activityLogRepository.markNotificationSent(activityLog.getId());
        assertThat(updated).isEqualTo(1);

        // Verify the change
        // ActivityLog updatedLog =
        // activityLogRepository.findById(activityLog.getId()).orElse(null);
        // assertThat(updatedLog).isNotNull();
        // assertThat(updatedLog.getNotificationSent()).isTrue();
    }

    @Test
    public void testMarkNotificationSentBatch() {
        List<Long> ids = List.of(activityLog.getId(), activityLog2.getId());
        int updated = activityLogRepository.markNotificationSentBatch(ids);
        assertThat(updated).isEqualTo(2);
    }

    @Test
    public void testDeleteOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        int deleted = activityLogRepository.deleteOldLogs(cutoff);
        assertThat(deleted).isEqualTo(1); // Only activityLog2 should be deleted
    }

    // test Failure

    @Test
    public void testFindById_NotFound() {
        Optional<ActivityLog> foundLog = activityLogRepository.findById(999L);
        assertThat(foundLog).isEmpty();
    }

    @Test
    public void testFindAllByUserIdOrderByCreatedAtDesc_NotFound() {
        List<ActivityLog> foundLogs = activityLogRepository.findAllByUserIdOrderByCreatedAtDesc(999L);
        assertThat(foundLogs).isEmpty();
    }

    @Test
    public void testFindAllByActivityTypeOrderByCreatedAtDesc_NotFound() {
        List<ActivityLog> foundLogs = activityLogRepository
                .findAllByActivityTypeOrderByCreatedAtDesc(ActivityType.DELETE);
        assertThat(foundLogs).isEmpty();
    }

    @Test
    public void testFindAllByEntityTypeOrderByCreatedAtDesc_NotFound() {
        List<ActivityLog> foundLogs = activityLogRepository
                .findAllByEntityTypeOrderByCreatedAtDesc(EntityType.KEGIATAN);
        assertThat(foundLogs).isEmpty();
    }

    @Test
    public void testFindByEntityTypeAndEntityIdOrderByCreatedAtDesc_NotFound() {
        List<ActivityLog> foundLogs = activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                EntityType.USER, 999L);
        assertThat(foundLogs).isEmpty();
    }

    @Test
    public void testFindAllBySeverityOrderByCreatedAtDesc_NotFound() {
        List<ActivityLog> foundLogs = activityLogRepository.findAllBySeverityOrderByCreatedAtDesc(LogSeverity.CRITICAL);
        assertThat(foundLogs).isEmpty();
    }

    @Test
    public void testSearchActivities_NotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> foundLogs = activityLogRepository.searchActivities("NonExistentActivity", pageable);
        assertThat(foundLogs.getContent()).isEmpty();
    }

    @Test
    public void testCountUnreadLogsByUserId_NotFound() {
        Long count = activityLogRepository.countUnreadLogsByUserId(999L);
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testCountBySeverity_NotFound() {
        Long count = activityLogRepository.countBySeverity(LogSeverity.CRITICAL);
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void testMarkAsRead_NotFound() {
        int updated = activityLogRepository.markAsRead(999L);
        assertThat(updated).isEqualTo(0);
    }

    @Test
    public void testMarkAllAsReadByUserId_NotFound() {
        int updated = activityLogRepository.markAllAsReadByUserId(999L);
        assertThat(updated).isEqualTo(0);
    }

    @Test
    public void testMarkNotificationSent_NotFound() {
        int updated = activityLogRepository.markNotificationSent(999L);
        assertThat(updated).isEqualTo(0);
    }

    @Test
    public void testDeleteOldLogs_NoOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        int deleted = activityLogRepository.deleteOldLogs(cutoff);
        assertThat(deleted).isEqualTo(0);
    }
}