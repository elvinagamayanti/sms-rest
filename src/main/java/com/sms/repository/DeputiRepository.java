package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Deputi;

/**
 * Repository for Deputi
 * 
 * @author pinaa
 */
@Repository
public interface DeputiRepository extends JpaRepository<Deputi, Long> {

    Optional<Deputi> findByCode(String code);

    Deputi findByName(String name);

    boolean existsByCode(String code);
}