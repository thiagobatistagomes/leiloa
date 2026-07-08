import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { RegisterRequest } from '../../core/models/auth.model';
import { AuthService } from '../../core/services/auth.service';

type AuthMode = 'login' | 'register';
type FieldName = 'name' | 'phoneNumber' | 'email' | 'password';

@Component({
  selector: 'app-auth-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './auth-page.html',
  styleUrl: './auth-page.scss',
})
export class AuthPage {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly mode = this.route.snapshot.data['mode'] as AuthMode;
  readonly isLogin = this.mode === 'login';
  readonly isSubmitting = signal(false);
  readonly showPassword = signal(false);
  readonly submitError = signal('');
  readonly canReactivate = signal(false);

  readonly form = new FormGroup({
    name: new FormControl('', {
      nonNullable: true,
      validators: this.isLogin ? [] : [Validators.required, Validators.minLength(2)],
    }),
    phoneNumber: new FormControl('', {
      nonNullable: true,
      validators: this.isLogin
        ? []
        : [Validators.required, Validators.pattern(/^\(?[1-9]{2}\)?\s?9\d{4}-?\d{4}$/)],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: this.isLogin
        ? [Validators.required]
        : [Validators.required, Validators.minLength(6)],
    }),
  });

  submit(): void {
    this.submitError.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const request$ = this.isLogin
      ? this.authService.login({ email: value.email, password: value.password })
      : this.authService.register(value as RegisterRequest);

    this.isSubmitting.set(true);
    request$.pipe(finalize(() => this.isSubmitting.set(false))).subscribe({
      next: () =>
        void this.router.navigateByUrl(
          this.route.snapshot.queryParamMap.get('returnUrl') ?? '/leiloes',
        ),
      error: (error: HttpErrorResponse) => this.handleSubmitError(error),
    });
  }

  reactivate(): void {
    const { email, password } = this.form.getRawValue();
    this.isSubmitting.set(true);
    this.authService
      .reactivate({ email, password })
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => void this.router.navigateByUrl('/minha-area'),
        error: () =>
          this.submitError.set('Não foi possível reativar a conta com estas credenciais.'),
      });
  }

  fieldError(fieldName: FieldName): string {
    const field = this.form.controls[fieldName];

    if (!field.touched || !field.errors) return '';
    if (field.errors['required']) return 'Este campo é obrigatório.';
    if (field.errors['email']) return 'Informe um e-mail válido.';
    if (field.errors['backend']) return String(field.errors['backend']);
    if (fieldName === 'name') return 'Informe ao menos 2 caracteres.';
    if (fieldName === 'phoneNumber') return 'Use um celular brasileiro com DDD.';
    if (fieldName === 'password') return 'A senha deve ter no mínimo 6 caracteres.';
    return 'Confira este campo.';
  }

  formatPhone(): void {
    const digits = this.form.controls.phoneNumber.value.replace(/\D/g, '').slice(0, 11);
    let formatted = digits;

    if (digits.length > 2) formatted = `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    if (digits.length > 7) formatted = `${formatted.slice(0, 10)}-${formatted.slice(10)}`;

    this.form.controls.phoneNumber.setValue(formatted, { emitEvent: false });
  }

  private handleSubmitError(error: HttpErrorResponse): void {
    if (error.status === 422 && error.error && typeof error.error === 'object') {
      const backendErrors = error.error as Record<string, string>;

      for (const fieldName of Object.keys(backendErrors) as FieldName[]) {
        const field = this.form.controls[fieldName];
        if (field) field.setErrors({ backend: backendErrors[fieldName] });
      }

      this.form.markAllAsTouched();
      this.submitError.set('Revise os campos destacados e tente novamente.');
      return;
    }

    if (error.status === 401) {
      this.submitError.set(
        error.error?.message === 'Conta inativa'
          ? (this.canReactivate.set(true), 'Esta conta está inativa. Você pode reativá-la agora.')
          : 'E-mail ou senha incorretos.',
      );
      return;
    }

    if (error.status === 403) {
      this.submitError.set('Esta conta está bloqueada. Entre em contato com o suporte.');
      return;
    }

    this.submitError.set(
      this.isLogin
        ? 'Não foi possível entrar agora. Tente novamente em instantes.'
        : 'Não foi possível concluir o cadastro. Confira os dados ou tente novamente.',
    );
  }
}
