import {Component, computed, inject} from '@angular/core';
import {DatePipe} from '@angular/common';
import {DashboardService} from '../../../core/services/dashboard.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-dashboard.component',
  imports: [
    RouterLink
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {

  private dashboardService = inject(DashboardService);

  // Accès direct aux signaux du service
  stats = this.dashboardService.stats;
  loading = this.dashboardService.isLoading;

  // Exemple de calcul réactif pour le graphique (extraire les clés du Map)
  chartLabels = computed(() => {
    const s = this.stats();
    return s ? Object.keys(s.licensesPerMonth) : [];
  });

  ngOnInit() {
    this.dashboardService.fetchStats().subscribe();
  }

  /**
   * Helper pour mapper le status du log à une classe CSS Tailwind
   */
  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'border-taurus-green text-taurus-green';
      case 'WARNING': return 'border-yellow-500 text-yellow-500';
      case 'DANGER': return 'border-red-600 text-red-600';
      default: return 'border-gray-500 text-gray-400';
    }
  }

  maxLicenseValue = computed(() => {
    const s = this.stats();
    if (!s || !s.licensesPerMonth) return 10;
    const values = Object.values(s.licensesPerMonth);
    return Math.max(...values, 10); // On met 10 par défaut pour éviter division par 0
  });

}
