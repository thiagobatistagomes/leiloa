import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const session = inject(AuthService).session();

  if (!session || request.url.startsWith('/api/auth/')) return next(request);

  return next(
    request.clone({
      setHeaders: { Authorization: `${session.type} ${session.token}` },
    }),
  );
};
