import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import {
  AuthResponse,
  AuthSession,
  LoginRequest,
  RegisterRequest,
  UserRole,
} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/auth';
  private readonly storageKey = 'leiloa.auth';
  private readonly sessionState = signal<AuthSession | null>(this.restoreSession());

  readonly session = this.sessionState.asReadonly();
  readonly isAuthenticated = computed(() => this.sessionState() !== null);
  readonly isAdmin = computed(() => this.hasRole('ROLE_ADMIN'));

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.endpoint}/login`, credentials)
      .pipe(tap((response) => this.saveSession(response)));
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.endpoint}/register`, data)
      .pipe(tap((response) => this.saveSession(response)));
  }

  reactivate(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.endpoint}/reactivate`, credentials)
      .pipe(tap((response) => this.saveSession(response)));
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.sessionState.set(null);
  }

  hasRole(role: UserRole): boolean {
    return this.sessionState()?.roles.includes(role) ?? false;
  }

  private saveSession(response: AuthResponse): void {
    const session: AuthSession = {
      ...response,
      expiresAt: Date.now() + response.expiresIn,
    };

    localStorage.setItem(this.storageKey, JSON.stringify(session));
    this.sessionState.set(session);
  }

  private restoreSession(): AuthSession | null {
    const storedSession = localStorage.getItem(this.storageKey);

    if (!storedSession) return null;

    try {
      const session = JSON.parse(storedSession) as AuthSession;

      if (!session.token || session.expiresAt <= Date.now()) {
        localStorage.removeItem(this.storageKey);
        return null;
      }

      return session;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
