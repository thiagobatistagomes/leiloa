import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuctionListItem } from '../../core/models/auction.model';
import { Category } from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AuctionCard } from '../../shared/auction-card/auction-card';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-catalog-page',
  imports: [FormsModule, AuctionCard, AppHeader],
  templateUrl: './catalog-page.html',
  styleUrl: './catalog-page.scss',
})
export class CatalogPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  readonly auctions = signal<AuctionListItem[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly loading = signal(true);
  readonly error = signal(false);
  search = '';
  status = '';
  categoryId = '';
  readonly page = signal(0);
  readonly totalPages = signal(0);
  ngOnInit(): void {
    this.marketplace.getCategories().subscribe({ next: (value) => this.categories.set(value) });
    this.load();
  }
  load(page = 0): void {
    this.loading.set(true);
    this.error.set(false);
    this.marketplace
      .getAuctions({
        search: this.search.trim() || undefined,
        categoryId: this.categoryId || undefined,
        status: this.status ? [this.status as 'ACTIVE' | 'SCHEDULED' | 'SOLD'] : undefined,
        page,
      })
      .subscribe({
        next: async (value) => {
          await this.warmUpImages(value.content);
          this.auctions.set(value.content);
          this.page.set(value.number);
          this.totalPages.set(value.totalPages);
          this.loading.set(false);
        },
        error: () => {
          this.error.set(true);
          this.loading.set(false);
        },
      });
  }

  private async warmUpImages(auctions: AuctionListItem[]): Promise<void> {
    const firstRowImages = auctions
      .slice(0, 3)
      .map((auction) => auction.imageUrl)
      .filter((imageUrl): imageUrl is string => !!imageUrl);

    if (!firstRowImages.length) return;

    await Promise.race([
      Promise.all(firstRowImages.map((imageUrl) => this.preloadImage(imageUrl))),
      new Promise((resolve) => setTimeout(resolve, 1600)),
    ]);
  }

  private preloadImage(imageUrl: string): Promise<void> {
    return new Promise((resolve) => {
      const image = new Image();
      image.onload = () => resolve();
      image.onerror = () => resolve();
      image.src = imageUrl;
    });
  }
}
