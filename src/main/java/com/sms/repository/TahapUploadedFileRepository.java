package com.sms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.TahapUploadedFile;

@Repository
public interface TahapUploadedFileRepository extends JpaRepository<TahapUploadedFile, Long> {

    List<TahapUploadedFile> findByKegiatanId(Long kegiatanId);

    List<TahapUploadedFile> findByKegiatanIdAndTahapId(Long kegiatanId, int tahapId);

    TahapUploadedFile findByKegiatanIdAndTahapIdAndStoredFilename(Long kegiatanId, int tahapId, String storedFilename);

    void deleteByKegiatanId(Long kegiatanId);

    void deleteByKegiatanIdAndTahapId(Long kegiatanId, int tahapId);
}