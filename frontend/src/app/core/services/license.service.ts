import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {LicenseResponse, LicenseStats} from '../models/license.model';
import { environment } from '../../../environments/environment';
import { Pagination} from '../models/auth.model';
import {StorageService} from './storage.service';
import {ACCESS_TOKEN} from '../constants/auth.constants';

@Injectable({
  providedIn: 'root'
})
export class LicenseService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/licenses`;

  private storage = inject(StorageService);

  // Utilitaire pour simuler ou récupérer l'ID de l'utilisateur connecté
  private getUserId(): string {
    return this.storage.getUserIdFromToken(ACCESS_TOKEN)
  }

  // 1. Sauvegarder/Créer une configuration de licence
  saveLicense( license: LicenseResponse): Observable<LicenseResponse> {
    return this.http.post<LicenseResponse>(`${this.apiUrl}`, license);
  }

  createLicense(payload: any ) {
    return this.http.post<LicenseResponse>(`${this.apiUrl}`, payload);
  }

  // 2. Lister les licences (Pagination Backend)
  fetchLicenses( page: number, size: number,key:string): Observable<Pagination<LicenseResponse>> {
    const params = new HttpParams()
      .set('key', key)
      .set('page', (page-1).toString())
      .set('size', size.toString());
    return this.http.get<Pagination<LicenseResponse>>(`${this.apiUrl}`, { params });
  }
  getClientLicenses(id: any, page: number, size: number) {
    const params = new HttpParams()
      .set('id', id)
      .set('page', (page-1).toString())
      .set('size', size.toString());
    return this.http.get<Pagination<LicenseResponse>>(`${this.apiUrl}/clients`, { params });
  }

  // 3. Mettre à jour une configuration
  /**
   * Met à jour les paramètres d'une licence existante
   * @param licenseId L'UUID de la licence
   * @param payload Objet contenant la Map des paramètres
   */
  updateLicense(licenseId: string, payload: any): Observable<LicenseResponse> {
    return this.http.put<LicenseResponse>(`${this.apiUrl}/${licenseId}`, payload);
  }

  // 4. Générer et Télécharger le fichier binaire .lic
  downloadLicenseFile(licenseId: string, raison: string): Observable<Blob> {
    // Envoi de la raison dans le corps de la requête
    return this.http.post(`${this.apiUrl}/generate/${licenseId}`,  raison , {
      responseType: 'blob'
    });
  }

  // 5. Soft Delete
  deleteLicense( licenseId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${licenseId}`);
  }

  fetchLicenseMiniStats() {
    return this.http.get<LicenseStats>(`${this.apiUrl}/mini-stats`)
  }

}
