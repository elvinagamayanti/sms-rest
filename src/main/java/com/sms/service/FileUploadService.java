package com.sms.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sms.entity.TahapUploadedFile;
import com.sms.repository.TahapUploadedFileRepository;

@Service
public class FileUploadService {

    private final TahapUploadedFileRepository tahapUploadedFileRepository;

    public FileUploadService(TahapUploadedFileRepository tahapUploadedFileRepository) {
        this.tahapUploadedFileRepository = tahapUploadedFileRepository;
    }

    @Value("${file.upload.dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, Long kegiatanId, int tahapId) throws IOException {
        // Create directory if it doesn't exist
        String uploadPath = uploadDir + "/kegiatan/" + kegiatanId + "/tahap/" + tahapId;
        Files.createDirectories(Paths.get(uploadPath));

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFilename = System.currentTimeMillis() + "_" + originalFilename;

        // Save the file
        Path targetLocation = Paths.get(uploadPath).resolve(storedFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Save reference to database
        TahapUploadedFile fileEntity = new TahapUploadedFile();
        fileEntity.setKegiatanId(kegiatanId);
        fileEntity.setTahapId(tahapId);
        fileEntity.setOriginalFilename(originalFilename);
        fileEntity.setStoredFilename(storedFilename);
        fileEntity.setFilePath(uploadPath + "/" + storedFilename);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setUploadTimestamp(LocalDateTime.now());

        tahapUploadedFileRepository.save(fileEntity);

        return storedFilename;
    }

    public Resource loadFileAsResource(Long kegiatanId, int tahapId, String filename) {
        try {
            Path filePath = Paths.get(uploadDir + "/kegiatan/" + kegiatanId + "/tahap/" + tahapId)
                    .resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading file", e);
        }
    }

    public List<String> getUploadedFiles(Long kegiatanId, int tahapId) {
        return tahapUploadedFileRepository.findByKegiatanIdAndTahapId(kegiatanId, tahapId)
                .stream()
                .map(TahapUploadedFile::getOriginalFilename)
                .collect(Collectors.toList());
    }
}