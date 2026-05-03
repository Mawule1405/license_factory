package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.models.*;
import com.taurustechnology.backend.repositories.*;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.LicenseGeneratorService;
import com.taurustechnology.backend.services.LicenseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        if (request.getParameters() != null) {
            mapMetadataToParameters(request, license);
        }

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

        // Mise à jour des relations si nécessaire
        if (!existingLicense.getClient().getId().equals(request.getClientId())) {
            Client newClient = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new EntityNotFoundException("New client not found"));
            existingLicense.setClient(newClient);
        }

        // Mise à jour des paramètres dynamiques (Metadata)
        existingLicense.getParameters().clear();
        if (request.getParameters() != null) {
            mapMetadataToParameters(request, existingLicense);
        }

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
            exportRepository.save(export);
            auditService.logAction("GENERATE_SIGNED_KEY", username, "ID: " + id, "SUCCESS");
            return signedLicense;
        } catch (Exception e) {
            auditService.logAction("GENERATE_SIGNED_KEY", username, id, "FAILED");
            throw new RuntimeException("Error during digital signature process", e);
        }
    }

    /**
     * Helper pour mapper la Map de métadonnées vers les entités LicenseParameter
     */
    private void mapMetadataToParameters(LicenseRequest request, License license) {
        List<LicenseParameter> params = request.getParameters().entrySet().stream()
                .map(entry -> LicenseParameter.builder()
                        .label(entry.getKey())
                        .value(entry.getValue())
                        .license(license)
                        .build())
                .toList();
        license.getParameters().addAll(params);
    }
}