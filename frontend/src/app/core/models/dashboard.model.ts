export interface RecentActivity {
  timestamp: string; // Format: "2026-02-04 19:30:00"
  action: string;    // Ex: "GENERATE", "CREATE", "DELETE"
  details: string;   // Le message complet du log
  status: 'SUCCESS' | 'WARNING' | 'DANGER' | 'INFO'; // Pour le mapping des couleurs CSS
}

/**
 * Le DTO global renvoyé par le DashboardStatsController
 */
export interface DashboardStats {
  totalClients: number;
  totalLicenses: number;
  activeUsers: number;
  recentActivities: RecentActivity[];
  licensesPerMonth: { [key: string]: number }; // Map Java -> Objet JS { "Jan": 5, "Feb": 12 }
}
