package com.thiago.leiloa_api.dto.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thiago.leiloa_api.domain.bid.Bid;

public record BidPublicResponseDTO(
        BigDecimal value,
        LocalDateTime createdAt,
        String bidder
) {

    public static BidPublicResponseDTO fromEntity(Bid bid) {
        return new BidPublicResponseDTO(
                bid.getValue(),
                bid.getCreatedAt(),
                bid.getBidder().getName()
        );
    }
}

