package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.payment.PaymentStatus;

public record PaymentDTO(
    UUID id,
    UUID auctionId,
    UUID winnerId,
    BigDecimal value,
    PaymentStatus status,
    LocalDateTime createdAt,
    LocalDateTime paidAt,
    LocalDateTime expiredAt
) {}
