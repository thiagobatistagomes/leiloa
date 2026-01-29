package com.thiago.leiloa_api.domain.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Construtor para criar um novo lance
    public Bid(Auction auction, User bidder, BigDecimal value) {
        this.auction = auction;
        this.bidder = bidder;
        this.value = value;
        this.createdAt = LocalDateTime.now();
    }

    // Valida o lance contra as regras do leilão
    public void validateAgainst(Auction auction) {
        if (value.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalStateException("O lance deve ser maior que o valor atual.");
        }

        BigDecimal minAllowed = auction
                .getCurrentPrice()
                .add(auction.getMinIncrement());

        if (value.compareTo(minAllowed) < 0) {
            throw new IllegalStateException("O lance não respeita o incremento mínimo.");
        }
    }
}

