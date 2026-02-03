package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.entities.License;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // Remplace le constructeur manuel pour plus de clarté
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final ClientRepository clientRepository;
    private final AppUserRepository appUserRepository;
    private final AuditService auditService;
    private final LicenseGeneratorService licenseGeneratorService;

    @Override
    @Transactional
    public License save(String userId, License license) {
        log.debug("Initiating license creation for client: {}", license.getClient().getId());

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validation : s'assurer que le client existe
        if (license.getClient() == null || !clientRepository.existsById(license.getClient().getId())) {
            throw new EntityNotFoundException("Client associated with license not found");
        }

        // Logique par défaut
        license.setCreator(user);
        license.setCreatedAt(LocalDateTime.now());
        license.setActivated(true);
        license.setDeleted(false);

        // Génération d'une clé unique si absente
        if (license.getLicenseKey() == null) {
            license.setLicenseKey(UUID.randomUUID().toString().toUpperCase());
        }

        try {
            License savedLicense = licenseRepository.save(license);
            log.info("License created successfully: {}", savedLicense.getLicenseKey());
            auditService.logAction("CREATE_LICENSE", userId, "License:" + savedLicense.getLicenseKey(), "SUCCESS");
            return savedLicense;
        } catch (Exception e) {
            log.error("Error creating license", e);
            auditService.logAction("CREATE_LICENSE", userId, "Client:" + license.getClient().getId(), "FAILED");
            throw e;
        }
    }

    @Override
    public Page<License> findAll(String userId, int page, int size) {
        log.debug("Fetching licenses page {} for user {}", page, userId);
        return licenseRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Override
    public License findOne(String userId, String id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found with id: " + id));
    }

    @Override
    @Transactional
    public License update(String userId, License license) {
        log.info("Updating license: {}", license.getId());

        License existingLicense = licenseRepository.findById(license.getId())
                .orElseThrow(() -> new EntityNotFoundException("License not found"));

        // Mise à jour sélective (on ne change pas le créateur ou la date de création)
        existingLicense.setAddressMac(license.getAddressMac());
        existingLicense.setLicenseLevel(license.getLicenseLevel());
        existingLicense.setMaxUsers(license.getMaxUsers());
        existingLicense.setExpiryDate(license.getExpiryDate());
        existingLicense.setActivated(license.isActivated());

        try {
            License updated = licenseRepository.save(existingLicense);
            auditService.logAction("UPDATE_LICENSE", userId, updated.getLicenseKey(), "SUCCESS");
            return updated;
        } catch (Exception e) {
            log.error("Update license failed", e);
            auditService.logAction("UPDATE_LICENSE", userId, license.getId(), "FAILED");
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean delete(String userId, String id) {
        log.warn("Soft deleting license: {}", id);

        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found"));

        try {
            // Option 1 : Soft delete (recommandé pour l'audit)
            license.setDeleted(true);
            license.setActivated(false);
            licenseRepository.save(license);

            // Option 2 : Hard delete (si vous préférez supprimer vraiment)
            // licenseRepository.delete(license);

            auditService.logAction("DELETE_LICENSE", userId, license.getLicenseKey(), "SUCCESS");
            return true;
        } catch (Exception e) {
            log.error("Deletion failed for license {}", id, e);
            auditService.logAction("DELETE_LICENSE", userId, id, "FAILED");
            return false;
        }
    }


    @Override
    public String generateLicense(String userId, String id) {
        log.info("Generating signed license string for ID: {} by user: {}", id, userId);

        // 1. Vérifier que l'utilisateur qui fait la demande existe
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. Récupérer la licence en base de données
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("License not found with id: " + id));

        // 3. (Optionnel mais recommandé) Vérifier si la licence n'est pas supprimée ou expirée
        if (license.isDeleted() || license.getExpiryDate().isBefore(LocalDate.now())
        || !license.isActivated()) {
            throw new IllegalStateException("Cannot generate a string for a deleted license.");
        }

        try {
            // 4. Appeler le service de génération (RSA Signature)
            String signedLicense = licenseGeneratorService.buildLicense(license);

            // 5. Audit de la génération (Tracé de qui a généré le fichier/clé)
            auditService.logAction("GENERATE_SIGNED_KEY", userId, license.getLicenseKey(), "SUCCESS");

            return signedLicense;
        } catch (Exception e) {
            log.error("Failed to sign license for ID: {}", id, e);
            auditService.logAction("GENERATE_SIGNED_KEY", userId, id, "FAILED");
            throw new RuntimeException("Error during digital signature process", e);
        }
    }
}