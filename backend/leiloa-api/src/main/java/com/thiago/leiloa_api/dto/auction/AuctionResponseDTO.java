package com.thiago.leiloa_api.dto.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;

public record AuctionResponseDTO(
        UUID id,

        UUID itemId,
        String itemName,
        String itemImageUrl,

        UUID categoryId,
        String categoryName,

        BigDecimal startPrice,
        BigDecimal currentPrice,
        BigDecimal minIncrement,

        LocalDateTime startDate,
        LocalDateTime endDate,

        AuctionStatus status
) {

    public static AuctionResponseDTO fromEntity(Auction auction) {
        return new AuctionResponseDTO(
                auction.getId(),

                auction.getItem().getId(),
                auction.getItem().getName(),
                auction.getItem().getImageUrl(),

                auction.getItem().getCategory().getId(),
                auction.getItem().getCategory().getName(),

                auction.getStartPrice(),
                auction.getCurrentPrice(),
                auction.getMinIncrement(),

                auction.getStartDate(),
                auction.getEndDate(),

                auction.getStatus()
        );
    }
}
