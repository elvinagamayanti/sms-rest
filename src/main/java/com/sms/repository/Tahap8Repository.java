package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Tahap8;

@Repository
public interface Tahap8Repository extends JpaRepository<Tahap8, Long> {
    Optional<Tahap8> findByKegiatanId(Long kegiatanId);
}