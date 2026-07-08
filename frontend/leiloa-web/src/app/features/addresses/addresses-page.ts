import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Address, AddressRequest } from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-addresses-page',
  imports: [AppHeader, FormsModule],
  templateUrl: './addresses-page.html',
  styleUrl: './addresses-page.scss',
})
export class AddressesPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  readonly addresses = signal<Address[]>([]);
  readonly message = signal('');
  loading = true;
  showForm = false;
  editingId: string | null = null;
  submitting = false;
  form: AddressRequest = this.emptyAddress();
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.marketplace.getAddresses().subscribe({
      next: (page) => {
        this.addresses.set(page.content);
        this.loading = false;
      },
      error: () => {
        this.message.set('Não foi possível carregar seus endereços.');
        this.loading = false;
      },
    });
  }
  open(address?: Address): void {
    this.editingId = address?.id ?? null;
    this.form = address
      ? {
          label: address.label,
          street: address.street,
          number: address.number,
          complement: address.complement,
          district: address.district,
          city: address.city,
          state: address.state,
          country: address.country,
          postalCode: address.postalCode,
          isDefault: address.isDefault,
        }
      : this.emptyAddress();
    this.showForm = true;
  }
  save(): void {
    this.submitting = true;
    const request = this.editingId
      ? this.marketplace.updateAddress(this.editingId, this.form)
      : this.marketplace.createAddress(this.form);
    request.subscribe({
      next: () => {
        this.message.set('Endereço salvo.');
        this.showForm = false;
        this.submitting = false;
        this.load();
      },
      error: () => {
        this.message.set('Revise os dados do endereço.');
        this.submitting = false;
      },
    });
  }
  setDefault(address: Address): void {
    this.marketplace.setDefaultAddress(address.id).subscribe({
      next: () => {
        this.message.set('Endereço padrão atualizado.');
        this.load();
      },
    });
  }
  remove(address: Address): void {
    if (!confirm(`Excluir o endereço “${address.label}”?`)) return;
    this.marketplace.deleteAddress(address.id).subscribe({
      next: () => {
        this.message.set('Endereço excluído.');
        this.load();
      },
      error: () => this.message.set('Não foi possível excluir este endereço.'),
    });
  }
  formatPostalCode(): void {
    const digits = this.form.postalCode.replace(/\D/g, '').slice(0, 8);
    this.form.postalCode = digits.length > 5 ? `${digits.slice(0, 5)}-${digits.slice(5)}` : digits;
  }
  private emptyAddress(): AddressRequest {
    return {
      label: '',
      street: '',
      number: '',
      complement: '',
      district: '',
      city: '',
      state: '',
      country: 'Brasil',
      postalCode: '',
      isDefault: false,
    };
  }
}
