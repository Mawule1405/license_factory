package com.taurustechnology.backend.controllers;


import com.taurustechnology.backend.dtos.GlobalActivityMix;
import com.taurustechnology.backend.dtos.GrowthMetrics;
import com.taurustechnology.backend.dtos.RecentActivity;
import com.taurustechnology.backend.dtos.UserActivityMetrics;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;

import com.taurustechnology.backend.services.DashboardService;
import com.taurustechnology.backend.services.impl.AuditLogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardStatsController {

    private final DashboardService dashboardService;

    @GetMapping("/user-metrics")
    public ResponseEntity<List<UserActivityMetrics>> getUserMetrics() {
        return ResponseEntity.ok(dashboardService.getUserActivityBreakdown());
    }

    @GetMapping("/growth")
    public ResponseEntity<GrowthMetrics> getGrowth() {
        return ResponseEntity.ok(dashboardService.getMonthlyGrowth());
    }

    @GetMapping("/activity-mix")
    public ResponseEntity<GlobalActivityMix> getActivityMix() {
        return ResponseEntity.ok(dashboardService.getGlobalMix());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<RecentActivity>> getLogs() {
        return ResponseEntity.ok(dashboardService.getLatestActivities());
    }
}