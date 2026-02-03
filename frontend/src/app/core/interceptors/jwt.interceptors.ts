// src/app/core/interceptors/jwt.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage.service';
import { AuthService } from '../services/auth.service';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../constants/auth.constants';
import {NotificationService} from '../services/notification.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(StorageService);
  const authService = inject(AuthService);
  const notifService = inject(NotificationService)

  const token = storage.read(ACCESS_TOKEN);
  let authReq = req;

  if (token) {
    authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Cas 1: C'est un 401 sur une requête normale -> On tente le REFRESH
      if (error.status === 401 && !req.url.includes('/refresh-token')) {
        return handle401Error(authReq, next, authService,notifService, storage);
      }

      // Cas 2: C'est un 401 ALORS qu'on essayait déjà de REFRESHER
      // Ou toute autre erreur critique -> LOGOUT immédiat
      if (error.status === 401 && req.url.includes('/refresh-token')) {
        authService.logout(); // Redirige vers /login
      }

      return throwError(() => error);
    })
  );
};

const handle401Error = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  notifService: NotificationService,
  storage: StorageService
): Observable<HttpEvent<unknown>> => {
  const refreshToken = storage.read(REFRESH_TOKEN);

  if (!refreshToken) {
    notifService.error(
      "Your security session has expired. Please log in again to continue.",
      "SESSION_EXPIRED"
    );
    authService.logout();
    return throwError(() => new Error('SESSION_EXPIRED_NO_REFRESH'));
  }

  return authService.refreshToken(refreshToken).pipe(
    switchMap((res) => {
      storage.save(ACCESS_TOKEN, res.accessToken);
      storage.save(REFRESH_TOKEN, res.refreshToken);

      // On re-tente la requête initiale avec le NOUVEAU token
      console.log('--- RETRYING WITH NEW TOKEN ---')
      return next(req.clone({
        setHeaders: { Authorization: `Bearer ${res.accessToken}` }
      }));
    }),
    catchError((err) => {
      // Si le serveur répond 401 ici, le refreshToken est mort.
      authService.logout();
      console.log(err)
      return throwError(() => err);
    })
  );
};
