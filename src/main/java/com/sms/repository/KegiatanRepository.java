/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Kegiatan;

/**
 *
 * @author pinaa
 */
@Repository
public interface KegiatanRepository extends JpaRepository<Kegiatan, Long> {
    Optional<Kegiatan> findByCode(String code);

    // @Query("SELECT k from Kegiatan k WHERE " +
    // " k.name LIKE CONCAT('%', :query, '%') OR " +
    // " k.id LIKE CONCAT('%', :query, '%')")
    // List<Kegiatan> searchKegiatan(String query);

}