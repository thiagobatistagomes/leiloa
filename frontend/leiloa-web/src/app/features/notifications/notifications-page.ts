import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Notification } from '../../core/models/marketplace.model';
import { AuthService } from '../../core/services/auth.service';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';
import {
  notificationDate,
  notificationIcon,
  notificationLabel,
} from '../../core/utils/notification-display';

@Component({
  selector: 'app-notifications-page',
  imports: [AppHeader, RouterLink],
  templateUrl: './notifications-page.html',
  styleUrl: './notifications-page.scss',
})
export class NotificationsPage implements OnInit {
  private readonly marketplace = inject(MarketplaceService);
  private readonly auth = inject(AuthService);
  readonly notifications = signal<Notification[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly notificationLabel = notificationLabel;
  readonly notificationIcon = notificationIcon;
  readonly notificationDate = notificationDate;
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    const id = this.auth.session()!.userId;
    this.marketplace.getNotifications(id).subscribe({
      next: (page) => {
        this.notifications.set(page.content);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar as notificações.');
        this.loading.set(false);
      },
    });
  }
  read(item: Notification): void {
    if (item.readAt) return;
    this.marketplace.readNotification(item.id, this.auth.session()!.userId).subscribe({
      next: () =>
        this.notifications.update((all) =>
          all.map((entry) =>
            entry.id === item.id ? { ...entry, readAt: new Date().toISOString() } : entry,
          ),
        ),
    });
  }
  readAll(): void {
    this.marketplace.readAllNotifications(this.auth.session()!.userId).subscribe({
      next: () =>
        this.notifications.update((all) =>
          all.map((item) => ({ ...item, readAt: item.readAt ?? new Date().toISOString() })),
        ),
    });
  }
  auctionId(item: Notification): string | null {
    const value = item.data?.['auctionId'];
    return typeof value === 'string' ? value : null;
  }
}
