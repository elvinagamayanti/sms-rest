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
    public List<KegiatanDto> ambilDaftarKegiatan() {
        List<Kegiatan> kegiatans = this.kegiatanRepository.findAll();
        List<KegiatanDto> kegiatanDtos = kegiatans.stream()
                .map((kegiatan) -> (KegiatanMapper.mapToKegiatanDto(kegiatan)))
                .collect(Collectors.toList());
        return kegiatanDtos;
    }

    @Override
    public void hapusDataKegiatan(Long kegiatanId) {
        kegiatanRepository.deleteById(kegiatanId);
    }

    @Override
    public void perbaruiDataKegiatan(KegiatanDto kegiatanDto) {
        Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);

        // Auto-assign direktorat penanggung jawab dari user
        if (kegiatan.getUser() != null && kegiatan.getUser().getDirektorat() != null) {
            kegiatan.setDirektoratPenanggungJawab(kegiatan.getUser().getDirektorat());
        }

        System.out.println(kegiatanDto);
        kegiatanRepository.save(kegiatan);
    }

    @Override
    public KegiatanDto simpanDataKegiatan(KegiatanDto kegiatanDto) {
        Kegiatan kegiatan = KegiatanMapper.mapToKegiatan(kegiatanDto);
        User user = userRepository.findById(kegiatanDto.getUser().getId())
                .orElseThrow(
                        () -> new RuntimeException("User not found with id: " + kegiatanDto.getUser().getId()));
        System.out.println("User: " + user);

        Satker satker = satkerRepository.findById(user.getSatker().getId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Satker not found with id: " + user.getSatker().getId()));
        System.out.println("Satker: " + satker);

        Output output = outputRepository.findById(kegiatanDto.getOutput().getId())
                .orElseThrow(
                        () -> new RuntimeException("Output not found with id: " + kegiatanDto.getOutput().getId()));

        Program program = programRepository.findById(output.getProgram().getId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Program not found with id: " + output.getProgram().getId()));

        kegiatan.setUser(user);
        System.out.println(kegiatan.getUser().getId());
        kegiatan.setSatker(satker);
        System.out.println(kegiatan.getSatker().getId());
        kegiatan.setProgram(program);
        System.out.println(kegiatan.getProgram().getId());
        kegiatan.setOutput(output);
        System.out.println(kegiatan.getOutput().getId());
        // kegiatanRepository.save(kegiatan);

        // AUTO-ASSIGN: Set direktorat penanggung jawab berdasarkan direktorat user
        if (user.getDirektorat() != null) {
            kegiatan.setDirektoratPenanggungJawab(user.getDirektorat());
            System.out.println("Direktorat PJ: " + user.getDirektorat().getName());
        } else {
            System.out.println("Warning: User tidak memiliki direktorat, direktorat PJ tidak di-set");
        }

        Kegiatan saved = kegiatanRepository.save(kegiatan);
        return KegiatanMapper.mapToKegiatanDto(saved);
    }

    @Override
    public KegiatanDto cariKegiatanById(Long id) {
        Kegiatan kegiatan = kegiatanRepository.findById(id).get();
        return KegiatanMapper.mapToKegiatanDto(kegiatan);
    }

    @Override
    public Kegiatan findKegiatanById(Long id) {
        return kegiatanRepository.findById(id).orElseThrow(() -> new RuntimeException("Survey not found"));
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