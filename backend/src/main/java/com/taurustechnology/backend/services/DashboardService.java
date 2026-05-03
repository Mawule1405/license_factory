package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.GlobalActivityMix;
import com.taurustechnology.backend.dtos.GrowthMetrics;
import com.taurustechnology.backend.dtos.RecentActivity;
import com.taurustechnology.backend.dtos.UserActivityMetrics;

import java.util.List;

public interface DashboardService {
    List<UserActivityMetrics> getUserActivityBreakdown();

    GrowthMetrics getMonthlyGrowth();

    GlobalActivityMix getGlobalMix();

    List<RecentActivity> getLatestActivities();
}
