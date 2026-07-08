import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuctionListItem } from '../../core/models/auction.model';
import { AuctionService } from '../../core/services/auction.service';
import { AuctionCard } from '../../shared/auction-card/auction-card';
import { SiteHeader } from '../../shared/site-header/site-header';

@Component({
  selector: 'app-landing-page',
  imports: [AuctionCard, SiteHeader, RouterLink],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage implements OnInit {
  private readonly auctionService = inject(AuctionService);
  readonly auctions = signal<AuctionListItem[]>([]);
  readonly isLoading = signal(true);
  readonly hasLoadError = signal(false);

  ngOnInit(): void {
    this.loadAuctions();
  }

  loadAuctions(): void {
    this.isLoading.set(true);
    this.hasLoadError.set(false);
    this.auctionService
      .getFeaturedAuctions()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (page) => this.auctions.set(page.content),
        error: () => this.hasLoadError.set(true),
      });
  }
}
