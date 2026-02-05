import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DashboardStats} from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/admin/dashboard/stats`;

  // Utilisation d'un Signal pour stocker les stats de manière réactive
  stats = signal<DashboardStats | null>(null);
  isLoading = signal<boolean>(false);

  /**
   * Récupère l'ensemble des données du dashboard en un seul appel
   */
  fetchStats(): Observable<DashboardStats> {
    this.isLoading.set(true);
    return this.http.get<DashboardStats>(this.apiUrl).pipe(
      tap({
        next: (data) => {
          this.stats.set(data);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false)
      })
    );
  }
}
