package com.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entity.Direktorat;

/**
 * Repository for Direktorat
 * 
 * @author pinaa
 */
@Repository
public interface DirektoratRepository extends JpaRepository<Direktorat, Long> {

    Optional<Direktorat> findByCode(String code);

    Direktorat findByName(String name);

    boolean existsByCode(String code);

    List<Direktorat> findByDeputiId(Long deputiId);

    @Query("SELECT d FROM Direktorat d WHERE d.deputi.code = :deputiCode")
    List<Direktorat> findByDeputiCode(@Param("deputiCode") String deputiCode);

    @Query("SELECT d FROM Direktorat d WHERE " +
            "d.code LIKE CONCAT('%', :query, '%') OR " +
            "d.name LIKE CONCAT('%', :query, '%')")
    List<Direktorat> searchDirektorat(@Param("query") String query);
}