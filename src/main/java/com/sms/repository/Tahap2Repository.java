package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap2;

@Repository
public interface Tahap2Repository extends JpaRepository<Tahap2, Long> {
    Optional<Tahap2> findByKegiatanId(Long kegiatanId);
}