package com.thiago.leiloa_api.dto.bid;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBidDTO(

    @NotNull
    UUID auctionId,

    @NotNull
    @Positive
    BigDecimal value
) {}
