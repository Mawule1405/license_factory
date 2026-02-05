package com.taurustechnology.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/logs")
@Slf4j
public class LogController {

    // On récupère le dossier parent "./logs" défini dans ton logback.xml
    @Value("${logging.file.path:./logs}")
    private String logBaseDir;

    /**
     * Récupère les logs de manière dynamique
     * @param type 'technical' pour les logs tech, 'audit' pour les logs d'audit
     * @param lines nombre de lignes à remonter (défaut 100)
     */
    @GetMapping("/stream/{type}")
    //@PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<String>> getLogs(
            @PathVariable String type,
            @RequestParam(defaultValue = "100") Long lines) {

        System.out.println("============ 0 ==============");
        // Mapping dynamique vers tes fichiers logback
        String targetFile = type.equalsIgnoreCase("audit")
                ? logBaseDir + "/audit/audit.log"
                : logBaseDir + "/technical.log";

        Path path = Paths.get(targetFile);

        if (!Files.exists(path)) {

            log.error("Fichier de log introuvable : {}", targetFile);
            return ResponseEntity.notFound().build();
        }

        try (Stream<String> stream = Files.lines(path)) {
            List<String> logLines = stream.toList();

            long start = Math.max(0L, logLines.size() - lines);
            // On crée une NOUVELLE liste modifiable à partir de la subList
            List<String> result = new ArrayList<>(logLines.subList((int) start, logLines.size()));

            // Maintenant reverse() fonctionnera sans erreur
            Collections.reverse(result);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Erreur de lecture du fichier log : {}", targetFile, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Exportation sécurisée du fichier d'audit pour archivage externe
     */
    @GetMapping("/export/audit")
    //@PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<byte[]> exportAuditFile() {
        try {
            Path path = Paths.get(logBaseDir + "/audit/audit.log");
            byte[] data = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=taurus_audit_export.log")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}