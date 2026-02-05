package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.DashboardStats;
import com.taurustechnology.backend.dtos.RecentActivity;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;

import com.taurustechnology.backend.services.impl.AuditLogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardStatsController {

    private final ClientRepository clientRepository;
    private final LicenseRepository licenseRepository;
    private final AppUserRepository userRepository;
    private final AuditLogParser auditLogParser;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {

        // 1. Comptage des données en base de données
        long totalClients = clientRepository.count();
        long totalLicenses = licenseRepository.count();
        long activeUsers = userRepository.count();

        // 2. Récupération des activités récentes depuis le fichier audit.log (pas de DB)
        // On récupère les 10 dernières actions
        List<RecentActivity> recentActivities = auditLogParser.getRecentActivities(10);

        // 3. Construction d'un graphique simple (Exemple: Licences par mois)
        // Tu pourras affiner cette requête JPA plus tard
        Map<String, Long> licensesByMonth = calculateLicenseGraph();

        return ResponseEntity.ok(new DashboardStats(
                totalClients,
                totalLicenses,
                activeUsers,
                recentActivities,
                licensesByMonth
        ));
    }

    /**
     * Simulation de données pour le graphique.
     * À remplacer par une requête repository groupée par date plus tard.
     */
    private Map<String, Long> calculateLicenseGraph() {
        Map<String, Long> mockData = new TreeMap<>();
        mockData.put("Jan", 5L);
        mockData.put("Feb", 12L);
        mockData.put("Mar", 8L);
        mockData.put("Apr", 15L);
        return mockData;
    }
}