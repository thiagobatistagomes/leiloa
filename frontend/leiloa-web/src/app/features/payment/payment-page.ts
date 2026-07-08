import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuctionDetail } from '../../core/models/auction.model';
import { Address, Payment } from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-payment-page',
  imports: [AppHeader, CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './payment-page.html',
  styleUrl: './payment-page.scss',
})
export class PaymentPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly payment = signal<Payment | null>(null);
  readonly auction = signal<AuctionDetail | null>(null);
  readonly addresses = signal<Address[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  selectedAddress = '';
  referenceNote = '';
  processing = false;
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.marketplace.getPendingPayments().subscribe({
      next: (all) => {
        const payment = all.find((entry) => entry.id === id) ?? null;
        this.payment.set(payment);
        if (payment) this.loadAuction(payment.auctionId);
        if (!payment) this.error.set('Este pagamento não está mais pendente.');
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar o pagamento.');
        this.loading.set(false);
      },
    });
    this.marketplace.getAddresses().subscribe({
      next: (page) => {
        this.addresses.set(page.content);
        this.selectedAddress =
          page.content.find((a) => a.isDefault)?.id ?? page.content[0]?.id ?? '';
      },
    });
  }
  auctionTitle(payment: Payment): string {
    return payment.itemName ?? payment.auctionName ?? this.auction()?.itemName ?? 'Leilão';
  }
  paymentDueAt(payment: Payment): string | null {
    if (payment.expiredAt) return payment.expiredAt;
    if (!payment.createdAt || payment.status !== 'PENDING') return null;
    const createdAt = new Date(payment.createdAt);
    if (Number.isNaN(createdAt.getTime())) return null;
    createdAt.setHours(createdAt.getHours() + 48);
    return createdAt.toISOString();
  }
  confirm(): void {
    const payment = this.payment();
    if (!payment || !this.selectedAddress) return;
    this.processing = true;
    this.error.set('');
    this.marketplace
      .setPaymentAddress(payment.id, this.selectedAddress, this.referenceNote)
      .subscribe({
        next: () =>
          this.marketplace.approvePayment(payment.id).subscribe({
            next: () =>
              void this.router.navigate(['/minha-area'], {
                queryParams: { pagamento: 'concluido' },
              }),
            error: () => {
              this.error.set(
                'O endereço foi salvo, mas o pagamento não pôde ser aprovado. Tente novamente.',
              );
              this.processing = false;
            },
          }),
        error: () => {
          this.error.set('Não foi possível vincular este endereço ao pagamento.');
          this.processing = false;
        },
      });
  }
  private loadAuction(auctionId: string): void {
    this.marketplace.getAuction(auctionId).subscribe({
      next: (auction) => this.auction.set(auction),
    });
  }
}
