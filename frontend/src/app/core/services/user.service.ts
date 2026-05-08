import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {AppUser, Pagination} from '../models/auth.model';
import {StorageService} from './storage.service';
import {ACCESS_TOKEN} from '../constants/auth.constants';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/users`;
  private storage = inject(StorageService)


  private getUserId(): string {
    return this.storage.getUserIdFromToken(ACCESS_TOKEN)
  }

  // Recherche avec pagination Backend
  searchUsers(keyword: string, page: number, size: number): Observable<Pagination<AppUser>> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', (page-1).toString())
      .set('size', size.toString());

    return this.http.get<Pagination<AppUser>>(`${this.apiUrl}/search`, { params });
  }

  // Création d'un utilisateur
  createUser(user: Partial<AppUser>): Observable<AppUser> {
    return this.http.post<AppUser>(`${this.apiUrl}/create`, user);
  }

  // Suppression (Soft delete ou Hard delete selon ton backend)
  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  revokeUser(id: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/revoke/${id}`,{});
  }

  getUser(userId: string) {
    return this.http.get<AppUser>(`${this.apiUrl}/find/${userId}`);
  }

  updateProfile(currentUserId: string, value: any) {
    return this.http.put<AppUser>(`${this.apiUrl}/update/${currentUserId}`, value);
  }

  changePassword(currentUserId: string, oldPassword: string, newPassword: string): Observable<AppUser> {

    const payload = {
      oldPassword: oldPassword.trim(),
      newPassword: newPassword.trim()
    };

    return this.http.patch<AppUser>(
      `${this.apiUrl}/change-password/${currentUserId}`,
      payload
    );
  }

  resetPassword( userId:string) {
    return this.http.patch<AppUser>(`${this.apiUrl}/initialize-password/${userId}`, {})
  }

}
