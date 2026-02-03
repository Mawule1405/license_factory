import {HttpClient, HttpParams} from '@angular/common/http';
import {PageResponse} from '../models/auth.model';
import {Client} from '../models/client.model';
import {Observable} from 'rxjs';
import {inject, Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {StorageService} from './storage.service';
import {ACCESS_TOKEN} from '../constants/auth.constants';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/clients`;
  private storage = inject(StorageService);

  // Utilitaire pour simuler ou récupérer l'ID de l'utilisateur connecté
  private getUserId(): string {
    return this.storage.getUserIdFromToken(ACCESS_TOKEN)
  }

  findClients(page: number, size: number, searchKey: string): Observable<PageResponse<Client>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('searchKey', searchKey);
    return this.http.get<PageResponse<Client>>(`${this.apiUrl}/${this.getUserId()}`, { params });
  }

  saveClient(client: Client): Observable<Client> {
    client.creatorId = this.getUserId();
    return this.http.post<Client>(`${this.apiUrl}/${this.getUserId()}`, client);
  }

  deleteClient(clientId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${this.getUserId()}/${clientId}`);
  }

  getClientById( clientId: string) {
    return this.http.get<Client>(`${this.apiUrl}/${this.getUserId()}/${clientId}`)
  }
}
