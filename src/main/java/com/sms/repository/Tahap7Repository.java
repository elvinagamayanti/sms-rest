package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap7;

@Repository
public interface Tahap7Repository extends JpaRepository<Tahap7, Long> {
    Optional<Tahap7> findByKegiatanId(Long kegiatanId);
}