package com.thiago.leiloa_api.domain.comment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false, updatable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ADMIN → BLOCKED
    public void block() {
        this.status = CommentStatus.BLOCKED;
    }

    // ADMIN → VISIBLE
    public void unblock() {
        this.status = CommentStatus.VISIBLE;
    }

    // USER → HIDDEN
    public void hide() {
        this.status = CommentStatus.HIDDEN;
    }

    // USER → VISIBLE
    public void unhide() {
        this.status = CommentStatus.VISIBLE;
    }

    // USER → DELETED (soft delete irreversível)
    public void deleteUser() {
        this.status = CommentStatus.DELETED;
    }

    // Verifica autoria
    public boolean isAuthor(User user) {
        return this.user != null && this.user.getId().equals(user.getId());
    }
}