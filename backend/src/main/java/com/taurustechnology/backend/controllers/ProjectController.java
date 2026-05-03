package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.requests.ProjectRequest;
import com.taurustechnology.backend.dtos.responses.Pagination;
import com.taurustechnology.backend.dtos.responses.ProjectResponse;
import com.taurustechnology.backend.mappers.ProjectMapper;
import com.taurustechnology.backend.models.Project;
import com.taurustechnology.backend.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    /**
     * Crée un nouveau projet.
     * Accessible uniquement par les administrateurs.
     */
    @PostMapping("/create")
    //@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest, Principal principal) {
        log.info("REST request to create project: {}", projectRequest.getName());
        Project createdProject = projectService.createProject(projectRequest, principal.getName());
        return new ResponseEntity<>(projectMapper.toDto(createdProject), HttpStatus.CREATED);
    }

    /**
     * Récupère la liste paginée des projets avec recherche par mot-clé.
     */
    @GetMapping("/search")
    public ResponseEntity<Pagination<ProjectResponse>> getProjects(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        log.debug("REST request to get projects page {} with size {}", page, size);
        Page<Project> projects = projectService.getProjects(keyword, page, size);

        return ResponseEntity.ok(Pagination.of(projects.map(projectMapper::toDto)));
    }

    /**
     * Récupère un projet par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String id) {
        log.debug("REST request to get project: {}", id);
        Project project = projectService.getProject(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projectMapper.toDto(project));
    }

    /**
     * Met à jour un projet existant.
     */
    @PutMapping("/update/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> updateProject(
            @PathVariable String id,
            @RequestBody Project projectUpdates,
            Principal principal) {

        log.info("REST request to update project ID: {}", id);
        projectService.updateProject(id, projectUpdates, principal.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Suppression logique d'un projet.
     */
    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> deleteProject(@PathVariable String id, Principal principal) {
        log.warn("REST request to delete project ID: {} by user: {}", id, principal.getName());
        projectService.deleteProject(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}