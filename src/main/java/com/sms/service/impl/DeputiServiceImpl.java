package com.sms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sms.dto.DeputiDto;
import com.sms.entity.Deputi;
import com.sms.entity.User;
import com.sms.mapper.DeputiMapper;
import com.sms.repository.DeputiRepository;
import com.sms.repository.UserRepository;
import com.sms.service.DeputiService;

/**
 * Service implementation for Deputi
 * 
 * @author pinaa
 */
@Service
public class DeputiServiceImpl implements DeputiService {
    private final DeputiRepository deputiRepository;
    private final UserRepository userRepository;

    public DeputiServiceImpl(DeputiRepository deputiRepository, UserRepository userRepository) {
        this.deputiRepository = deputiRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<DeputiDto> ambilDaftarDeputi() {
        List<Deputi> deputis = this.deputiRepository.findAll();
        return deputis.stream()
                .map(deputi -> DeputiMapper.mapToDeputiDto(deputi))
                .collect(Collectors.toList());
    }

    @Override
    public void hapusDataDeputi(Long deputiId) {
        deputiRepository.deleteById(deputiId);
    }

    @Override
    public void perbaruiDataDeputi(DeputiDto deputiDto) {
        Deputi deputi = DeputiMapper.mapToDeputi(deputiDto);
        System.out.println(deputiDto);
        deputiRepository.save(deputi);
    }

    @Override
    public void simpanDataDeputi(DeputiDto deputiDto) {
        Deputi deputi = DeputiMapper.mapToDeputi(deputiDto);
        deputiRepository.save(deputi);
    }

    @Override
    public DeputiDto cariDeputiById(Long id) {
        Deputi deputi = deputiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputi not found with id: " + id));
        return DeputiMapper.mapToDeputiDto(deputi);
    }

    @Override
    public DeputiDto cariDeputiByCode(String code) {
        Deputi deputi = deputiRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Deputi not found with code: " + code));
        return DeputiMapper.mapToDeputiDto(deputi);
    }

    @Override
    public List<User> getUsersByDeputiId(Long deputiId) {
        return userRepository.findAllUsersByDeputiId(deputiId);
    }

    @Override
    public DeputiDto patchDeputi(Long deputiId, Map<String, Object> updates) {
        final Deputi[] deputiHolder = new Deputi[1];
        deputiHolder[0] = deputiRepository.findById(deputiId)
                .orElseThrow(() -> new RuntimeException("Deputi not found with id: " + deputiId));

        // Update only the fields that are provided
        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> {
                    if (value != null)
                        deputiHolder[0].setName((String) value);
                }
                case "code" -> {
                    if (value != null)
                        deputiHolder[0].setCode((String) value);
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        });

        deputiHolder[0] = deputiRepository.save(deputiHolder[0]);
        return DeputiMapper.mapToDeputiDto(deputiHolder[0]);
    }
}