/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entity.Province;

/**
 *
 * @author pinaa
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Province findByName(String name);

    Optional<Province> findByCode(String code);
}