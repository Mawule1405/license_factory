package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.LicenseMiniStats;
import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.models.*;
import com.taurustechnology.backend.repositories.*;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.LicenseGeneratorService;
import com.taurustechnology.backend.services.LicenseService;
import com.taurustechnology.backend.specifications.LicenseSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final ClientRepository clientRepository;
    private final AppUserRepository appUserRepository;
    private final ProjectRepository projectRepository;
    private final AuditService auditService;
    private final LicenseGeneratorService licenseGeneratorService;
    private final ExportRepository exportRepository;

    @Override
    @Transactional
    public License createLicense(LicenseRequest request, String username) {
        log.debug("Creating new license for client {} and project {}", request.getClientId(), request.getProjectId());

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        AppUser creator = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        License license = License.builder()
                .activationCode(UUID.randomUUID().toString().toUpperCase())
                .active(true)
                .client(client)
                .project(project)
                .creator(creator)
                .parameters(new ArrayList<>())
                .build();


        try {
            License saved = licenseRepository.save(license);
            auditService.logAction("CREATE_LICENSE", username, "License ID: " + saved.getId(), "SUCCESS");
            return saved;
        } catch (Exception e) {
            auditService.logAction("CREATE_LICENSE", username, "Client: " + request.getClientId(), "FAILED");
            throw e;
        }
    }

    @Override
    public Page<License> findAll(String username, int page, int size) {
        return licenseRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Override
    public Page<License> findClientLicenses(String username,String id, int page, int size) {
        return licenseRepository.findAllByClient_Id(id,PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Override
    public License findOne(String username, String id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found with id: " + id));
    }

    @Override
    @Transactional
    public License update(String username, String id, LicenseRequest request) {
        log.info("Updating license: {}", id);

        License existingLicense = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found"));


        // Mise à jour des paramètres dynamiques (Metadata)
        existingLicense.getParameters().clear();


        try {
            License updated = licenseRepository.save(existingLicense);
            auditService.logAction("UPDATE_LICENSE", username, "License ID: " + id, "SUCCESS");
            return updated;
        } catch (Exception e) {
            auditService.logAction("UPDATE_LICENSE", username, id, "FAILED");
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(String username, String id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found"));

        try {
            licenseRepository.delete(license);
            auditService.logAction("DELETE_LICENSE", username, "ID: " + id, "SUCCESS");
        } catch (Exception e) {
            auditService.logAction("DELETE_LICENSE", username, id, "FAILED");
            throw e;
        }
    }


    @Override
    public String generateLicense(String username, String id, String raison) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found"));

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {

            String signedLicense = licenseGeneratorService.buildLicense(license);
            Export export = new Export();
            export.setRegister(appUser);
            export.setLicense(license);
            export.setCreatedBy(username);
            export.setDetails(raison);
            export = exportRepository.save(export);

            auditService.logAction("GENERATE_SIGNED_KEY", username, "ID: " + id+"---EX: "+export.getId(), "SUCCESS");
            return signedLicense;
        } catch (Exception e) {
            auditService.logAction("GENERATE_SIGNED_KEY", username, id, "FAILED");
            throw new RuntimeException("Error during digital signature process", e);
        }
    }

    @Override
    public LicenseMiniStats getMiniStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);

        LocalDateTime startOfLastMonth = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime endOfLastMonth = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // 1. Volumes avec Specifications
        long total = licenseRepository.count();
        long activeTotal = licenseRepository.countByActive(true);

        long thisMonth = licenseRepository.count(LicenseSpecifications.createdBetween(startOfMonth, now));
        long lastMonth = licenseRepository.count(LicenseSpecifications.createdBetween(startOfLastMonth, endOfLastMonth));

        // 2. Croissance & Performance
        double growthRate = calculateGrowthRate(thisMonth, lastMonth);

        long totalProjects = licenseRepository.countDistinctProjectNames();
        long projectsWithActive = licenseRepository.countProjectsWithActiveLicense();
        double efficiency = totalProjects > 0 ? (double) projectsWithActive / totalProjects * 100 : 0;

        // 3. Records & Staff
        String lastClient = licenseRepository.findTopByOrderByCreatedAtDesc()
                .map(l -> l.getClient().getName()).orElse("N/A");
        String topProject = licenseRepository.findMostLicensedProjectName();
        String leadArchitect = licenseRepository.findTopCreatorName().orElse("N/A");

        // 4. Densité
        double density = totalProjects > 0 ? (double) total / totalProjects : 0;

        return new LicenseMiniStats(total, activeTotal, growthRate, efficiency, lastClient, topProject, leadArchitect, density);
    }


    private double calculateGrowthRate(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100;
    }



}