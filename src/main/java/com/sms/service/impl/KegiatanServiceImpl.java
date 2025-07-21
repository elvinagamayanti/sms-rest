/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Direktorat;
import com.sms.entity.Kegiatan;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.KegiatanMapper;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.KegiatanRepository;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.KegiatanService;
import com.sms.service.UserService;

/**
 *
 * @author pinaa
 */
@Service
public class KegiatanServiceImpl implements KegiatanService {
    private KegiatanRepository kegiatanRepository;
    private UserRepository userRepository;
    private SatkerRepository satkerRepository;
    private ProgramRepository programRepository;
    private OutputRepository outputRepository;
    private DirektoratRepository direktoratRepository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(KegiatanServiceImpl.class);

    public KegiatanServiceImpl(KegiatanRepository kegiatanRepository, UserRepository userRepository,
            SatkerRepository satkerRepository, ProgramRepository programRepository, OutputRepository outputRepository,
            DirektoratRepository direktoratRepository, UserService userService) {
        this.userRepository = userRepository;
        this.satkerRepository = satkerRepository;
        this.programRepository = programRepository;
        this.outputRepository = outputRepository;
        this.kegiatanRepository = kegiatanRepository;
        this.direktoratRepository = direktoratRepository;
        this.userService = userService;
    }

    @Override
    public List<KegiatanDto> findAllKegiatanFiltered() {
        User currentUser = userService.getUserLogged();
        if (currentUser == null) {
            throw new SecurityException("User tidak terautentikasi");
        }

        String userRole = userService.getCurrentUserHighestRole();

        switch (userRole) {
            case "ROLE_SUPERADMIN":
            case "ROLE_ADMIN_PUSAT":
            case "ROLE_OPERATOR_PUSAT":
                // Akses semua kegiatan nasional
                logger.info("User {} accessing all national kegiatan", currentUser.getName());
                return findAllKegiatan();

            case "ROLE_ADMIN_PROVINSI":
            case "ROLE_OPERATOR_PROVINSI":
                // Akses kegiatan dalam provinsi (semua satker dalam provinsi)
                logger.info("User {} accessing province-scoped kegiatan", currentUser.getName());
                return findKegiatanByProvinceScope(currentUser);

            case "ROLE_ADMIN_SATKER":
            case "ROLE_OPERATOR_SATKER":
                // Akses kegiatan hanya dalam satker sendiri
                logger.info("User {} accessing satker-scoped kegiatan", currentUser.getName());
                return findKegiatanBySatkerScope(currentUser.getSatker().getId());

            default:
                throw new SecurityException("Role tidak dikenali: " + userRole);
        }
    }

    private List<KegiatanDto> findKegiatanByProvinceScope(User currentUser) {
        if (currentUser.getSatker() == null) {
            throw new RuntimeException("User tidak memiliki satker");
        }

        String provinceCode = userService.extractProvinceCodeFromSatker(currentUser.getSatker().getCode());
        return findKegiatanByProvinceScope(provinceCode);
    }

    @Override
    public List<KegiatanDto> findKegiatanByProvinceScope(String provinceCode) {
        try {
            // Find all kegiatan yang di-assign ke satker dalam provinsi ini
            List<Kegiatan> kegiatans = kegiatanRepository.findByProvinceCode(provinceCode);
            logger.debug("Found {} kegiatan for province code {}", kegiatans.size(), provinceCode);

            return kegiatans.stream()
                    .map(KegiatanMapper::mapToKegiatanDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding kegiatan by province scope {}: {}", provinceCode, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<KegiatanDto> findKegiatanBySatkerScope(Long satkerId) {
        try {
            // Find kegiatan yang di-assign ke satker tertentu
            List<Kegiatan> kegiatans = kegiatanRepository.findBySatkerId(satkerId);
            logger.debug("Found {} kegiatan for satker {}", kegiatans.size(), satkerId);

            return kegiatans.stream()
                    .map(KegiatanMapper::mapToKegiatanDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding kegiatan by satker scope {}: {}", satkerId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean canAccessKegiatan(Long kegiatanId) {
        User currentUser = userService.getUserLogged();
        if (currentUser == null)
            return false;

        try {
            Kegiatan kegiatan = findKegiatanById(kegiatanId);
            String userRole = userService.getCurrentUserHighestRole();

            switch (userRole) {
                case "ROLE_SUPERADMIN":
                case "ROLE_ADMIN_PUSAT":
                case "ROLE_OPERATOR_PUSAT":
                    return true; // Can access all kegiatan

                case "ROLE_ADMIN_PROVINSI":
                case "ROLE_OPERATOR_PROVINSI":
                    // Can access kegiatan in same province
                    if (kegiatan.getSatker() == null)
                        return true; // Master kegiatan accessible
                    return userService.isSameProvince(
                            currentUser.getSatker().getCode(),
                            kegiatan.getSatker().getCode());

                case "ROLE_ADMIN_SATKER":
                case "ROLE_OPERATOR_SATKER":
                    // Can access kegiatan in same satker only
                    if (kegiatan.getSatker() == null)
                        return false; // No access to master kegiatan
                    return currentUser.getSatker().getId().equals(kegiatan.getSatker().getId());

                default:
                    return false;
            }

        } catch (Exception e) {
            logger.error("Error checking kegiatan access for user {} and kegiatan {}: {}",
                    currentUser.getId(), kegiatanId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canModifyKegiatan(Long kegiatanId) {
        User currentUser = userService.getUserLogged();
        if (currentUser == null)
            return false;

        try {
            Kegiatan kegiatan = findKegiatanById(kegiatanId);
            String userRole = userService.getCurrentUserHighestRole();

            switch (userRole) {
                case "ROLE_SUPERADMIN":
                case "ROLE_ADMIN_PUSAT":
                    return true; // Can modify all kegiatan

                case "ROLE_OPERATOR_PUSAT":
                    // Can only modify master kegiatan (not assigned to any satker)
                    return kegiatan.getSatker() == null;

                case "ROLE_ADMIN_PROVINSI":
                    // Can modify kegiatan assigned to satkers in same province
                    if (kegiatan.getSatker() == null)
                        return false; // No access to master
                    return userService.isSameProvince(
                            currentUser.getSatker().getCode(),
                            kegiatan.getSatker().getCode());

                case "ROLE_OPERATOR_PROVINSI":
                    // Cannot modify, only view and update status
                    return false;

                case "ROLE_ADMIN_SATKER":
                    // Can modify kegiatan assigned to their satker
                    if (kegiatan.getSatker() == null)
                        return false;
                    return currentUser.getSatker().getId().equals(kegiatan.getSatker().getId());

                case "ROLE_OPERATOR_SATKER":
                    // Cannot modify, only view and update status
                    return false;

                default:
                    return false;
            }

        } catch (Exception e) {
            logger.error("Error checking kegiatan modify permission for user {} and kegiatan {}: {}",
                    currentUser.getId(), kegiatanId, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getKegiatanStatisticsForCurrentScope() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            List<KegiatanDto> scopedKegiatan = findAllKegiatanFiltered();

            // Basic counts
            statistics.put("totalKegiatan", scopedKegiatan.size());

            // Active/inactive kegiatan (berdasarkan end date)
            Date now = new Date();
            long activeKegiatan = scopedKegiatan.stream()
                    .mapToLong(k -> k.getEndDate() == null || k.getEndDate().after(now) ? 1 : 0)
                    .sum();
            statistics.put("activeKegiatan", activeKegiatan);
            statistics.put("completedKegiatan", scopedKegiatan.size() - activeKegiatan);

            // User assignment status
            long assignedKegiatan = scopedKegiatan.stream()
                    .mapToLong(k -> k.getUser() != null ? 1 : 0)
                    .sum();
            statistics.put("assignedKegiatan", assignedKegiatan);
            statistics.put("unassignedKegiatan", scopedKegiatan.size() - assignedKegiatan);

            // Budget statistics
            BigDecimal totalBudget = scopedKegiatan.stream()
                    .filter(k -> k.getBudget() != null)
                    .map(KegiatanDto::getBudget)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("totalBudget", totalBudget);

            // Current user info
            User currentUser = userService.getUserLogged();
            if (currentUser != null) {
                statistics.put("currentUserRole", userService.getCurrentUserHighestRole());
                statistics.put("currentUserScope", determineKegiatanScope(currentUser));
                statistics.put("currentUserSatker", currentUser.getSatker().getName());
            }

        } catch (Exception e) {
            logger.error("Error generating kegiatan statistics: {}", e.getMessage());
            statistics.put("error", "Unable to generate statistics");
        }

        return statistics;
    }

    private String determineKegiatanScope(User user) {
        String role = userService.getCurrentUserHighestRole();

        switch (role) {
            case "ROLE_SUPERADMIN":
            case "ROLE_ADMIN_PUSAT":
            case "ROLE_OPERATOR_PUSAT":
                return "National";
            case "ROLE_ADMIN_PROVINSI":
            case "ROLE_OPERATOR_PROVINSI":
                String provinceCode = userService.extractProvinceCodeFromSatker(user.getSatker().getCode());
                return "Province " + provinceCode;
            case "ROLE_ADMIN_SATKER":
            case "ROLE_OPERATOR_SATKER":
                return "Satker " + user.getSatker().getCode();
            default:
                return "Limited";
        }
    }

    // ====================================
    // CORE CRUD METHODS (UPDATED)
    // ====================================

    @Override
    public List<KegiatanDto> ambilDaftarKegiatan() {
        // DEPRECATED - use findAllKegiatanFiltered() instead for security
        logger.warn("Using deprecated ambilDaftarKegiatan() method - should use findAllKegiatanFiltered()");
        return findAllKegiatanFiltered();
    }

    private List<KegiatanDto> findAllKegiatan() {
        // Internal method for unfiltered access (pusat only)
        List<Kegiatan> kegiatans = kegiatanRepository.findAll();
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KegiatanDto simpanDataKegiatan(KegiatanDto kegiatanDto) {
        try {
            Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);

            // Get current user for auto-assignment
            User currentUser = userService.getUserLogged();
            if (currentUser == null) {
                throw new RuntimeException("User tidak terautentikasi");
            }

            // Handle user assignment
            if (kegiatanDto.getUser() != null && kegiatanDto.getUser().getId() != null) {
                User user = userRepository.findById(kegiatanDto.getUser().getId())
                        .orElseThrow(
                                () -> new RuntimeException("User not found with id: " + kegiatanDto.getUser().getId()));
                kegiatan.setUser(user);

                // AUTO-ASSIGN: Set direktorat penanggung jawab berdasarkan direktorat user
                if (user.getDirektorat() != null) {
                    kegiatan.setDirektoratPenanggungJawab(user.getDirektorat());
                    logger.info("Auto-assigned direktorat PJ: {}", user.getDirektorat().getName());
                }
            } else {
                // For master kegiatan created by pusat, user might be null initially
                kegiatan.setUser(null);
            }

            // Handle satker assignment
            if (kegiatanDto.getSatker() != null && kegiatanDto.getSatker().getId() != null) {
                Satker satker = satkerRepository.findById(kegiatanDto.getSatker().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Satker not found with id: " + kegiatanDto.getSatker().getId()));
                kegiatan.setSatker(satker);
            } else {
                // Master kegiatan (created by pusat) has no satker initially
                kegiatan.setSatker(null);
            }

            // Handle program and output
            if (kegiatanDto.getOutput() != null && kegiatanDto.getOutput().getId() != null) {
                Output output = outputRepository.findById(kegiatanDto.getOutput().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Output not found with id: " + kegiatanDto.getOutput().getId()));
                kegiatan.setOutput(output);

                // Auto-assign program from output
                Program program = programRepository.findById(output.getProgram().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Program not found with id: " + output.getProgram().getId()));
                kegiatan.setProgram(program);
            }

            Kegiatan saved = kegiatanRepository.save(kegiatan);
            logger.info("Kegiatan created: {} by user {}", saved.getName(), currentUser.getName());

            return KegiatanMapper.mapToKegiatanDto(saved);

        } catch (Exception e) {
            logger.error("Error saving kegiatan: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menyimpan kegiatan: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void perbaruiDataKegiatan(KegiatanDto kegiatanDto) {
        try {
            Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);

            // Auto-assign direktorat penanggung jawab dari user
            if (kegiatan.getUser() != null && kegiatan.getUser().getDirektorat() != null) {
                kegiatan.setDirektoratPenanggungJawab(kegiatan.getUser().getDirektorat());
                logger.info("Updated direktorat PJ: {}", kegiatan.getUser().getDirektorat().getName());
            }

            kegiatanRepository.save(kegiatan);
            logger.info("Kegiatan updated: {}", kegiatan.getName());

        } catch (Exception e) {
            logger.error("Error updating kegiatan: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mengupdate kegiatan: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void hapusDataKegiatan(Long kegiatanId) {
        try {
            // Validate kegiatan exists and user can delete it
            Kegiatan kegiatan = findKegiatanById(kegiatanId);

            kegiatanRepository.deleteById(kegiatanId);
            logger.info("Kegiatan deleted: {} (ID: {})", kegiatan.getName(), kegiatanId);

        } catch (Exception e) {
            logger.error("Error deleting kegiatan {}: {}", kegiatanId, e.getMessage(), e);
            throw new RuntimeException("Gagal menghapus kegiatan: " + e.getMessage());
        }
    }

    @Override
    public KegiatanDto cariKegiatanById(Long id) {
        Kegiatan kegiatan = findKegiatanById(id);
        return KegiatanMapper.mapToKegiatanDto(kegiatan);
    }

    @Override
    public Kegiatan findKegiatanById(Long id) {
        return kegiatanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + id));
    }

    @Override
    public KegiatanDto patchKegiatan(Long kegiatanId, Map<String, Object> updates) {
        final Kegiatan[] kegiatanHolder = new Kegiatan[1];
        kegiatanHolder[0] = kegiatanRepository.findById(kegiatanId)
                .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

        // Update only the fields that are provided
        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        kegiatanHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        kegiatanHolder[0].setCode((String) value);
                }
                case "budget" -> {
                    if (value != null && value instanceof Number) {
                        kegiatanHolder[0].setBudget(new BigDecimal(value.toString()));
                    }
                }
                case "startDate" -> {
                    if (value != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            kegiatanHolder[0].setStartDate(sdf.parse((String) value));
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Invalid date format for startDate. Use yyyy-MM-dd format. Error: "
                                            + e.getMessage());
                        }
                    }
                }
                case "endDate" -> {
                    if (value != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            kegiatanHolder[0].setEndDate(sdf.parse((String) value));
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Invalid date format for endDate. Use yyyy-MM-dd format. Error: " + e.getMessage());
                        }
                    }
                }
                case "user" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userData = (Map<String, Object>) value;
                        Long userId = Long.valueOf(userData.get("id").toString());
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                        kegiatanHolder[0].setUser(user);

                        // AUTO-UPDATE: Update direktorat penanggung jawab saat user berubah
                        if (user.getDirektorat() != null) {
                            kegiatanHolder[0].setDirektoratPenanggungJawab(user.getDirektorat());
                        }
                    }
                }
                case "satker" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> satkerData = (Map<String, Object>) value;
                        Long satkerId = Long.valueOf(satkerData.get("id").toString());
                        Satker satker = satkerRepository.findById(satkerId)
                                .orElseThrow(() -> new RuntimeException("Satker not found with id: " + satkerId));
                        kegiatanHolder[0].setSatker(satker);
                    }
                }
                case "program" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> programData = (Map<String, Object>) value;
                        Long programId = Long.valueOf(programData.get("id").toString());
                        Program program = programRepository.findById(programId)
                                .orElseThrow(() -> new RuntimeException("Program not found with id: " + programId));
                        kegiatanHolder[0].setProgram(program);
                    }
                }
                case "output" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> outputData = (Map<String, Object>) value;
                        Long outputId = Long.valueOf(outputData.get("id").toString());
                        Output output = outputRepository.findById(outputId)
                                .orElseThrow(() -> new RuntimeException("Output not found with id: " + outputId));
                        kegiatanHolder[0].setOutput(output);
                    }
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        kegiatanHolder[0] = kegiatanRepository.save(kegiatanHolder[0]);
        return KegiatanMapper.mapToKegiatanDto(kegiatanHolder[0]);
    }

    @Override
    public List<KegiatanDto> getKegiatanByDirektoratPJ(Long direktoratId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByDirektoratPenanggungJawabId(direktoratId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> getKegiatanByDirektoratPJCode(String direktoratCode) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByDirektoratPenanggungJawabCode(direktoratCode);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> getKegiatanByDeputiPJ(Long deputiId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByDeputiPenanggungJawabId(deputiId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> getKegiatanByDeputiPJCode(String deputiCode) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByDeputiPenanggungJawabCode(deputiCode);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> getKegiatanByYearAndDirektoratPJ(int year, Long direktoratId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByYearAndDirektoratPenanggungJawabId(year, direktoratId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getKegiatanStatisticsByDirektorat() {
        List<Object[]> results = kegiatanRepository.countKegiatanByDirektorat();
        Map<String, Object> statistics = new HashMap<>();

        for (Object[] result : results) {
            String direktoratName = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(direktoratName != null ? direktoratName : "Tanpa Direktorat", count);
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getKegiatanStatisticsByDeputi() {
        List<Object[]> results = kegiatanRepository.countKegiatanByDeputi();
        Map<String, Object> statistics = new HashMap<>();

        for (Object[] result : results) {
            String deputiName = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(deputiName != null ? deputiName : "Tanpa Deputi", count);
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getBudgetStatisticsByDirektorat() {
        List<Object[]> results = kegiatanRepository.getTotalBudgetByDirektorat();
        Map<String, Object> statistics = new HashMap<>();

        for (Object[] result : results) {
            String direktoratName = (String) result[0];
            BigDecimal totalBudget = (BigDecimal) result[1];
            statistics.put(direktoratName != null ? direktoratName : "Tanpa Direktorat", totalBudget);
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getBudgetStatisticsByDeputi() {
        List<Object[]> results = kegiatanRepository.getTotalBudgetByDeputi();
        Map<String, Object> statistics = new HashMap<>();

        for (Object[] result : results) {
            String deputiName = (String) result[0];
            BigDecimal totalBudget = (BigDecimal) result[1];
            statistics.put(deputiName != null ? deputiName : "Tanpa Deputi", totalBudget);
        }

        return statistics;
    }

    @Override
    public List<KegiatanDto> getKegiatanWithoutDirektoratPJ() {
        List<Kegiatan> kegiatans = kegiatanRepository.findKegiatanWithoutDirektoratPJ();
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> searchKegiatan(String query) {
        List<Kegiatan> kegiatans = kegiatanRepository.searchKegiatan(query);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> filterKegiatan(Long direktoratId, Integer year, Long programId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findWithFilters(direktoratId, year, programId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getMonthlyStatistics(int year, Long direktoratId) {
        List<Object[]> results = kegiatanRepository.getMonthlyStatistics(year, direktoratId);
        Map<String, Object> monthlyStats = new HashMap<>();

        // Initialize all months with 0
        for (int i = 1; i <= 12; i++) {
            monthlyStats.put("month_" + i, 0L);
        }

        // Fill actual data
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            monthlyStats.put("month_" + month, count);
        }

        return monthlyStats;
    }

    @Override
    public void assignDirektoratPJ(Long kegiatanId, Long direktoratId) {
        Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

        Direktorat direktorat = direktoratRepository.findById(direktoratId)
                .orElseThrow(() -> new RuntimeException("Direktorat not found with id: " + direktoratId));

        kegiatan.setDirektoratPenanggungJawab(direktorat);
        kegiatanRepository.save(kegiatan);
    }

    @Override
    public Map<String, Object> syncDirektoratPJFromUser() {
        List<Kegiatan> allKegiatans = kegiatanRepository.findAll();
        int syncedCount = 0;
        int errorCount = 0;

        for (Kegiatan kegiatan : allKegiatans) {
            try {
                if (kegiatan.getUser() != null && kegiatan.getUser().getDirektorat() != null) {
                    kegiatan.setDirektoratPenanggungJawab(kegiatan.getUser().getDirektorat());
                    kegiatanRepository.save(kegiatan);
                    syncedCount++;
                }
            } catch (Exception e) {
                errorCount++;
                // Log error but continue with other records
                System.err.println("Error syncing kegiatan ID " + kegiatan.getId() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalKegiatan", allKegiatans.size());
        result.put("syncedCount", syncedCount);
        result.put("errorCount", errorCount);
        result.put("message",
                "Sync completed: " + syncedCount + " kegiatan berhasil di-sync, " + errorCount + " error");

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> assignKegiatanToSatkers(Long kegiatanId, List<Long> satkerIds) {
        Map<String, Object> result = new HashMap<>();

        // 1. Validasi input
        if (satkerIds == null || satkerIds.isEmpty()) {
            throw new IllegalArgumentException("Daftar satker tidak boleh kosong");
        }

        // 2. Ambil kegiatan master
        Kegiatan masterKegiatan = kegiatanRepository.findById(kegiatanId)
                .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

        List<String> successSatkers = new ArrayList<>();
        List<String> failedSatkers = new ArrayList<>();

        // 3. Process setiap satker - TANPA TRY-CATCH INTERNAL
        for (Long satkerId : satkerIds) {
            // Validasi satker
            Satker targetSatker = satkerRepository.findById(satkerId)
                    .orElseThrow(() -> new RuntimeException("Satker not found with id: " + satkerId));

            // Cek duplikasi
            boolean exists = kegiatanRepository.existsByNameAndSatkerId(
                    masterKegiatan.getName(), satkerId);

            if (exists) {
                failedSatkers.add(targetSatker.getName() + " (sudah ada)");
                continue; // Skip tanpa exception
            }

            // Buat dan simpan kegiatan baru
            Kegiatan assignedKegiatan = createDuplicateKegiatan(masterKegiatan, targetSatker);
            kegiatanRepository.save(assignedKegiatan);

            successSatkers.add(targetSatker.getName());
        }

        // 4. Buat response
        result.put("success", true);
        result.put("message", "Proses assign kegiatan selesai");
        result.put("masterKegiatan", masterKegiatan.getName());
        result.put("totalSatker", satkerIds.size());
        result.put("successCount", successSatkers.size());
        result.put("failedCount", failedSatkers.size());
        result.put("successSatkers", successSatkers);
        result.put("failedSatkers", failedSatkers);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> assignKegiatanToProvinces(Long kegiatanId, List<String> provinceCodes) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Ambil semua satker berdasarkan kode provinsi
            List<Long> satkerIds = new ArrayList<>();

            for (String provinceCode : provinceCodes) {
                List<Satker> satkers = satkerRepository.findByCodeStartingWith(provinceCode);
                satkerIds.addAll(satkers.stream().map(Satker::getId).collect(Collectors.toList()));
            }

            // Gunakan method assignKegiatanToSatkers
            result = assignKegiatanToSatkers(kegiatanId, satkerIds);
            result.put("assignType", "by_provinces");
            result.put("provinceCodes", provinceCodes);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saat assign kegiatan by provinces: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<KegiatanDto> getAssignedKegiatanBySatker(Long satkerId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findBySatkerId(satkerId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method untuk membuat duplikasi kegiatan
     */
    private Kegiatan createDuplicateKegiatan(Kegiatan masterKegiatan, Satker targetSatker) {
        return Kegiatan.builder()
                .name(masterKegiatan.getName())
                .code(generateKegiatanCode(masterKegiatan.getCode(), targetSatker))
                .budget(masterKegiatan.getBudget())
                .startDate(masterKegiatan.getStartDate())
                .endDate(masterKegiatan.getEndDate())
                .satker(targetSatker)
                .program(masterKegiatan.getProgram())
                .output(masterKegiatan.getOutput())
                .direktoratPenanggungJawab(masterKegiatan.getDirektoratPenanggungJawab())
                // User akan di-set ketika satker daerah mulai mengerjakan
                .user(null) // Initially null, akan di-assign nanti oleh satker daerah
                .build();
    }

    /**
     * Generate kode kegiatan untuk satker daerah
     */
    private String generateKegiatanCode(String masterCode, Satker targetSatker) {
        if (masterCode == null || masterCode.isEmpty()) {
            return targetSatker.getCode() + "-" + System.currentTimeMillis();
        }
        return masterCode + "-" + targetSatker.getCode();
    }

    @Override
    @Transactional
    public Map<String, Object> assignUserToKegiatan(Long kegiatanId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Validasi kegiatan ada
            Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                    .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

            // 2. Validasi user ada dan aktif
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            if (!user.getIsActive()) {
                throw new RuntimeException("User tidak aktif, tidak dapat ditugaskan");
            }

            // 3. Validasi user dan kegiatan dalam satker yang sama
            if (!kegiatan.getSatker().getId().equals(user.getSatker().getId())) {
                throw new RuntimeException("User dan kegiatan harus dalam satker yang sama");
            }

            // 4. Cek apakah kegiatan sudah ditugaskan ke user lain
            if (kegiatan.getUser() != null && !kegiatan.getUser().getId().equals(userId)) {
                result.put("success", false);
                result.put("message", "Kegiatan sudah ditugaskan ke user lain: " + kegiatan.getUser().getName());
                result.put("currentAssignedUser", kegiatan.getUser().getName());
                return result;
            }

            // 5. Assign user ke kegiatan
            kegiatan.setUser(user);

            // 6. Auto-update direktorat PJ berdasarkan user yang ditugaskan
            if (user.getDirektorat() != null) {
                kegiatan.setDirektoratPenanggungJawab(user.getDirektorat());
            }

            // 7. Simpan perubahan
            Kegiatan savedKegiatan = kegiatanRepository.save(kegiatan);

            // 8. Build response
            result.put("success", true);
            result.put("message", "User berhasil ditugaskan ke kegiatan");
            result.put("kegiatanId", savedKegiatan.getId());
            result.put("kegiatanName", savedKegiatan.getName());
            result.put("assignedUserId", user.getId());
            result.put("assignedUserName", user.getName());
            result.put("satkerName", kegiatan.getSatker().getName());
            result.put("assignedDate", new Date());

            System.out.println("Kegiatan '" + kegiatan.getName() +
                    "' berhasil ditugaskan ke user: " + user.getName());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saat assign user ke kegiatan: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> claimKegiatan(Long kegiatanId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Ambil current user yang sedang login
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                throw new RuntimeException("User tidak terautentikasi");
            }

            // Gunakan method assignUserToKegiatan
            result = assignUserToKegiatan(kegiatanId, currentUser.getId());

            if ((Boolean) result.get("success")) {
                result.put("claimType", "self_claim");
                result.put("message", "Kegiatan berhasil di-claim untuk diri sendiri");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saat claim kegiatan: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> unassignUserFromKegiatan(Long kegiatanId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Ambil kegiatan
            Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                    .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

            // 2. Simpan info user sebelumnya untuk response
            String previousUserName = kegiatan.getUser() != null ? kegiatan.getUser().getName() : "Tidak ada";

            // 3. Set user ke null (unassign)
            kegiatan.setUser(null);

            // 4. Simpan perubahan
            kegiatanRepository.save(kegiatan);

            // 5. Build response
            result.put("success", true);
            result.put("message", "User berhasil dilepas dari kegiatan");
            result.put("kegiatanId", kegiatanId);
            result.put("kegiatanName", kegiatan.getName());
            result.put("previousUser", previousUserName);
            result.put("unassignedDate", new Date());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saat unassign user: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<KegiatanDto> getUnassignedKegiatanBySatker(Long satkerId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findUnassignedKegiatanBySatkerId(satkerId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KegiatanDto> getKegiatanByAssignedUser(Long userId) {
        List<Kegiatan> kegiatans = kegiatanRepository.findByUserId(userId);
        return kegiatans.stream()
                .map(KegiatanMapper::mapToKegiatanDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> transferKegiatanToUser(Long kegiatanId, Long fromUserId, Long toUserId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Validasi kegiatan
            Kegiatan kegiatan = kegiatanRepository.findById(kegiatanId)
                    .orElseThrow(() -> new RuntimeException("Kegiatan not found with id: " + kegiatanId));

            // 2. Validasi kegiatan saat ini ditugaskan ke fromUser
            if (kegiatan.getUser() == null || !kegiatan.getUser().getId().equals(fromUserId)) {
                throw new RuntimeException("Kegiatan tidak ditugaskan ke user asal yang dimaksud");
            }

            // 3. Validasi target user
            User toUser = userRepository.findById(toUserId)
                    .orElseThrow(() -> new RuntimeException("Target user not found with id: " + toUserId));

            // 4. Validasi kedua user dalam satker yang sama
            if (!kegiatan.getSatker().getId().equals(toUser.getSatker().getId())) {
                throw new RuntimeException("Target user harus dalam satker yang sama");
            }

            // 5. Simpan info untuk response
            String fromUserName = kegiatan.getUser().getName();
            String toUserName = toUser.getName();

            // 6. Transfer assignment
            kegiatan.setUser(toUser);

            // 7. Update direktorat PJ jika berbeda
            if (toUser.getDirektorat() != null) {
                kegiatan.setDirektoratPenanggungJawab(toUser.getDirektorat());
            }

            // 8. Simpan perubahan
            kegiatanRepository.save(kegiatan);

            // 9. Build response
            result.put("success", true);
            result.put("message", "Kegiatan berhasil ditransfer");
            result.put("kegiatanId", kegiatanId);
            result.put("kegiatanName", kegiatan.getName());
            result.put("fromUser", fromUserName);
            result.put("toUser", toUserName);
            result.put("transferDate", new Date());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saat transfer kegiatan: " + e.getMessage());
        }

        return result;
    }
}