import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuctionListItem } from '../../core/models/auction.model';
import {
  AdminDevice,
  AdminUser,
  Bid,
  Category,
  Comment,
  Delivery,
  Payment,
} from '../../core/models/marketplace.model';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

type AdminTab =
  'users' | 'payments' | 'deliveries' | 'auctions' | 'categories' | 'comments' | 'bids' | 'devices';

@Component({
  selector: 'app-admin-page',
  imports: [AppHeader, CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.scss',
})
export class AdminPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  readonly tab = signal<AdminTab>('users');
  readonly loading = signal(false);
  readonly feedback = signal('');
  readonly userStatus = signal('');
  readonly paymentStatus = signal('');
  readonly deliveryStatus = signal('');
  readonly commentStatus = signal('');
  readonly commentQuery = signal('');
  readonly categoryName = signal('');
  readonly users = signal<AdminUser[]>([]);
  readonly payments = signal<Payment[]>([]);
  readonly deliveries = signal<Delivery[]>([]);
  readonly auctions = signal<AuctionListItem[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly comments = signal<Comment[]>([]);
  readonly bids = signal<Bid[]>([]);
  readonly devices = signal<AdminDevice[]>([]);
  readonly tabs: Array<{ id: AdminTab; label: string }> = [
    { id: 'users', label: 'Usuários' },
    { id: 'payments', label: 'Pagamentos' },
    { id: 'deliveries', label: 'Entregas' },
    { id: 'auctions', label: 'Leilões' },
    { id: 'categories', label: 'Categorias' },
    { id: 'comments', label: 'Comentários' },
    { id: 'bids', label: 'Lances' },
    { id: 'devices', label: 'Dispositivos' },
  ];
  readonly totals = computed(() => ({
    users: this.users().length,
    payments: this.payments().length,
    deliveries: this.deliveries().length,
    comments: this.comments().length,
  }));

  ngOnInit(): void {
    this.refreshAll();
  }

  setTab(tab: AdminTab): void {
    this.tab.set(tab);
    this.feedback.set('');
  }

  refreshAll(): void {
    this.loading.set(true);
    let done = 0;
    const finish = () => {
      if (++done === 8) this.loading.set(false);
    };
    this.loadUsers(finish);
    this.loadPayments(finish);
    this.loadDeliveries(finish);
    this.loadAuctions(finish);
    this.loadCategories(finish);
    this.loadComments(finish);
    this.loadBids(finish);
    this.loadDevices(finish);
  }

  loadUsers(done?: () => void): void {
    this.marketplace.getAdminUsers({ status: this.userStatus() }).subscribe({
      next: (page) => this.users.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar usuarios.');
        done?.();
      },
      complete: done,
    });
  }

  loadPayments(done?: () => void): void {
    this.marketplace.getAdminPayments({ status: this.paymentStatus() }).subscribe({
      next: (page) => this.payments.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar pagamentos.');
        done?.();
      },
      complete: done,
    });
  }

  loadDeliveries(done?: () => void): void {
    this.marketplace.getAdminDeliveries({ status: this.deliveryStatus() }).subscribe({
      next: (page) => this.deliveries.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar entregas.');
        done?.();
      },
      complete: done,
    });
  }

  loadComments(done?: () => void): void {
    this.marketplace
      .getAdminComments({ status: this.commentStatus(), query: this.commentQuery() })
      .subscribe({
        next: (page) => this.comments.set(page.content),
        error: () => {
          this.warn('Nao foi possivel carregar comentarios.');
          done?.();
        },
        complete: done,
      });
  }

  loadAuctions(done?: () => void): void {
    this.marketplace.getAuctions({ status: ['ACTIVE', 'SCHEDULED'], page: 0, size: 30 }).subscribe({
      next: (page) => this.auctions.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar leiloes.');
        done?.();
      },
      complete: done,
    });
  }

  loadCategories(done?: () => void): void {
    this.marketplace.getCategories().subscribe({
      next: (categories) => this.categories.set(categories),
      error: () => {
        this.warn('Nao foi possivel carregar categorias.');
        done?.();
      },
      complete: done,
    });
  }

  loadBids(done?: () => void): void {
    this.marketplace.getAdminBids().subscribe({
      next: (page) => this.bids.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar lances.');
        done?.();
      },
      complete: done,
    });
  }

  loadDevices(done?: () => void): void {
    this.marketplace.getAdminDevices().subscribe({
      next: (page) => this.devices.set(page.content),
      error: () => {
        this.warn('Nao foi possivel carregar dispositivos.');
        done?.();
      },
      complete: done,
    });
  }

  toggleUser(user: AdminUser): void {
    const request =
      user.status === 'BLOCKED'
        ? this.marketplace.activateUser(user.id)
        : this.marketplace.blockUser(user.id);
    request.subscribe({
      next: () => {
        this.ok(user.status === 'BLOCKED' ? 'Usuario reativado.' : 'Usuario bloqueado.');
        this.loadUsers();
      },
      error: () => this.warn('A acao no usuario falhou.'),
    });
  }

  cancelPayment(payment: Payment): void {
    this.marketplace.cancelAdminPayment(payment.id).subscribe({
      next: () => {
        this.ok('Pagamento cancelado.');
        this.loadPayments();
      },
      error: () => this.warn('Nao foi possivel cancelar o pagamento.'),
    });
  }

  expirePayments(): void {
    this.marketplace.expirePendingPayments().subscribe({
      next: (total) => {
        this.ok(`${total} pagamento(s) expirado(s).`);
        this.loadPayments();
      },
      error: () => this.warn('Nao foi possivel expirar pagamentos pendentes.'),
    });
  }

  updateDelivery(delivery: Delivery, status: string): void {
    if (!status || status === delivery.status) return;
    this.marketplace.updateDeliveryStatus(delivery.id, status).subscribe({
      next: () => {
        this.ok('Entrega atualizada.');
        this.loadDeliveries();
      },
      error: () => this.warn('A transicao de entrega nao foi aceita.'),
    });
  }

  finishAuction(auction: AuctionListItem): void {
    this.marketplace.finishAuction(auction.auctionId).subscribe({
      next: () => {
        this.ok('Leilao finalizado.');
        this.loadAuctions();
        this.loadPayments();
      },
      error: () => this.warn('Nao foi possivel finalizar o leilao.'),
    });
  }

  saveCategory(category?: Category): void {
    const name = (category?.name ?? this.categoryName()).trim();
    if (!name) return;
    const request = category
      ? this.marketplace.updateCategory(category.id, name)
      : this.marketplace.createCategory(name);
    request.subscribe({
      next: () => {
        this.categoryName.set('');
        this.ok(category ? 'Categoria atualizada.' : 'Categoria criada.');
        this.loadCategories();
      },
      error: () => this.warn('Nao foi possivel salvar a categoria.'),
    });
  }

  deleteCategory(category: Category): void {
    this.marketplace.deleteCategory(category.id).subscribe({
      next: () => {
        this.ok('Categoria removida.');
        this.loadCategories();
      },
      error: () => this.warn('A categoria pode estar associada a itens.'),
    });
  }

  toggleComment(comment: Comment): void {
    const request =
      comment.status === 'BLOCKED'
        ? this.marketplace.unblockComment(comment.id)
        : this.marketplace.blockComment(comment.id);
    request.subscribe({
      next: () => {
        this.ok(
          comment.status === 'BLOCKED' ? 'Comentario desbloqueado.' : 'Comentario bloqueado.',
        );
        this.loadComments();
      },
      error: () => this.warn('Nao foi possivel alterar o comentario.'),
    });
  }

  statusLabel(status: string | undefined): string {
    return (
      (
        {
          ACTIVE: 'Ativo',
          INACTIVE: 'Inativo',
          BLOCKED: 'Bloqueado',
          PENDING: 'Pendente',
          COMPLETED: 'Pago',
          EXPIRED: 'Expirado',
          CANCELLED: 'Cancelado',
          SCHEDULED: 'Agendado',
          SOLD: 'Vendido',
          VISIBLE: 'Visivel',
          HIDDEN: 'Oculto',
          DELETED: 'Excluido',
          PROCESSING: 'Em preparo',
          SHIPPED: 'Enviado',
          IN_TRANSIT: 'Em transito',
          OUT_FOR_DELIVERY: 'Saiu para entrega',
          DELIVERED: 'Entregue',
          RETURN_REQUESTED: 'Devolucao solicitada',
          RETURNED: 'Devolvido',
        } as Record<string, string>
      )[status ?? ''] ??
      status ??
      'Sem status'
    );
  }

  shortId(id: string | undefined): string {
    return id ? id.slice(0, 8) : 'sem ID';
  }

  private ok(message: string): void {
    this.feedback.set(message);
  }

  private warn(message: string): void {
    this.feedback.set(message);
  }
}
