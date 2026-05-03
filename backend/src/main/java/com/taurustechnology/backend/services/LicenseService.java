package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.LicenseMiniStats;
import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.models.License;
import org.springframework.data.domain.Page;

public interface LicenseService {

    /**
     * Crée une nouvelle licence à partir du payload (clientId, projectId, metadata)
     */
    License createLicense(LicenseRequest request, String username);

    /**
     * Récupère la liste paginée des licences liées à l'utilisateur
     */
    Page<License> findAll(String username, int page, int size);
    Page<License> findClientLicenses(String name, String id, int page, int size);
    /**
     * Récupère une licence spécifique par son ID (String)
     */
    License findOne(String username, String id);

    /**
     * Met à jour une licence existante
     */
    License update(String username, String id, LicenseRequest request);

    /**
     * Supprime une licence du registre
     */
    void delete(String username, String id);

    /**
     * Génère le contenu du fichier .lic
     */
    String generateLicense(String username, String id,String raison);


    LicenseMiniStats getMiniStats();
}