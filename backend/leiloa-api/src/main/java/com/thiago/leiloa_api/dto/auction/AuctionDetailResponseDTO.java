package com.thiago.leiloa_api.dto.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.auction.AuctionStatus;

public record AuctionDetailResponseDTO(
        UUID auctionId,

        BigDecimal startPrice,
        BigDecimal currentPrice,
        BigDecimal minIncrement,

        LocalDateTime startDate,
        LocalDateTime endDate,
        AuctionStatus status,

        UUID itemId,
        String itemName,
        String itemDescription,
        String imageUrl,

        UUID sellerId,
        String sellerName
) {}
