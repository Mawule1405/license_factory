package com.taurustechnology.backend.dtos;


public record LicenseMiniStats(
        long total,
        long activeTotal,
        double growthRate,
        double conversionEfficiency,
        String lastDeployedName,
        String topLicensedProject,
        String leadArchitect,
        double deploymentDensity
) {}
