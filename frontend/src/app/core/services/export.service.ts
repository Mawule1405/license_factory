import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {Pagination} from '../models/auth.model';
import {ExportResponse} from '../models/export_response.model';


@Injectable({
  providedIn: 'root'
})
export class ExportService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/exports`;

  /**
   * Récupère tous les exports avec pagination
   * @param page Index de la page (0 par défaut)
   * @param size Taille de la page (10 par défaut)
   */
  fetchAllExports(page: number = 1, size: number = 10): Observable<Pagination<ExportResponse>> {
    const params = new HttpParams()
      .set('page', (page-1).toString())
      .set('size', size.toString());

    return this.http.get<Pagination<ExportResponse>>(this.apiUrl, { params });
  }

  /**
   * Récupère les exports liés à une licence spécifique
   * @param licenseId Identifiant de la licence
   * @param page Index de la page
   * @param size Taille de la page
   */
  fetchExportsByLicense(licenseId: string, page: number = 1, size: number = 10): Observable<Pagination<ExportResponse>> {
    const params = new HttpParams()
      .set('page', (page-1).toString())
      .set('size', size.toString());

    return this.http.get<Pagination<ExportResponse>>(`${this.apiUrl}/license/${licenseId}`, { params });
  }
}
