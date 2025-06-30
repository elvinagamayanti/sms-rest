package com.sms.service.impl;

import java.util.List;
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
}