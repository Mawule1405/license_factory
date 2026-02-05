package com.taurustechnology.backend.dtos;

import java.util.List;
import java.util.Map;

public record DashboardStats(
        long totalClients,
        long totalLicenses,
        long activeUsers,
        List<RecentActivity> recentActivities,
        Map<String, Long> licensesPerMonth
) {}