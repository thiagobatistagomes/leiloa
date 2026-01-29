package com.thiago.leiloa_api.dto.bid;

import java.math.BigDecimal;
import java.util.UUID;

public record BidRequestDTO(
    UUID auctionId,
    BigDecimal value
) {}

