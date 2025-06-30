package com.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entity.User;

/**
 * Updated UserRepository with Direktorat and Deputi queries
 * 
 * @author pinaa
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Optional<User> findByName(String fullName);

    boolean existsByEmail(String email);

    // Existing queries for roles and satker
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findAllUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.satker.id = :satkerId")
    List<User> findAllUsersBySatkerId(@Param("satkerId") Long satkerId);

    // Find users by direktorat
    @Query("SELECT u FROM User u WHERE u.direktorat.id = :direktoratId")
    List<User> findAllUsersByDirektoratId(@Param("direktoratId") Long direktoratId);

    // Find users by deputi (through direktorat)
    @Query("SELECT u FROM User u WHERE u.direktorat.deputi.id = :deputiId")
    List<User> findAllUsersByDeputiId(@Param("deputiId") Long deputiId);

    // Find users by direktorat code
    @Query("SELECT u FROM User u WHERE u.direktorat.code = :direktoratCode")
    List<User> findAllUsersByDirektoratCode(@Param("direktoratCode") String direktoratCode);

    // Find users by deputi code
    @Query("SELECT u FROM User u WHERE u.direktorat.deputi.code = :deputiCode")
    List<User> findAllUsersByDeputiCode(@Param("deputiCode") String deputiCode);

    // Status-based queries
    List<User> findByIsActive(Boolean isActive);

    // Active users by direktorat
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.direktorat.id = :direktoratId")
    List<User> findActiveByDirektoratId(@Param("direktoratId") Long direktoratId);

    // Active users by deputi
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.direktorat.deputi.id = :deputiId")
    List<User> findActiveByDeputiId(@Param("deputiId") Long deputiId);

    // Active users by satker
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.satker.id = :satkerId")
    List<User> findActiveBySatkerId(@Param("satkerId") Long satkerId);

    // Users by status and direktorat
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive AND u.direktorat.id = :direktoratId")
    List<User> findByIsActiveAndDirektoratId(@Param("isActive") Boolean isActive,
            @Param("direktoratId") Long direktoratId);

    // Users by status and deputi
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive AND u.direktorat.deputi.id = :deputiId")
    List<User> findByIsActiveAndDeputiId(@Param("isActive") Boolean isActive, @Param("deputiId") Long deputiId);

    // Users by status and satker
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive AND u.satker.id = :satkerId")
    List<User> findByIsActiveAndSatkerId(@Param("isActive") Boolean isActive, @Param("satkerId") Long satkerId);

    // Count queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countInactiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.direktorat.id = :direktoratId")
    Long countUsersByDirektoratId(@Param("direktoratId") Long direktoratId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.direktorat.deputi.id = :deputiId")
    Long countUsersByDeputiId(@Param("deputiId") Long deputiId);

    // Search queries
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findActiveUsersByKeyword(@Param("keyword") String keyword);

    // Active users with specific role and direktorat
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = true AND r.id = :roleId AND u.direktorat.id = :direktoratId")
    List<User> findActiveUsersByRoleIdAndDirektoratId(@Param("roleId") Long roleId,
            @Param("direktoratId") Long direktoratId);

    // Active users with specific role and deputi
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = true AND r.id = :roleId AND u.direktorat.deputi.id = :deputiId")
    List<User> findActiveUsersByRoleIdAndDeputiId(@Param("roleId") Long roleId, @Param("deputiId") Long deputiId);

    // Users by role and direktorat
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.direktorat.id = :direktoratId")
    List<User> findUsersByRoleIdAndDirektoratId(@Param("roleId") Long roleId, @Param("direktoratId") Long direktoratId);

    // Users by role and deputi
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.direktorat.deputi.id = :deputiId")
    List<User> findUsersByRoleIdAndDeputiId(@Param("roleId") Long roleId, @Param("deputiId") Long deputiId);

    // Existing queries
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = true AND r.id = :roleId")
    List<User> findActiveUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.isActive = :isActive AND r.id = :roleId")
    List<User> findUsersByStatusAndRoleId(@Param("isActive") Boolean isActive, @Param("roleId") Long roleId);
}