package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap5;

@Repository
public interface Tahap5Repository extends JpaRepository<Tahap5, Long> {
    Optional<Tahap5> findByKegiatanId(Long kegiatanId);
}