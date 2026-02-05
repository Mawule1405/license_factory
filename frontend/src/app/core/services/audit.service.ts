import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuditService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/admin/logs`;

  // Signal privé pour la gestion interne et public en lecture seule
  private _logs = signal<string[]>([]);
  public logs = this._logs.asReadonly();

  /**
   * Récupère les logs dynamiquement selon le type (technical ou audit)
   * @param type 'technical' ou 'audit'
   * @param lines Nombre de lignes à récupérer
   */
  getLogs(type: 'technical' | 'audit', lines: number = 100): Observable<string[]> {
    const params = new HttpParams().set('lines', lines.toString());

    // On utilise l'URL formatée : /api/admin/logs/stream/{type}
    return this.http.get<string[]>(`${this.apiUrl}/stream/${type}`, { params }).pipe(
      tap(data => this._logs.set(data))
    );
  }

  /**
   * Télécharge le fichier d'audit complet (audit.log)
   * Idéal pour les archivages de sécurité hors-ligne
   */
  exportAuditTrail(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export/audit`, { responseType: 'blob' });
  }

  /**
   * Optionnel : Nettoyer le signal des logs lors du changement de vue
   */
  clearLogs(): void {
    this._logs.set([]);
  }
}
