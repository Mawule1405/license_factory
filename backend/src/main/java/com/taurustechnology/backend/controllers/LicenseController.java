package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.LicenseRequest;
import com.taurustechnology.backend.dtos.LicenseDTO; // Supposé existant
import com.taurustechnology.backend.entities.License;

import com.taurustechnology.backend.mappers.LicenseMapper;
import com.taurustechnology.backend.services.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final LicenseMapper licenseMapper;

    // 1. Créer une licence
    @PostMapping("/{userId}")
    public ResponseEntity<LicenseDTO> createLicense(@PathVariable String userId, @RequestBody LicenseDTO request) {
        License license = licenseMapper.toEntity(request);
        License savedLicense = licenseService.save(userId, license);
        return ResponseEntity.ok(licenseMapper.toDto(savedLicense));
    }

    // 2. Récupérer toutes les licences (Pagination)
    @GetMapping("/{userId}")
    public ResponseEntity<Page<LicenseDTO>> getAllLicenses(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<License> licenses = licenseService.findAll(userId, page, size);
        return ResponseEntity.ok(licenses.map(licenseMapper::toDto));
    }

    // 3. Récupérer une licence par ID
    @GetMapping("/{userId}/{id}")
    public ResponseEntity<LicenseDTO> getLicense(@PathVariable String userId, @PathVariable String id) {
        License license = licenseService.findOne(userId, id);
        return ResponseEntity.ok(licenseMapper.toDto(license));
    }

    // 4. Mettre à jour une licence
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<LicenseDTO> updateLicense(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestBody LicenseDTO request) {
        License license = licenseMapper.toEntity(request);
        license.setId(id); // On s'assure que l'ID est le bon
        License updatedLicense = licenseService.update(userId, license);
        return ResponseEntity.ok(licenseMapper.toDto(updatedLicense));
    }

    // 5. Supprimer (Soft Delete) une licence
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable String userId, @PathVariable String id) {
        boolean deleted = licenseService.delete(userId, id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    // 6. Générer et Télécharger le fichier .lic
    @PostMapping("/{userId}/generate/{id}")
    public ResponseEntity<byte[]> downloadLicense(@PathVariable String userId, @PathVariable String id) {
        try {
            String licenseContent = licenseService.generateLicense(userId, id);
            License license = licenseService.findOne(userId, id);
            byte[] licenseBytes = licenseContent.getBytes(StandardCharsets.UTF_8);

            String clientName = license.getClient().getName().replaceAll("\\s+", "_");
            String fileName = "License_" + clientName + ".lic";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(licenseBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}