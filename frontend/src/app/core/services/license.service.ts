import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LicenseDTO } from '../models/license.model';
import { environment } from '../../../environments/environment';
import {PageResponse} from '../models/auth.model';
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
  saveLicense( license: LicenseDTO): Observable<LicenseDTO> {
    return this.http.post<LicenseDTO>(`${this.apiUrl}/${this.getUserId()}`, license);
  }

  // 2. Lister les licences (Pagination Backend)
  getLicenses( page: number, size: number): Observable<PageResponse<LicenseDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<LicenseDTO>>(`${this.apiUrl}/${this.getUserId()}`, { params });
  }

  // 3. Mettre à jour une configuration
  updateLicense( licenseId: string, license: LicenseDTO): Observable<LicenseDTO> {
    return this.http.put<LicenseDTO>(`${this.apiUrl}/${this.getUserId()}/${licenseId}`, license);
  }

  // 4. Générer et Télécharger le fichier binaire .lic
  downloadLicenseFile(licenseId: string): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/${this.getUserId()}/generate/${licenseId}`, {}, {
      responseType: 'blob'
    });
  }

  // 5. Soft Delete
  deleteLicense( licenseId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${this.getUserId()}/${licenseId}`);
  }
}
