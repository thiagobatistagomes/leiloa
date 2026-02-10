package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private UUID id;
    private UUID auctionId;
    private UUID winnerId;
    private BigDecimal value;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime expiredAt;

    public static PaymentResponseDTO fromEntity(Payment p) {
        return PaymentResponseDTO.builder()
                .id(p.getId())
                .auctionId(p.getAuction().getId())
                .winnerId(p.getWinner().getId())
                .value(p.getValue())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .expiredAt(p.getExpiredAt())
                .build();
    }
}

