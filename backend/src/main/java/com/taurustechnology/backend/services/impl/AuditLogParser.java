package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.RecentActivity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuditLogParser {

    public List<RecentActivity> getRecentActivities(int limit) {
        String auditFilePath = "./logs/audit/audit.log";
        Path path = Paths.get(auditFilePath);
        if (!Files.exists(path)) return List.of();

        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                        // On prend les dernières lignes
                        int start = Math.max(0, list.size() - limit);
                        List<String> lastLines = list.subList(start, list.size());
                        Collections.reverse(lastLines); // Plus récent en premier
                        return lastLines;
                    }))
                    .stream()
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private RecentActivity parseLine(String line) {
        try {
            // On suppose que ton log est formaté ainsi dans logback.xml :
            // %d{yyyy-MM-dd HH:mm:ss} | %-5level | %msg%n
            String[] parts = line.split("\\|");

            String date = parts[0].trim();
            String level = parts[1].trim(); // INFO, WARN, ERROR
            String message = parts[2].trim();

            // Mapping du niveau vers le status visuel
            String status = switch (level) {
                case "INFO" -> "SUCCESS";
                case "WARN" -> "WARNING";
                case "ERROR" -> "DANGER";
                default -> "INFO";
            };

            // Extraction simple de l'action (le premier mot du message)
            String action = message.contains(" ") ? message.substring(0, message.indexOf(" ")) : message;

            return new RecentActivity(date, action, message, status);
        } catch (Exception e) {
            return null; // Ligne mal formatée ou vide
        }
    }
}