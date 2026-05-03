package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.responses.ExportResponse;
import com.taurustechnology.backend.mappers.ExportMapper;
import com.taurustechnology.backend.models.Export;
import com.taurustechnology.backend.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final ExportMapper exportMapper;

    /**
     * Récupère tous les journaux d'exportation avec pagination.
     */
    @GetMapping
    public ResponseEntity<Page<ExportResponse>> getAllExports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Export> exports = exportService.findAll(page, size);

        // Conversion de la page d'entités en page de DTOs
        Page<ExportResponse> response = exports.map(exportMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère l'historique des exports pour une licence spécifique.
     */
    @GetMapping("/license/{licenseId}")
    public ResponseEntity<Page<ExportResponse>> getExportsByLicense(
            @PathVariable String licenseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Export> exports = exportService.findAllByLicense(licenseId, page, size);

        // Conversion via le mapper injecté
        Page<ExportResponse> response = exports.map(exportMapper::toResponse);

        return ResponseEntity.ok(response);
    }
}