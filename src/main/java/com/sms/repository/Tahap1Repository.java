package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap1;

@Repository
public interface Tahap1Repository extends JpaRepository<Tahap1, Long> {
    Optional<Tahap1> findByKegiatanId(Long kegiatanId);
}