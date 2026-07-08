import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuctionListItem } from '../../core/models/auction.model';

@Component({
  selector: 'app-auction-card',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './auction-card.html',
  styleUrl: './auction-card.scss',
})
export class AuctionCard {
  readonly auction = input.required<AuctionListItem>();
  readonly priority = input(false);
  readonly imageFailed = signal(false);

  get statusLabel(): string {
    return this.auction().status === 'ACTIVE'
      ? 'Ao vivo'
      : this.auction().status === 'SCHEDULED'
        ? 'Em breve'
        : 'Vendido';
  }

  get datePrefix(): string {
    const status = this.auction().status;
    if (status === 'ACTIVE') return 'Encerra em';
    if (status === 'SCHEDULED') return 'Começa em';
    if (status === 'SOLD') return 'Vendido em';
    return 'Encerrado em';
  }

  get displayDate(): string {
    const auction = this.auction();
    return auction.status === 'SCHEDULED' ? auction.startDate : auction.endDate;
  }

  onImageError(): void {
    this.imageFailed.set(true);
  }
}
