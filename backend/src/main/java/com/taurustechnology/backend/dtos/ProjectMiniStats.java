package com.taurustechnology.backend.dtos;



public record ProjectMiniStats(
        long total,
        long totalThisMonth,
        long newDeployments,
        String lastDeployedName,
        String topLicensedProject,
        String leadArchitect
) {}