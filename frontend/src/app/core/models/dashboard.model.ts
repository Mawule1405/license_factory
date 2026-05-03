export interface UserActivityMetrics {
  username: string;
  licensing: number;
  clientRegistration: number;
  exports: number;
  userManagement: number;
}

export interface GrowthMetrics {
  labels: string[];
  values: number[];
}

export interface GlobalActivityMix {
  licensing: number;
  registrations: number;
  exports: number;
  admin: number;
}

export interface RecentActivity {
  timestamp: string;
  action: string;
  details: string;
  status: 'SUCCESS' | 'WARNING' | 'DANGER' | 'INFO';
}
