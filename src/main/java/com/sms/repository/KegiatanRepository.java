package com.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entity.Kegiatan;

@Repository
public interface KegiatanRepository extends JpaRepository<Kegiatan, Long> {
    Optional<Kegiatan> findByCode(String code);

    // Query berdasarkan direktorat penanggung jawab
    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab.id = :direktoratId")
    List<Kegiatan> findByDirektoratPenanggungJawabId(@Param("direktoratId") Long direktoratId);

    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab.code = :direktoratCode")
    List<Kegiatan> findByDirektoratPenanggungJawabCode(@Param("direktoratCode") String direktoratCode);

    // Query berdasarkan deputi (melalui direktorat)
    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab.deputi.id = :deputiId")
    List<Kegiatan> findByDeputiPenanggungJawabId(@Param("deputiId") Long deputiId);

    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab.deputi.code = :deputiCode")
    List<Kegiatan> findByDeputiPenanggungJawabCode(@Param("deputiCode") String deputiCode);

    // Query berdasarkan tahun dan direktorat
    @Query("SELECT k FROM Kegiatan k WHERE YEAR(k.startDate) = :year AND k.direktoratPenanggungJawab.id = :direktoratId")
    List<Kegiatan> findByYearAndDirektoratPenanggungJawabId(@Param("year") int year,
            @Param("direktoratId") Long direktoratId);

    // Query statistik per direktorat
    @Query("SELECT k.direktoratPenanggungJawab.name, COUNT(k) FROM Kegiatan k GROUP BY k.direktoratPenanggungJawab.id, k.direktoratPenanggungJawab.name")
    List<Object[]> countKegiatanByDirektorat();

    // Query statistik per deputi
    @Query("SELECT k.direktoratPenanggungJawab.deputi.name, COUNT(k) FROM Kegiatan k GROUP BY k.direktoratPenanggungJawab.deputi.id, k.direktoratPenanggungJawab.deputi.name")
    List<Object[]> countKegiatanByDeputi();

    // Query kegiatan yang belum ada direktorat PJ
    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab IS NULL")
    List<Kegiatan> findKegiatanWithoutDirektoratPJ();

    // Query berdasarkan user dan tahun
    @Query("SELECT k FROM Kegiatan k WHERE k.user.id = :userId AND YEAR(k.startDate) = :year")
    List<Kegiatan> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

    // Query dengan filter kompleks
    @Query("SELECT k FROM Kegiatan k WHERE " +
            "(:direktoratId IS NULL OR k.direktoratPenanggungJawab.id = :direktoratId) AND " +
            "(:year IS NULL OR YEAR(k.startDate) = :year) AND " +
            "(:programId IS NULL OR k.program.id = :programId)")
    List<Kegiatan> findWithFilters(
            @Param("direktoratId") Long direktoratId,
            @Param("year") Integer year,
            @Param("programId") Long programId);

    // Query untuk dashboard - statistik per bulan
    @Query("SELECT MONTH(k.startDate), COUNT(k) FROM Kegiatan k " +
            "WHERE YEAR(k.startDate) = :year AND k.direktoratPenanggungJawab.id = :direktoratId " +
            "GROUP BY MONTH(k.startDate) ORDER BY MONTH(k.startDate)")
    List<Object[]> getMonthlyStatistics(@Param("year") int year, @Param("direktoratId") Long direktoratId);

    // Query untuk total anggaran per direktorat
    @Query("SELECT k.direktoratPenanggungJawab.name, SUM(k.budget) FROM Kegiatan k " +
            "WHERE k.budget IS NOT NULL " +
            "GROUP BY k.direktoratPenanggungJawab.id, k.direktoratPenanggungJawab.name")
    List<Object[]> getTotalBudgetByDirektorat();

    // Query untuk total anggaran per deputi
    @Query("SELECT k.direktoratPenanggungJawab.deputi.name, SUM(k.budget) FROM Kegiatan k " +
            "WHERE k.budget IS NOT NULL " +
            "GROUP BY k.direktoratPenanggungJawab.deputi.id, k.direktoratPenanggungJawab.deputi.name")
    List<Object[]> getTotalBudgetByDeputi();

    // Query search kegiatan dengan nama atau kode
    @Query("SELECT k FROM Kegiatan k WHERE " +
            "k.name LIKE CONCAT('%', :query, '%') OR " +
            "k.code LIKE CONCAT('%', :query, '%')")
    List<Kegiatan> searchKegiatan(@Param("query") String query);

    // Query kegiatan berdasarkan status tahapan (untuk integrasi dengan sistem
    // tahapan)
    @Query("SELECT k FROM Kegiatan k WHERE k.direktoratPenanggungJawab.id = :direktoratId " +
            "ORDER BY k.startDate DESC")
    List<Kegiatan> findRecentByDirektoratPenanggungJawab(@Param("direktoratId") Long direktoratId);
}