import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UserActivityMetrics, GrowthMetrics, GlobalActivityMix, RecentActivity } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/dashboard`;

  // Signaux indépendants
  userMetrics = signal<UserActivityMetrics[]>([]);
  growthMetrics = signal<GrowthMetrics | null>(null);
  activityMix = signal<GlobalActivityMix | null>(null);
  recentLogs = signal<RecentActivity[]>([]);

  // Chargements indépendants
  fetchUserMetrics() {
    return this.http.get<UserActivityMetrics[]>(`${this.baseUrl}/user-metrics`)
      .subscribe(data => this.userMetrics.set(data));
  }

  fetchGrowth() {
    return this.http.get<GrowthMetrics>(`${this.baseUrl}/growth`)
      .subscribe(data => this.growthMetrics.set(data));
  }

  fetchActivityMix() {
    return this.http.get<GlobalActivityMix>(`${this.baseUrl}/activity-mix`)
      .subscribe(data => this.activityMix.set(data));
  }

  fetchRecentLogs() {
    return this.http.get<RecentActivity[]>(`${this.baseUrl}/logs`)
      .subscribe(data => this.recentLogs.set(data));
  }
}
