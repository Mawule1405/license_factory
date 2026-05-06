package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.ProjectMiniStats;
import com.taurustechnology.backend.dtos.requests.ProjectRequest;
import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.LicenseModel;
import com.taurustechnology.backend.models.Parameter;
import com.taurustechnology.backend.models.Project;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.LicenseModelRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
import com.taurustechnology.backend.repositories.ProjectRepository;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.ProjectService;
import com.taurustechnology.backend.specifications.LicenseSpecifications;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;
    private final AuditService auditService;
    private final LicenseModelRepository licenseModelRepository;
    private final LicenseRepository licenseRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, AppUserRepository appUserRepository, AuditService auditService, LicenseModelRepository licenseModelRepository, LicenseRepository licenseRepository) {
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
        this.auditService = auditService;
        this.licenseModelRepository = licenseModelRepository;
        this.licenseRepository = licenseRepository;
    }

    @Override
    @Transactional
    public Project createProject(ProjectRequest projectRequest, String username) {
        log.info("[START] Attempting to create project '{}' by user: {}", projectRequest.getName(), username);

        AppUser appuser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("[AUTH_ERROR] Creator '{}' not found in database", username);
            return new EntityNotFoundException("Creator: " + username + " not found");
        });

        if (projectRepository.existsByNameIgnoreCase(projectRequest.getName())) {
            log.warn("[VALIDATION_ERROR] Project name '{}' already exists in registry", projectRequest.getName());
            throw new EntityExistsException("Project with name '" + projectRequest.getName() + "' already exists");
        }

        Project newProject = new Project();
        newProject.setName(projectRequest.getName());
        newProject.setDescription(projectRequest.getDescription());
        newProject.setCreator(appuser);
        newProject.setDeleted(false);
        newProject.setCreatedBy(appuser.getId());
        newProject.setCreatedAt(LocalDateTime.now());

        // 1. Sauvegarder d'abord le projet pour avoir l'ID
        final Project savedProject = projectRepository.save(newProject);

        if (projectRequest.getLicenseModel() != null) {
            log.debug("[CONFIG] Initializing security parameters for project: {}", savedProject.getName());

            LicenseModel licenseModel = new LicenseModel();
            licenseModel.setProject(savedProject);

            // 2. Mapper les paramètres depuis la requête (DTO -> Entity)
            if (projectRequest.getLicenseModel().getParameters() != null) {
                List<Parameter> parameters = projectRequest.getLicenseModel().getParameters().stream()
                        .map(paramRequest -> {
                            Parameter p = new Parameter();
                            p.setLabel(paramRequest.getLabel());
                            p.setType(paramRequest.getType());
                            // Important : Si Parameter a un lien vers LicenseModel, le définir ici
                            return p;
                        }).collect(Collectors.toList());

                // 3. Enregistrer les paramètres (dépend de votre cascade JPA)
                licenseModel.setParameters(parameters);
            }

            licenseModel = licenseModelRepository.save(licenseModel);
            savedProject.setLicenseModel(licenseModel);

            log.info("[CONFIG_SUCCESS] {} security parameters attached to project",
                    licenseModel.getParameters() != null ? licenseModel.getParameters().size() : 0);
        }

        auditService.logAction("CREATE_PROJECT", username, savedProject.getName(), "SUCCESS");
        return savedProject;
    }

    @Override
    @Transactional
    public void updateProject(String id, Project projectUpdates, String username) {
        log.info("[START] Updating project ID: {} by user: {}", id, username);

        AppUser appuser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("[AUTH_ERROR] Update denied: User '{}' not found", username);
            return new EntityNotFoundException("User: " + username + " not found");
        });

        Project existingProject = projectRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Update failed: Project ID {} does not exist", id);
            return new EntityNotFoundException("Project not found with id: " + id);
        });

        // Vérification de l'unicité du nom en cas de renommage
        if (!existingProject.getName().equalsIgnoreCase(projectUpdates.getName()) &&
                projectRepository.existsByNameIgnoreCase(projectUpdates.getName())) {
            log.warn("[CONFLICT] Cannot rename project to '{}': Name already taken", projectUpdates.getName());
            throw new EntityExistsException("Another project already uses the name: " + projectUpdates.getName());
        }

        try {
            existingProject.setName(projectUpdates.getName());
            existingProject.setDescription(projectUpdates.getDescription());
            existingProject.setUpdatedAt(LocalDateTime.now());
            existingProject.setUpdatedBy(appuser.getId());

            if (projectUpdates.getLicenseModel() != null) {
                log.debug("[SYNC] Synchronizing license model parameters for: {}", existingProject.getName());

                LicenseModel lm = existingProject.getLicenseModel();
                if (lm == null) {
                    lm = new LicenseModel();
                    lm.setProject(existingProject);
                    existingProject.setLicenseModel(lm);
                }

                // Gestion sécurisée de la collection de paramètres
                if (projectUpdates.getLicenseModel().getParameters() != null) {
                    // 1. On initialise ou on vide la liste existante
                    if (lm.getParameters() == null) {
                        lm.setParameters(new ArrayList<>());
                    } else {
                        lm.getParameters().clear();
                    }

                    // 2. CRUCIAL : On ajoute les nouveaux paramètres
                    for (Parameter paramUpdate : projectUpdates.getLicenseModel().getParameters()) {
                        // On crée une nouvelle instance ou on prépare l'instance reçue
                        Parameter newParam = new Parameter();
                        newParam.setLabel(paramUpdate.getLabel());
                        newParam.setType(paramUpdate.getType());

                        // 3. IMPORTANT : On lie le paramètre au LicenseModel (Lien bidirectionnel)
                        //newParam.setLicenseModel(lm);

                        // 4. On ajoute à la liste gérée par Hibernate
                        lm.getParameters().add(newParam);
                    }
                }

                licenseModelRepository.save(lm);
            }

            projectRepository.save(existingProject);
            auditService.logAction("UPDATE_PROJECT", username, existingProject.getName(), "SUCCESS");
            log.info("[COMPLETED] Project '{}' updated successfully", existingProject.getName());

        } catch (Exception e) {
            log.error("[CRITICAL] Failed to update project {}: {}", id, e.getMessage());
            auditService.logAction("UPDATE_PROJECT", username, id, "FAILED: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Project getProject(String id) {
        log.debug("[QUERY] Fetching details for Project ID: {}", id);
        return projectRepository.findById(id).orElseGet(() -> {
            log.warn("[QUERY_EMPTY] No project found for ID: {}", id);
            return null;
        });
    }

    @Override
    public Page<Project> getProjects(String key, int page, int size) {
        log.info("[QUERY] Fetching projects page {} (size: {}) with filter: '{}'", page, size, key);

        Sort sort = Sort.by(Sort.Direction.ASC, "name")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Project> results = projectRepository.findAllByNameContainingIgnoreCase(key, pageable);
        log.info("[QUERY_RESULT] Retrieved {} projects for filter: '{}'", results.getTotalElements(), key);
        return results;
    }

    @Override
    public void deleteProject(String id, String username) {
        log.info("[START] Request to terminate project ID: {} by: {}", id, username);

        // Utilisation de findByUsername pour la cohérence avec les autres méthodes
        AppUser appuser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("[AUTH_ERROR] Termination denied: User '{}' not found", username);
            return new EntityNotFoundException("Creator: " + username + " not found");
        });

        Project project = projectRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Termination failed: Project {} not found", id);
            return new EntityNotFoundException("Project: " + id + " not found");
        });

        project.setDeletedBy(appuser.getId());
        project.setDeletedAt(LocalDateTime.now());
        project.setDeleted(true);
        projectRepository.save(project);

        auditService.logAction("DELETE_PROJECT", username, project.getName(), "SUCCESS");
        log.info("[COMPLETED] Project '{}' successfully decommissioned (Soft Delete)", project.getName());
    }


    @Override
    public ProjectMiniStats getProjectStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);

        // Un "nouveau projet" est un projet dont le nom n'existait pas avant ce mois-ci
        List<String> existingBefore = licenseRepository.findProjectNamesExistingBefore(startOfMonth);
        long totalProjects = licenseRepository.countDistinctProjectNames();
        long newProjectsCount = totalProjects - existingBefore.size();

        return new ProjectMiniStats(
                totalProjects,
                newProjectsCount, // totalThisMonth
                licenseRepository.count(LicenseSpecifications.createdBetween(startOfMonth, now)), // newDeployments
                licenseRepository.findTopByOrderByCreatedAtDesc().map(l -> l.getClient().getName()).orElse("N/A"),
                licenseRepository.findMostLicensedProjectName(),
                licenseRepository.findTopCreatorName().orElse("N/A")
        );
    }
}