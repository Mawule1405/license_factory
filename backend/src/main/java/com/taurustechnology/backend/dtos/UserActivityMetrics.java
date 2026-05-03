package com.taurustechnology.backend.dtos;

import java.util.List;
import java.util.Map;

// DTO pour la répartition par utilisateur
public record UserActivityMetrics(
        String username,
        long licensing,
        long clientRegistration,
        long exports,
        long userManagement
) {}

