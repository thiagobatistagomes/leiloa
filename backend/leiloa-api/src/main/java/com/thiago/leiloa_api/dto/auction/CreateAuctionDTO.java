package com.thiago.leiloa_api.dto.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record CreateAuctionDTO(

    @NotNull
    UUID itemId,

    @NotNull
    @Positive
    BigDecimal startPrice,

    @NotNull
    @Positive
    BigDecimal minIncrement,

    @NotNull
    @Future
    LocalDateTime startDate,

    @NotNull
    @Future
    LocalDateTime endDate
) {}
