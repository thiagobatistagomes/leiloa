import { CurrencyPipe, DatePipe, NgTemplateOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuctionDetail } from '../../core/models/auction.model';
import { Bid, Comment } from '../../core/models/marketplace.model';
import { AuthService } from '../../core/services/auth.service';
import { MarketplaceService } from '../../core/services/marketplace.service';
import { AppHeader } from '../../shared/app-header/app-header';

@Component({
  selector: 'app-auction-detail-page',
  imports: [AppHeader, CurrencyPipe, DatePipe, FormsModule, NgTemplateOutlet, RouterLink],
  templateUrl: './auction-detail-page.html',
  styleUrl: './auction-detail-page.scss',
})
export class AuctionDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly marketplace = inject(MarketplaceService);
  readonly auth = inject(AuthService);
  readonly auction = signal<AuctionDetail | null>(null);
  readonly bids = signal<Bid[]>([]);
  readonly comments = signal<Comment[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly actionMessage = signal('');
  readonly minimumBid = computed(
    () => (this.auction()?.currentPrice ?? 0) + (this.auction()?.minIncrement ?? 0),
  );
  readonly activeReplyId = signal<string | null>(null);
  readonly replyDraft = signal('');
  bidValue: number | null = null;
  comment = '';
  submittingBid = false;
  submittingComment = false;
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.loading.set(true);
    this.marketplace.getAuction(id).subscribe({
      next: (auction) => {
        this.auction.set(auction);
        this.bidValue = auction.currentPrice + auction.minIncrement;
        this.loading.set(false);
        this.marketplace.getBids(id).subscribe({ next: (page) => this.bids.set(page.content) });
        if (this.auth.isAuthenticated()) this.loadComments();
      },
      error: () => {
        this.error.set('Este leilão não está disponível ou não foi encontrado.');
        this.loading.set(false);
      },
    });
  }
  placeBid(): void {
    const auction = this.auction();
    if (!auction || !this.bidValue) return;
    this.submittingBid = true;
    this.actionMessage.set('');
    this.marketplace.placeBid(auction.auctionId, this.bidValue).subscribe({
      next: () => {
        this.actionMessage.set('Lance registrado com sucesso.');
        this.submittingBid = false;
        this.load();
      },
      error: (error: HttpErrorResponse) => {
        this.actionMessage.set(
          error.error?.message ??
            'Não foi possível registrar o lance. Confira o valor e tente novamente.',
        );
        this.submittingBid = false;
      },
    });
  }
  submitComment(parentCommentId: string | null = null): void {
    const auction = this.auction();
    if (!auction || !this.comment.trim()) return;
    this.submittingComment = true;
    this.marketplace
      .createComment(auction.auctionId, this.comment.trim(), parentCommentId)
      .subscribe({
        next: () => {
          this.comment = '';
          this.submittingComment = false;
          this.loadComments();
        },
        error: () => {
          this.actionMessage.set('Não foi possível publicar o comentário.');
          this.submittingComment = false;
        },
      });
  }
  toggleReply(commentId: string): void {
    const opening = this.activeReplyId() !== commentId;
    this.activeReplyId.set(opening ? commentId : null);
    this.replyDraft.set('');
  }
  submitReply(parentCommentId: string): void {
    const auction = this.auction();
    const content = this.replyDraft().trim();
    if (!auction || !content) return;
    this.submittingComment = true;
    this.marketplace.createComment(auction.auctionId, content, parentCommentId).subscribe({
      next: () => {
        this.activeReplyId.set(null);
        this.replyDraft.set('');
        this.submittingComment = false;
        this.loadComments();
      },
      error: () => {
        this.actionMessage.set('Não foi possível publicar a resposta.');
        this.submittingComment = false;
      },
    });
  }
  deleteComment(comment: Comment): void {
    if (!confirm('Excluir este comentário?')) return;
    this.marketplace.deleteComment(comment.id).subscribe({ next: () => this.loadComments() });
  }
  isMine(comment: Comment): boolean {
    return comment.userId === this.auth.session()?.userId;
  }
  initial(name: string): string {
    return name.trim().slice(0, 1).toUpperCase();
  }
  private loadComments(): void {
    this.marketplace
      .getComments(this.route.snapshot.paramMap.get('id')!)
      .subscribe({ next: (value) => this.comments.set(value) });
  }
}
