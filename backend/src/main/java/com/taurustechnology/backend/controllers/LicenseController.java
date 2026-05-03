package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.responses.LicenseResponse;
import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.dtos.responses.Pagination;
import com.taurustechnology.backend.models.License;
import com.taurustechnology.backend.mappers.LicenseMapper;
import com.taurustechnology.backend.services.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final LicenseMapper licenseMapper;

    /**
     * 1. Provisioning : Créer une licence
     */
    @PostMapping
    public ResponseEntity<LicenseResponse> create(@RequestBody LicenseRequest request, Principal principal) {
        String username = principal.getName();
        System.out.println(username+"   Création de license");
        License savedLicense = licenseService.createLicense(request, username);
        System.out.println(username+"   Création de license 2");
        return ResponseEntity.ok(licenseMapper.toResponse(savedLicense));
    }

    /**
     * 2. Exploration : Liste paginée des licences
     */
    @GetMapping
    public ResponseEntity<Pagination<LicenseResponse>> getAllLicenses(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<License> licenses = licenseService.findAll(principal.getName(), page, size);
        return ResponseEntity.ok(Pagination.of(licenses.map(licenseMapper::toResponse)));
    }

    /**
     * 2.1. Exploration : Liste paginée des licences
     */
    @GetMapping("/clients")
    public ResponseEntity<Pagination<LicenseResponse>> getClientLicenses(
            Principal principal,
            @RequestParam String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<License> licenses = licenseService.findClientLicenses(principal.getName(),id, page, size);
        return ResponseEntity.ok(Pagination.of(licenses.map(licenseMapper::toResponse)));
    }

    /**
     * 3. Inspection : Détail d'une licence spécifique
     */
    @GetMapping("/{id}")
    public ResponseEntity<LicenseResponse> getLicense(@PathVariable String id, Principal principal) {
        License license = licenseService.findOne(principal.getName(), id);
        return ResponseEntity.ok(licenseMapper.toResponse(license));
    }

    /**
     * 4. Mutation : Mise à jour des données de licence
     */
    @PutMapping("/{id}")
    public ResponseEntity<LicenseResponse> updateLicense(
            @PathVariable String id,
            @RequestBody LicenseRequest request, // On peut réutiliser Request ou créer un UpdateRequest
            Principal principal) {

        License updatedLicense = licenseService.update(principal.getName(), id, request);
        return ResponseEntity.ok(licenseMapper.toResponse(updatedLicense));
    }

    /**
     * 5. Suppression : Retrait d'une licence du registre
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable String id, Principal principal) {
        licenseService.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 6. Artefact : Génération et téléchargement du fichier .lic
     */
    @PostMapping("/generate/{id}")
    public ResponseEntity<byte[]> downloadLicense(@PathVariable String id, @RequestBody String raison, Principal principal) {
        try {
            String username = principal.getName();
            String licenseContent = licenseService.generateLicense(username, id,raison);
            License license = licenseService.findOne(username, id);

            byte[] licenseBytes = licenseContent.getBytes(StandardCharsets.UTF_8);

            String clientName = license.getClient().getName().replaceAll("\\s+", "_");
            String fileName = "License_" + clientName + "_"+license.getProject().getName()+".lic";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(licenseBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}