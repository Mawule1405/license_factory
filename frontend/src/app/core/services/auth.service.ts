// src/app/core/services/auth.service.ts
import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {Observable} from 'rxjs';
import {TokenResponse} from '../models/token-response.model';
import {StorageService} from './storage.service';
import {Router} from '@angular/router';
import {ACCESS_TOKEN, REFRESH_TOKEN} from '../constants/auth.constants'; // Import générique

@Injectable({ providedIn: 'root' })
export class AuthService {
  // On utilise la variable d'environnement
  private readonly apiUrl = `${environment.apiUrl}`;

  private http = inject(HttpClient);
  private storage = inject(StorageService);
  private router = inject(Router);

  // Utilitaire pour simuler ou récupérer l'ID de l'utilisateur connecté
  private getUserId(): string {
    return this.storage.getUserIdFromToken(ACCESS_TOKEN)
  }

  login(credentials: any) {
    return this.http.post<TokenResponse>(`${this.apiUrl.replace("/api","/login")}`, credentials);
  }

  refreshToken(token: string): Observable<TokenResponse> {
    // On envoie le refresh token pour en obtenir un nouveau
    return this.http.post<TokenResponse>(`${environment.apiUrl}/refresh-token`,  {
      refreshToken: token
    });
  }

  logout(): void {
    // 1. Appel optionnel au backend pour invalider le refresh token
    const refreshToken = this.storage.read(REFRESH_TOKEN);
    if (refreshToken) {
      this.http.patch(`${this.apiUrl}/users/logout/${this.getUserId()}`,{}).subscribe({
        next: () => this.finalizeLogout(),
        error: () => this.finalizeLogout()
      });
    } else {
      this.finalizeLogout();
    }
  }

  private finalizeLogout(): void {
    // 2. Supprimer les tokens du localStorage
    this.storage.remove(ACCESS_TOKEN);
    this.storage.remove(REFRESH_TOKEN);

    // 3. Rediriger vers la page de login
    this.router.navigate(['/login']);
  }
}
