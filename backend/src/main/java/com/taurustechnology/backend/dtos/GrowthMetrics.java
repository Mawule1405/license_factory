package com.taurustechnology.backend.dtos;

import java.util.List;

// DTO pour le graphique de croissance
public record GrowthMetrics(
        List<String> labels,
        List<Long> values
) {}
