import { Component, HostListener, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { Notification } from '../../core/models/marketplace.model';
import {
  notificationDate,
  notificationIcon,
  notificationLabel,
} from '../../core/utils/notification-display';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-user-actions',
  imports: [RouterLink],
  templateUrl: './user-actions.html',
  styleUrl: './user-actions.scss',
})
export class UserActions implements OnInit {
  readonly auth = inject(AuthService);
  private readonly marketplace = inject(MarketplaceService);
  private readonly router = inject(Router);
  readonly isOpen = signal(false);
  readonly isNotificationsOpen = signal(false);
  readonly unreadCount = signal(0);
  readonly notificationPreview = signal<Notification[]>([]);
  readonly notificationsLoading = signal(true);
  readonly notificationLabel = notificationLabel;
  readonly notificationIcon = notificationIcon;
  readonly notificationDate = notificationDate;

  ngOnInit(): void {
    const userId = this.auth.session()?.userId;
    if (!userId) return;
    forkJoin({
      unread: this.marketplace.getUnreadNotifications(userId),
      recent: this.marketplace.getNotifications(userId),
    }).subscribe({
      next: ({ unread, recent }) => {
        this.unreadCount.set(unread.totalElements);
        this.notificationPreview.set(recent.content.slice(0, 5));
        this.notificationsLoading.set(false);
      },
      error: () => this.notificationsLoading.set(false),
    });
  }

  toggle(event: MouseEvent): void {
    event.stopPropagation();
    this.isNotificationsOpen.set(false);
    this.isOpen.update((open) => !open);
  }

  toggleNotifications(event: MouseEvent): void {
    event.stopPropagation();
    this.isOpen.set(false);
    this.isNotificationsOpen.update((open) => !open);
  }

  close(): void {
    this.isOpen.set(false);
    this.isNotificationsOpen.set(false);
  }

  logout(): void {
    this.close();
    this.auth.logout();
    void this.router.navigateByUrl('/');
  }

  @HostListener('document:click') onDocumentClick(): void {
    this.close();
  }
  @HostListener('document:keydown.escape') onEscape(): void {
    this.close();
  }
}
