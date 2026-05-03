import { HttpInterceptorFn, HttpErrorResponse, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage.service';
import { AuthService } from '../services/auth.service';
import { catchError, Observable, switchMap, throwError, BehaviorSubject, filter, take, finalize } from 'rxjs';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../constants/auth.constants';

// Variables globales à l'intercepteur pour gérer l'état du verrou
let isRefreshing = false;
const refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(StorageService);
  const authService = inject(AuthService);

  const token = storage.read(ACCESS_TOKEN);
  let authReq = req;

  if (token) {
    authReq = addTokenHeader(req, token);
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401 && !req.url.includes('/refresh-token')) {
        return handle401Error(authReq, next, authService, storage);
      }
      return throwError(() => error);
    })
  );
};

const handle401Error = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  storage: StorageService
): Observable<HttpEvent<unknown>> => {

  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null); // On réinitialise le flux

    const refreshToken = storage.read(REFRESH_TOKEN);

    return authService.refreshToken(refreshToken).pipe(
      switchMap((res) => {
        storage.save(ACCESS_TOKEN, res.accessToken);
        storage.save(REFRESH_TOKEN, res.refreshToken);

        // On libère toutes les requêtes en attente avec le nouveau token
        refreshTokenSubject.next(res.accessToken);

        return next(addTokenHeader(req, res.accessToken));
      }),
      catchError((err) => {
        authService.logout();
        return throwError(() => err);
      }),
      finalize(() => {
        isRefreshing = false; // On déverrouille quoi qu'il arrive
      })
    );
  } else {
    // SI UN REFRESH EST DÉJÀ EN COURS :
    // On attend que refreshTokenSubject émette une nouvelle valeur (le nouveau token)
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(token => next(addTokenHeader(req, token!)))
    );
  }
};

// Helper pour cloner la requête proprement
const addTokenHeader = (request: HttpRequest<any>, token: string) => {
  return request.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
};
