import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const globalErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401) {
        return throwError(() => error);
      }

      let message = 'An unexpected system error occurred.';
      let title = 'CRITICAL_FAILURE';

      switch (error.status) {

        case 403:
          title = 'ACCESS_DENIED';
          message = 'You do not have the required clearance for this operation.';
          break;
        case 404:
          title = 'RESOURCE_NOT_FOUND';
          message = 'The requested data entity does not exist on the server.';
          break;
        case 400:
          title = 'BAD_REQUEST';
          message = error.error?.message || 'Invalid data submitted to the protocol.';
          break;
        case 500:
          title = 'SERVER_ERROR';
          message = 'The remote server encountered an internal failure.';
          break;
      }

      // On affiche la modale via le service global
      notifService.error(message, title);

      return throwError(() => error);
    })
  );
};
