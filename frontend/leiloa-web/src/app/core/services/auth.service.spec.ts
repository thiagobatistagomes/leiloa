import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthResponse } from '../models/auth.model';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  const response: AuthResponse = {
    token: 'jwt-token',
    type: 'Bearer',
    expiresIn: 3_600_000,
    userId: 'a82f67d8-3f37-4aa8-b524-e614263ebdb3',
    email: 'admin@leiloa.com',
    roles: ['ROLE_ADMIN'],
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
  });

  it('authenticates with the backend contract and stores roles', () => {
    service.login({ email: response.email, password: 'secret' }).subscribe();

    const request = httpTesting.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ email: response.email, password: 'secret' });
    request.flush(response);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.isAdmin()).toBe(true);
    expect(service.session()?.token).toBe(response.token);
    expect(JSON.parse(localStorage.getItem('leiloa.auth') ?? '{}').roles).toEqual(['ROLE_ADMIN']);
  });

  it('registers the fields required by the backend', () => {
    const registration = {
      name: 'Maria Silva',
      phoneNumber: '(11) 99999-9999',
      email: 'maria@exemplo.com',
      password: 'secret',
    };

    service.register(registration).subscribe();

    const request = httpTesting.expectOne('/api/auth/register');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(registration);
    request.flush({ ...response, email: registration.email, roles: ['ROLE_USER'] });

    expect(service.hasRole('ROLE_USER')).toBe(true);
  });
});
