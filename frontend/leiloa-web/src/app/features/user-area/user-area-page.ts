import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { Bid, Delivery, Payment } from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-user-area-page',
  imports: [AppHeader, CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './user-area-page.html',
  styleUrl: './user-area-page.scss',
})
export class UserAreaPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  readonly bids = signal<Bid[]>([]);
  readonly pending = signal<Payment[]>([]);
  readonly payments = signal<Payment[]>([]);
  readonly deliveries = signal<Record<string, Delivery>>({});
  readonly auctionNames = signal<Record<string, string>>({});
  readonly loading = signal(true);
  ngOnInit(): void {
    let finished = 0;
    const done = () => {
      if (++finished === 3) this.loading.set(false);
    };
    this.marketplace.getMyBids().subscribe({
      next: (p) => {
        this.bids.set(p.content);
        this.loadAuctionNames(p.content.map((bid) => bid.auctionId));
        done();
      },
      error: done,
    });
    this.marketplace.getPendingPayments().subscribe({
      next: (p) => {
        this.pending.set(p);
        this.loadAuctionNames(p.map((payment) => payment.auctionId));
        done();
      },
      error: done,
    });
    this.marketplace.getPayments().subscribe({
      next: (p) => {
        this.payments.set(p.content);
        this.loadAuctionNames(p.content.map((payment) => payment.auctionId));
        p.content
          .filter((payment) => payment.status === 'COMPLETED')
          .forEach((payment) => this.loadDelivery(payment.id));
        done();
      },
      error: done,
    });
  }
  auctionTitle(entry: Bid | Payment): string {
    const auctionId = entry.auctionId;
    if (!auctionId) return 'Leilão';
    return entry.itemName ?? entry.auctionName ?? this.auctionNames()[auctionId] ?? 'Leilão';
  }
  paymentDueAt(payment: Payment): string | null {
    if (payment.expiredAt) return payment.expiredAt;
    if (!payment.createdAt || payment.status !== 'PENDING') return null;
    const createdAt = new Date(payment.createdAt);
    if (Number.isNaN(createdAt.getTime())) return null;
    createdAt.setHours(createdAt.getHours() + 48);
    return createdAt.toISOString();
  }
  paymentLabel(payment: Payment): string {
    return (
      {
        COMPLETED: 'Pago',
        EXPIRED: 'Expirado',
        CANCELLED: 'Cancelado',
        PENDING: 'Pendente',
      } as const
    )[payment.status];
  }
  deliveryLabel(status: string): string {
    return (
      (
        {
          PENDING: 'Aguardando preparo',
          PROCESSING: 'Em preparação',
          SHIPPED: 'Enviado',
          IN_TRANSIT: 'Em trânsito',
          OUT_FOR_DELIVERY: 'Saiu para entrega',
          DELIVERED: 'Entregue',
          RETURN_REQUESTED: 'Devolução solicitada',
          RETURNED: 'Devolvido',
        } as Record<string, string>
      )[status] ?? status
    );
  }
  private loadDelivery(paymentId: string): void {
    this.marketplace.getDelivery(paymentId).subscribe({
      next: (delivery) => this.deliveries.update((all) => ({ ...all, [paymentId]: delivery })),
    });
  }
  private loadAuctionNames(auctionIds: Array<string | undefined>): void {
    const missingIds = Array.from(
      new Set(auctionIds.filter((id): id is string => !!id && !this.auctionNames()[id])),
    );
    if (!missingIds.length) return;
    forkJoin(
      missingIds.map((id) => this.marketplace.getAuction(id).pipe(catchError(() => of(null)))),
    ).subscribe((auctions) => {
      const names = auctions.reduce(
        (all, auction) => {
          if (auction) all[auction.auctionId] = auction.itemName;
          return all;
        },
        {} as Record<string, string>,
      );
      if (Object.keys(names).length) {
        this.auctionNames.update((current) => ({ ...current, ...names }));
      }
    });
  }
}
