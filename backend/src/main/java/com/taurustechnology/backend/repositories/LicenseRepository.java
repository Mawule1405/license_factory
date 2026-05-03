package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.License;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, String>, JpaSpecificationExecutor<License> {

    Page<License> findAllByClient_Id(String clientId, Pageable pageable);

    long countByCreator(AppUser user);

    long countByActive(boolean active);

    List<License> findTop5ByOrderByCreatedAtDesc();

    Optional<License> findTopByOrderByCreatedAtDesc();

    @Query("SELECT l.project.name FROM License l GROUP BY l.project.name ORDER BY COUNT(l) DESC LIMIT 1")
    String findMostLicensedProjectName();

    @Query("SELECT COUNT(DISTINCT l.project.name) FROM License l")
    long countDistinctProjectNames();

    @Query("SELECT COUNT(DISTINCT l.project.name) FROM License l WHERE l.active = true")
    long countProjectsWithActiveLicense();

    @Query("SELECT l.creator.username FROM License l GROUP BY l.creator.username ORDER BY COUNT(l) DESC LIMIT 1")
    Optional<String> findTopCreatorName();

    // Pour identifier les nouveaux projets (ceux dont la première licence est dans l'intervalle)
    @Query("SELECT DISTINCT l.project.name FROM License l WHERE l.createdAt < :start")
    List<String> findProjectNamesExistingBefore(@Param("start") LocalDateTime start);
}