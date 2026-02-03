import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AppRole } from '../models/auth.model';


@Injectable({
  providedIn: 'root'
})
export class AppRoleService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/roles`;

  /** Récupère tous les rôles (@GetMapping("/all")) */
  findAll(): Observable<AppRole[]> {
    return this.http.get<AppRole[]>(`${this.API_URL}/all`);
  }

  /** Trouve un rôle par ID (@GetMapping("/find/{id}")) */
  findById(id: string): Observable<AppRole> {
    return this.http.get<AppRole>(`${this.API_URL}/find/${id}`);
  }

  /** Crée un nouveau rôle (@PostMapping("/create")) */
  create(role: AppRole): Observable<AppRole> {
    return this.http.post<AppRole>(`${this.API_URL}/create`, role);
  }
}
