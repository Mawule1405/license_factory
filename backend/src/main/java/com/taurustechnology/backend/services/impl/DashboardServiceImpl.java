package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.GlobalActivityMix;
import com.taurustechnology.backend.dtos.GrowthMetrics;
import com.taurustechnology.backend.dtos.RecentActivity;
import com.taurustechnology.backend.dtos.UserActivityMetrics;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.ExportRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
import com.taurustechnology.backend.services.DashboardService;
import com.taurustechnology.backend.specifications.LicenseSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LicenseRepository licenseRepository;
    private final AppUserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ExportRepository exportRepository;
    private final AuditLogParser auditLogParser;

    @Override
    public List<UserActivityMetrics> getUserActivityBreakdown() {
        return userRepository.findAll().stream().map(user -> {
            return new UserActivityMetrics(
                    user.getUsername(),
                    // Comptage basé sur l'utilisateur créateur dans chaque table
                    licenseRepository.countByCreator(user),
                    clientRepository.countByRegister(user),
                    exportRepository.countByRegister(user), // Si l'export n'est pas persisté en base, on laisse à 0 ou on gère via un champ dédié
                    userRepository.countByCreatedBy(user.getUsername()) // Utilisateurs créés par cet admin
            );
        }).toList();
    }

    @Override
    public GrowthMetrics getMonthlyGrowth() {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        // On remonte sur les 6 derniers mois à partir d'aujourd'hui
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);

            // Label formaté (ex: "May 2026")
            labels.add(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH));

            // Calcul des bornes du mois
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

            // Comptage via Specification
            long count = licenseRepository.count(LicenseSpecifications.createdBetween(start, end));
            values.add(count);
        }

        return new GrowthMetrics(labels, values);
    }

    @Override
    public GlobalActivityMix getGlobalMix() {
        return new GlobalActivityMix(
                licenseRepository.count(),
                clientRepository.count(),
                exportRepository.count(), // Exemple
                userRepository.count()
        );
    }

    @Override
    public List<RecentActivity> getLatestActivities() {
        // On peut simuler l'audit trail en récupérant les dernières licences créées
        return auditLogParser.getRecentActivities(20);
    }
}