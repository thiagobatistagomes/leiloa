package com.thiago.leiloa_api.domain.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.thiago.leiloa_api.domain.item.Item;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "auctions")
@Getter
@Setter
@NoArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Item que está sendo leiloado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Usuário que criou o leilão (dono do item)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Preço inicial do leilão (imutável)
    @Column(name = "start_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal startPrice;

    // Incremento mínimo entre lances
    @Column(name = "min_increment", nullable = false, precision = 12, scale = 2)
    private BigDecimal minIncrement;

    // Preço atual do leilão (derivado dos lances)
    @Column(name = "current_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_bidder_id")
    private User lastBidder;


    public boolean isOwnedBy(User user) {
        return this.user.getId().equals(user.getId());
    }

    public boolean isActive() {
        return this.status == AuctionStatus.ACTIVE;
    }

    public boolean isScheduled() {
        return this.status == AuctionStatus.SCHEDULED;
    }

    public boolean canBeCancelled() {
        return status == AuctionStatus.ACTIVE || status == AuctionStatus.SCHEDULED;
    }

    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Leilão não pode ser cancelado.");
        }
        this.status = AuctionStatus.CANCELED;
    }

    public void finish() {
        if (status != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Leilão não pode ser finalizado.");
        }

        if (LocalDateTime.now().isBefore(endDate)) {
            throw new IllegalStateException("Leilão ainda não chegou ao fim.");
        }

        this.status = AuctionStatus.FINISHED;
    }

    public boolean isPubliclyVisible() {
        return status == AuctionStatus.ACTIVE || status == AuctionStatus.SCHEDULED;
    }

    public void updateAfterBid(BigDecimal newPrice, User bidder) {
        this.currentPrice = newPrice;
        this.lastBidder = bidder;
    }


    public UUID getWinnerId() {
        if (status != AuctionStatus.FINISHED || lastBidder == null) {
            return null;
        }
        return lastBidder.getId();
    }

    public BigDecimal getWinningBid() {
        if (status != AuctionStatus.FINISHED || lastBidder == null) {
            throw new IllegalStateException("Leilão não finalizado ou sem vencedor.");
        }
        return currentPrice;
    }

    public boolean isFinished() {
        return this.status == AuctionStatus.FINISHED;
    }


}

