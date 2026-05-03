import { Component, inject, effect } from '@angular/core';
import { DiagramService } from '../../../core/services/diagram.service';
import {DashboardService} from '../../../core/services/dashboard.service';
import {GlobalActivityMix, GrowthMetrics, UserActivityMetrics} from '../../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  private ds = inject(DashboardService);
  private diagram = inject(DiagramService);

  // Récupération des signaux
  logs = this.ds.recentLogs;

  constructor() {
    // Surveille et dessine le graphique des utilisateurs dès que les données arrivent
    effect(() => {
      const data = this.ds.userMetrics();
      if (data.length) this.renderUserChart(data);
    });

    // Surveille et dessine la croissance
    effect(() => {
      const data = this.ds.growthMetrics();
      if (data) this.renderGrowthChart(data);
    });

    // Surveille et dessine le mix global
    effect(() => {
      const data = this.ds.activityMix();
      if (data) this.renderMixChart(data);
    });
  }

  ngOnInit() {
    // Chargements parallèles et indépendants
    this.ds.fetchUserMetrics();
    this.ds.fetchGrowth();
    this.ds.fetchActivityMix();
    this.ds.fetchRecentLogs();
  }

  private renderUserChart(data: UserActivityMetrics[]) {
    this.diagram.createChart({
      canvasId: 'userWorkloadCanvas',
      type: 'stacked_bar',
      title: 'PRODUCTIVITÉ PAR OPÉRATEUR',
      labels: data.map(u => u.username),
      datasets: [
        { label: 'Licences', data: data.map(u => u.licensing), color: '#2ed573' },
        { label: 'Clients', data: data.map(u => u.clientRegistration), color: '#70a1ff' },
        { label: 'Exports', data: data.map(u => u.exports), color: '#ffa502' },
        { label: 'Admin', data: data.map(u => u.userManagement), color: '#ff4757' }
      ]
    });
  }

  private renderGrowthChart(data: GrowthMetrics) {
    this.diagram.createChart({
      canvasId: 'growthCanvas',
      type: 'bar_vertical',
      title: 'FLUX MENSUEL DE LICENCES',
      labels: data.labels,
      datasets: [{ label: 'Volume', data: data.values, color: '#2ed573', fill: true }]
    });
  }

  private renderMixChart(data: GlobalActivityMix) {
    this.diagram.createChart({
      canvasId: 'mixCanvas',
      type: 'doughnut_ring',
      title: 'RÉPARTITION DES OPÉRATIONS',
      labels: ['Licences', 'Enregistrements', 'Exports', 'Admin'],
      datasets: [{
        label: 'Actions',
        data: [data.licensing, data.registrations, data.exports, data.admin]
      }]
    });
  }
}
