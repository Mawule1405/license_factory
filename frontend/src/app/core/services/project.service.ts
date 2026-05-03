import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {Pagination} from '../models/auth.model';
import {Project} from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/projects`;

  /**
   * Récupère la liste paginée des projets avec un mot-clé
   * @param keyword Le terme de recherche
   * @param page L'index de la page (commence à 0 pour le backend)
   * @param size Le nombre d'éléments par page
   */
  fetchProjects(keyword: string = '', page: number = 0, size: number = 10): Observable<Pagination<Project>> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', (page-1).toString())
      .set('size', size.toString());

    return this.http.get<Pagination<Project>>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Récupère un projet spécifique par son ID
   */
  fetchProjectById(id: string): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crée un nouveau projet
   * Note : Le username est extrait du JWT par le backend via Principal
   */
  createProject(project: any): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/create`, project);
  }

  /**
   * Met à jour un projet existant
   */
  updateProject(id: string, project: Partial<Project>): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/update/${id}`, project);
  }

  /**
   * Supprime un projet (Suppression logique côté backend)
   */
  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}
