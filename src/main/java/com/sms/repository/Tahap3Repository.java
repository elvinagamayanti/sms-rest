package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap3;

@Repository
public interface Tahap3Repository extends JpaRepository<Tahap3, Long> {
    Optional<Tahap3> findByKegiatanId(Long kegiatanId);
}