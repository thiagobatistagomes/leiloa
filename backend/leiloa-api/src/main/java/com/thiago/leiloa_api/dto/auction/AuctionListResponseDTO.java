package com.thiago.leiloa_api.dto.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.auction.AuctionStatus;

public record AuctionListResponseDTO(
        UUID auctionId,
        UUID itemId,
        String itemName,
        String imageUrl,
        BigDecimal currentPrice,
        LocalDateTime startDate,
        LocalDateTime endDate,
        AuctionStatus status
) {
        public static AuctionListResponseDTO fromEntity(com.thiago.leiloa_api.domain.auction.Auction auction) {
            return new AuctionListResponseDTO(
                    auction.getId(),
                    auction.getItem().getId(),
                    auction.getItem().getName(),
                    auction.getItem().getImageUrl(),
                    auction.getCurrentPrice(),
                    auction.getStartDate(),
                    auction.getEndDate(),
                    auction.getStatus()
            );
        }
}

