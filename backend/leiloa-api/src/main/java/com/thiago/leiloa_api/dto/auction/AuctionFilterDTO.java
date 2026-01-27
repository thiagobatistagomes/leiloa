package com.thiago.leiloa_api.dto.auction;

import java.util.List;
import java.util.UUID;

import com.thiago.leiloa_api.domain.auction.AuctionStatus;

public record AuctionFilterDTO(
        List<AuctionStatus> statuses,
        UUID categoryId,
        String search
) {}

