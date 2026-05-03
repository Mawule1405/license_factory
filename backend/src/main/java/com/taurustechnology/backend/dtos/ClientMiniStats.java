package com.taurustechnology.backend.dtos;

public record ClientMiniStats(
        long total,
        long totalThisMonth,
        double growthRate,
        long activeDeployments,
        double deploymentDensity,
        String lastDeployedName,
        String topLicensedProject,
        String leadArchitect,
        double conversionEfficiency
) {}
