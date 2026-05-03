package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    // On force l'exclusion des entités supprimées au cas où le filtre auto échoue
    @Query("SELECT p FROM Project p WHERE p.deleted IS false AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Project> findAllByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}