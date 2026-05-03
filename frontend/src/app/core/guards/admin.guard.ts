
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StorageService } from '../services/storage.service';
import {ACCESS_TOKEN} from '../constants/auth.constants';

export const adminGuard: CanActivateFn = (route, state) => {
  const storage = inject(StorageService);
  const router = inject(Router);

  const roles = storage.getRolesFromToken(ACCESS_TOKEN);

  if (roles && roles.length > 0 && (roles.includes("ADMINISTRATOR")||roles.includes("ROLE_ADMINISTRATOR"))) {
    return true;
  }

  return router.createUrlTree(['/workspace/dashboard'], {
    queryParams: { returnUrl: state.url }
  });
};
