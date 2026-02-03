
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class StorageService {
  private jwtHelper = new JwtHelperService();
  private ls = localStorage;

  // Stocker des objets complexes (ex: préférences utilisateur)
  saveObject(key: string, value: any): void {
    this.ls.setItem(key, JSON.stringify(value));
  }

  // Lire des objets complexes
  readObject(key: string): any {
    const val = this.ls.getItem(key);
    return val ? JSON.parse(val) : null;
  }

  // Pour les chaînes simples (Token)
  save(key: string, value: string): void {
    this.ls.setItem(key, value);
  }

  read(key: string): string {
    return this.ls.getItem(key) || '';
  }

  remove(key: string): void {
    this.ls.removeItem(key);
  }

  // Centralisation de la logique d'extraction
  private getDecodedToken(key: string): any {
    const token = this.read(key);
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      return this.jwtHelper.decodeToken(token);
    }
    if (token) this.remove(key); // Nettoie si expiré
    return null;
  }

  getUserIdFromToken(key: string): string {
    const payload = this.getDecodedToken(key);
    return payload?.userId || '';
  }

  getUsernameFromToken(key: string): string {
    const payload = this.getDecodedToken(key);
    return payload?.sub || ''; // 'sub' est le standard pour l'username
  }

  getRolesFromToken(key: string): string[] {
    const payload = this.getDecodedToken(key);
    const roles = payload?.roles;
    return Array.isArray(roles) ? roles : [];
  }
}
