import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-account-page',
  imports: [AppHeader, FormsModule, RouterLink],
  templateUrl: './account-page.html',
  styleUrl: './account-page.scss',
})
export class AccountPage {
  readonly auth = inject(AuthService);
  private readonly marketplace = inject(MarketplaceService);
  private readonly router = inject(Router);
  readonly message = signal('');
  name = '';
  phone = '';
  currentPassword = '';
  newPassword = '';
  saving = '';
  saveName(): void {
    if (!this.name.trim()) return;
    this.run('name', this.marketplace.updateName(this.name.trim()), 'Nome atualizado.');
  }
  savePhone(): void {
    if (!this.phone.trim()) return;
    this.run('phone', this.marketplace.updatePhone(this.phone.trim()), 'Telefone atualizado.');
  }
  savePassword(): void {
    if (!this.currentPassword || this.newPassword.length < 6) return;
    this.saving = 'password';
    this.marketplace.updatePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.message.set('Senha atualizada.');
        this.currentPassword = '';
        this.newPassword = '';
        this.saving = '';
      },
      error: () => {
        this.message.set('Não foi possível alterar a senha. Confira a senha atual.');
        this.saving = '';
      },
    });
  }
  deactivate(): void {
    if (!confirm('Desativar sua conta? Você precisará reativá-la para entrar novamente.')) return;
    this.marketplace.deactivateAccount().subscribe({
      next: () => {
        this.auth.logout();
        void this.router.navigateByUrl('/');
      },
      error: () => this.message.set('Não foi possível desativar a conta.'),
    });
  }
  formatPhone(): void {
    const digits = this.phone.replace(/\D/g, '').slice(0, 11);
    this.phone =
      digits.length > 7
        ? `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`
        : digits.length > 2
          ? `(${digits.slice(0, 2)}) ${digits.slice(2)}`
          : digits;
  }
  private run(
    type: string,
    request: ReturnType<MarketplaceService['updateName']>,
    success: string,
  ): void {
    this.saving = type;
    request.subscribe({
      next: () => {
        this.message.set(success);
        this.saving = '';
      },
      error: () => {
        this.message.set('Não foi possível salvar a alteração.');
        this.saving = '';
      },
    });
  }
}
