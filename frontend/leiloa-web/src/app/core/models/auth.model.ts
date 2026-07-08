export type UserRole = 'ROLE_USER' | 'ROLE_ADMIN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends LoginRequest {
  name: string;
  phoneNumber: string;
}

export interface AuthResponse {
  token: string;
  type: 'Bearer';
  expiresIn: number;
  userId: string;
  email: string;
  roles: UserRole[];
}

export interface AuthSession extends AuthResponse {
  expiresAt: number;
}
