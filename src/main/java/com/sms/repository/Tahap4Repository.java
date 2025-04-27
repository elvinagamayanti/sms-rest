package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap4;

@Repository
public interface Tahap4Repository extends JpaRepository<Tahap4, Long> {
    Optional<Tahap4> findByKegiatanId(Long kegiatanId);
}