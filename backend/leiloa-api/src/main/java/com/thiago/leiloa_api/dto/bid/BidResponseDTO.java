package com.thiago.leiloa_api.dto.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.bid.Bid;

public record BidResponseDTO(

    UUID id,
    BigDecimal value,
    LocalDateTime createdAt,

    UUID auctionId,
    UUID bidderId,
    String bidderName

) {

    public static BidResponseDTO fromEntity(Bid bid) {
        return new BidResponseDTO(
            bid.getId(),
            bid.getValue(),
            bid.getCreatedAt(),
            bid.getAuction().getId(),
            bid.getBidder().getId(),
            bid.getBidder().getName() 
        );
    }
}

