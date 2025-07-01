package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.DirektoratDto;
import com.sms.entity.Deputi;
import com.sms.entity.Direktorat;
import com.sms.entity.User;
import com.sms.mapper.DirektoratMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.DirektoratRepository;
import com.sms.repository.UserRepository;
import com.sms.service.DirektoratService;

/**
 * Service implementation for Direktorat
 * 
 * @author pinaa
 */
@Service
public class DirektoratServiceImpl implements DirektoratService {
    private final DirektoratRepository direktoratRepository;
    private final DeputiRepository deputiRepository;
    private final UserRepository userRepository;

    public DirektoratServiceImpl(DirektoratRepository direktoratRepository,
            DeputiRepository deputiRepository,
            UserRepository userRepository) {
        this.direktoratRepository = direktoratRepository;
        this.deputiRepository = deputiRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<DirektoratDto> ambilDaftarDirektorat() {
        List<Direktorat> direktorats = this.direktoratRepository.findAll();
        return direktorats.stream()
                .map(direktorat -> DirektoratMapper.mapToDirektoratDto(direktorat))
                .collect(Collectors.toList());
    }

    @Override
    public void hapusDataDirektorat(Long direktoratId) {
        direktoratRepository.deleteById(direktoratId);
    }

    @Override
    public void perbaruiDataDirektorat(DirektoratDto direktoratDto) {
        Direktorat direktorat = DirektoratMapper.mapToDirektorat(direktoratDto);
        System.out.println(direktoratDto);
        direktoratRepository.save(direktorat);
    }

    @Override
    public void simpanDataDirektorat(DirektoratDto direktoratDto) {
        Direktorat direktorat = DirektoratMapper.mapToDirektorat(direktoratDto);

        // Set deputi relationship
        if (direktoratDto.getDeputi() != null && direktoratDto.getDeputi().getId() != null) {
            Deputi deputi = deputiRepository.findById(direktoratDto.getDeputi().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Deputi not found with id: " + direktoratDto.getDeputi().getId()));
            direktorat.setDeputi(deputi);
        }

        direktoratRepository.save(direktorat);
    }

    @Override
    public DirektoratDto cariDirektoratById(Long id) {
        Direktorat direktorat = direktoratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Direktorat not found with id: " + id));
        return DirektoratMapper.mapToDirektoratDto(direktorat);
    }

    @Override
    public DirektoratDto cariDirektoratByCode(String code) {
        Direktorat direktorat = direktoratRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Direktorat not found with code: " + code));
        return DirektoratMapper.mapToDirektoratDto(direktorat);
    }

    @Override
    public List<DirektoratDto> getDirektoratsByDeputiId(Long deputiId) {
        List<Direktorat> direktorats = direktoratRepository.findByDeputiId(deputiId);
        return direktorats.stream()
                .map(direktorat -> DirektoratMapper.mapToDirektoratDto(direktorat))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersByDirektoratId(Long direktoratId) {
        return userRepository.findAllUsersByDirektoratId(direktoratId);
    }

    @Override
    public DirektoratDto patchDirektorat(Long direktoratId, Map<String, Object> updates) {
        final Direktorat[] direktoratHolder = new Direktorat[1];
        direktoratHolder[0] = direktoratRepository.findById(direktoratId)
                .orElseThrow(() -> new RuntimeException("Direktorat not found with id: " + direktoratId));

        // Update only the fields that are provided
        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        direktoratHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        direktoratHolder[0].setCode((String) value);
                }
                case "deputi" -> {
                    if (value != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> deputiData = (Map<String, Object>) value;
                        Long deputiId = Long.valueOf(deputiData.get("id").toString());
                        Deputi deputi = deputiRepository.findById(deputiId)
                                .orElseThrow(() -> new RuntimeException("Deputi not found with id: " + deputiId));
                        direktoratHolder[0].setDeputi(deputi);
                    }
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        direktoratHolder[0] = direktoratRepository.save(direktoratHolder[0]);
        return DirektoratMapper.mapToDirektoratDto(direktoratHolder[0]);
    }
}