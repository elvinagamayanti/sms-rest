/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sms.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.KegiatanDto;
import com.sms.entity.Kegiatan;
import com.sms.entity.Output;
import com.sms.entity.Program;
import com.sms.entity.Satker;
import com.sms.entity.User;
import com.sms.mapper.KegiatanMapper;
import com.sms.repository.KegiatanRepository;
import com.sms.repository.OutputRepository;
import com.sms.repository.ProgramRepository;
import com.sms.repository.SatkerRepository;
import com.sms.repository.UserRepository;
import com.sms.service.KegiatanService;

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

    public KegiatanServiceImpl(KegiatanRepository kegiatanRepository, UserRepository userRepository,
            SatkerRepository satkerRepository, ProgramRepository programRepository, OutputRepository outputRepository) {
        this.userRepository = userRepository;
        this.satkerRepository = satkerRepository;
        this.programRepository = programRepository;
        this.outputRepository = outputRepository;
        this.kegiatanRepository = kegiatanRepository;
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
}