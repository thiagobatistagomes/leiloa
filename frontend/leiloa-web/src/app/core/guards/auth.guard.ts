import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);

  return (
    auth.isAuthenticated() ||
    inject(Router).createUrlTree(['/entrar'], { queryParams: { returnUrl: state.url } })
  );
};

export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);

  return auth.isAuthenticated() ? inject(Router).createUrlTree(['/leiloes']) : true;
};

export const adminGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.createUrlTree(['/entrar'], { queryParams: { returnUrl: state.url } });
  }

  return auth.isAdmin() ? true : router.createUrlTree(['/minha-area']);
};
