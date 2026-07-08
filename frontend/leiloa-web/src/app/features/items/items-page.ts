import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Category, Item, ItemRequest } from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-items-page',
  imports: [AppHeader, DatePipe, FormsModule],
  templateUrl: './items-page.html',
  styleUrl: './items-page.scss',
})
export class ItemsPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  readonly items = signal<Item[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly message = signal('');
  readonly loading = signal(true);
  showItemForm = false;
  showAuctionForm = false;
  editingId: string | null = null;
  submitting = false;
  item: ItemRequest = { name: '', description: '', imageUrl: null, categoryId: '' };
  auction = {
    itemId: '',
    startPrice: null as number | null,
    minIncrement: null as number | null,
    startDate: '',
    endDate: '',
  };
  ngOnInit(): void {
    this.load();
    this.marketplace.getCategories().subscribe({ next: (all) => this.categories.set(all) });
  }
  load(): void {
    this.marketplace.getMyItems().subscribe({
      next: (all) => {
        this.items.set(all);
        this.loading.set(false);
      },
      error: () => {
        this.message.set('Não foi possível carregar seus itens.');
        this.loading.set(false);
      },
    });
  }
  openItem(item?: Item): void {
    this.showAuctionForm = false;
    this.showItemForm = true;
    this.editingId = item?.id ?? null;
    this.item = item
      ? {
          name: item.name,
          description: item.description,
          imageUrl: item.imageUrl,
          categoryId: item.categoryId,
        }
      : { name: '', description: '', imageUrl: null, categoryId: '' };
  }
  saveItem(): void {
    if (!this.item.name || !this.item.categoryId) return;
    this.submitting = true;
    const request = this.editingId
      ? this.marketplace.updateItem(this.editingId, this.item)
      : this.marketplace.createItem(this.item);
    request.subscribe({
      next: () => {
        this.message.set(
          this.editingId
            ? 'Item atualizado.'
            : 'Item cadastrado. Agora você já pode criar o leilão.',
        );
        this.showItemForm = false;
        this.submitting = false;
        this.load();
      },
      error: (e: HttpErrorResponse) => {
        this.message.set(e.error?.message ?? 'Não foi possível salvar o item.');
        this.submitting = false;
      },
    });
  }
  remove(item: Item): void {
    if (!confirm(`Remover “${item.name}”?`)) return;
    this.marketplace.deleteItem(item.id).subscribe({
      next: () => {
        this.message.set('Item removido.');
        this.load();
      },
      error: () => this.message.set('Este item não pode ser removido enquanto estiver em leilão.'),
    });
  }
  openAuction(item?: Item): void {
    this.showItemForm = false;
    this.showAuctionForm = true;
    this.auction = {
      itemId: item?.id ?? '',
      startPrice: null,
      minIncrement: null,
      startDate: '',
      endDate: '',
    };
  }
  createAuction(): void {
    if (
      !this.auction.itemId ||
      !this.auction.startPrice ||
      !this.auction.minIncrement ||
      !this.auction.startDate ||
      !this.auction.endDate
    )
      return;
    this.submitting = true;
    this.marketplace
      .createAuction({
        ...this.auction,
        startPrice: this.auction.startPrice,
        minIncrement: this.auction.minIncrement,
      })
      .subscribe({
        next: () => {
          this.message.set('Leilão criado com sucesso.');
          this.showAuctionForm = false;
          this.submitting = false;
          this.load();
        },
        error: (e: HttpErrorResponse) => {
          this.message.set(e.error?.message ?? 'Confira as datas e os valores do leilão.');
          this.submitting = false;
        },
      });
  }
}
