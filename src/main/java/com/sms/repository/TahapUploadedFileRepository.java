package com.sms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.TahapUploadedFile;

@Repository
public interface TahapUploadedFileRepository extends JpaRepository<TahapUploadedFile, Long> {

    // Find all files uploaded for a specific kegiatan
    List<TahapUploadedFile> findByKegiatanId(Long kegiatanId);

    // Find all files uploaded for a specific tahap of a kegiatan
    List<TahapUploadedFile> findByKegiatanIdAndTahapId(Long kegiatanId, int tahapId);

    // Find a specific file by its stored filename for a kegiatan and tahap
    TahapUploadedFile findByKegiatanIdAndTahapIdAndStoredFilename(Long kegiatanId, int tahapId, String storedFilename);

    // Delete all files for a specific kegiatan
    void deleteByKegiatanId(Long kegiatanId);

    // Delete all files for a specific tahap of a kegiatan
    void deleteByKegiatanIdAndTahapId(Long kegiatanId, int tahapId);
}