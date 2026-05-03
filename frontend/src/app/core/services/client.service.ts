import {HttpClient, HttpParams} from '@angular/common/http';
import {Pagination} from '../models/auth.model';
import {Client, ClientStats} from '../models/client.model';
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

  fetchClients(page: number, size: number, searchKey: string): Observable<Pagination<Client>> {
    const params = new HttpParams()
      .set('page', (page-1).toString())
      .set('size', size.toString())
      .set('searchKey', searchKey);
    return this.http.get<Pagination<Client>>(`${this.apiUrl}`, { params });
  }

  saveClient(client: Client): Observable<Client> {
    return this.http.post<Client>(`${this.apiUrl}`, client);
  }

  deleteClient(clientId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${clientId}`);
  }

  getClientById( clientId: string) {
    return this.http.get<Client>(`${this.apiUrl}/${clientId}`)
  }

  updateClient(updatedClient: Client): Observable<Client> {
    const url = `${this.apiUrl}/${updatedClient.id}`;
    return this.http.put<Client>(url, updatedClient);
  }

  fetchClientMiniStats() {
    return this.http.get<ClientStats>(`${this.apiUrl}/mini-stats`)
  }
}
