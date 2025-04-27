package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap6;

@Repository
public interface Tahap6Repository extends JpaRepository<Tahap6, Long> {
    Optional<Tahap6> findByKegiatanId(Long kegiatanId);
}