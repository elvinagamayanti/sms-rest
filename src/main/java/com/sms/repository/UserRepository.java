/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entity.User;

/**
 *
 * @author pinaa
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Optional<User> findByName(String fullName);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findAllUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.satker.id = :satkerId")
    List<User> findAllUsersBySatkerId(@Param("satkerId") Long satkerId);

    // Method untuk mencari user berdasarkan status
    List<User> findByIsActive(Boolean isActive);

    // Method untuk mencari user aktif berdasarkan satker
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.satker.id = :satkerId")
    List<User> findActiveBySatkerId(@Param("satkerId") Long satkerId);

    // Method untuk mencari user berdasarkan status dan satker
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive AND u.satker.id = :satkerId")
    List<User> findByIsActiveAndSatkerId(@Param("isActive") Boolean isActive, @Param("satkerId") Long satkerId);

    // Method untuk menghitung jumlah user aktif
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();

    // Method untuk menghitung jumlah user non-aktif
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countInactiveUsers();

    // Method untuk mencari user aktif berdasarkan nama atau email
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findActiveUsersByKeyword(@Param("keyword") String keyword);

    // Method untuk mencari user aktif dengan role tertentu
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = true AND r.id = :roleId")
    List<User> findActiveUsersByRoleId(@Param("roleId") Long roleId);

    // Method untuk mencari user berdasarkan status dengan role tertentu
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = :isActive AND r.id = :roleId")
    List<User> findUsersByStatusAndRoleId(@Param("isActive") Boolean isActive, @Param("roleId") Long roleId);
}